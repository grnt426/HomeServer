package app.mqtt;

import app.ac.AcMqttControllerFacade;
import app.device.DeviceMqttControllerFacade;
import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class MqttHandler implements MqttCallback {

	@Autowired
	private DeviceMqttControllerFacade deviceMqttControllerFacade;

	@Autowired
	private AcMqttControllerFacade acMqttControllerFacade;

	private static final String MOQUETTE_CONF_DIR = "/resources/conf/moquette.conf";
	private static final String broker = "tcp://localhost:1883";
	private static final String clientId = "HomeServer";
	private static final MemoryPersistence persistence = new MemoryPersistence();
	private static final Logger logger = LoggerFactory.getLogger(MqttHandler.class);
	private static final String DEVICE_ACTIVATE = "activate";
	private static final String DEVICE_RECONNECT = "reconnect";
	private static final String DEVICE_SYNC = "ac/sync/+";

	private final MqttClient mqttClient = new MqttClient(broker, clientId, persistence);

	private final MqttAsyncPublisher asyncPublisher;

	public MqttHandler() throws MqttException, IOException {
		Server server = new Server();
		server.startServer(new File(System.getProperty("user.dir") + MOQUETTE_CONF_DIR));

		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		asyncPublisher = new MqttAsyncPublisher(this);
		new Thread(asyncPublisher).start();

		mqttClient.setCallback(this);
		mqttClient.connect(connOpts);

		mqttClient.subscribe(DEVICE_ACTIVATE);
		mqttClient.subscribe(DEVICE_RECONNECT);
		mqttClient.subscribe(DEVICE_SYNC);
	}

	public void publishMessage(String topic, String message) {
		try {
			logger.info("[" + topic + "]: " + message);
			mqttClient.publish(topic, message.getBytes(), 0, false);
		} catch (MqttException e) {
			logger.error("Failed to send a message. [" + topic + "] " + message, e);
		}
	}

	public void publishCommandSequence(String topic, List<String> commands) {
		commands.forEach(c -> publishMessage(topic, c));
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO: This should happen unless the underlying MQTT broker died
		logger.error("MQTT Broker may have died???", cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String payload = new String(message.getPayload(), "UTF-8");
		logger.info("Message received [" + topic + "] : " + payload);
		String subTopic = topic;
		String lastFilter = topic;
		if (topic.contains("/")) {
			subTopic = topic.substring(0, topic.lastIndexOf("/"));
			logger.info("Subtopic: " + subTopic);
			lastFilter = topic.substring(topic.lastIndexOf("/") + 1);
		}

		switch (subTopic) {
			case DEVICE_ACTIVATE:
				deviceMqttControllerFacade.activate(payload);
				break;
			case DEVICE_RECONNECT:
				deviceMqttControllerFacade.reconnect(payload);
				break;
			case "ac/sync":
				logger.info("Sync message [" + topic + "] for: " + payload);
				acMqttControllerFacade.syncDeviceState(lastFilter, payload);
				break;
			default:
				logger.info("Unmapped topic [" + topic + "]: " + payload);
				break;
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public void asyncPublishMessage(String topic, String message) {
		asyncPublisher.addMessage(topic, message);
	}
}
