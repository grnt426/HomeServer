package app.mqtt;

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
import java.util.Base64;
import java.util.List;

@Component
public class MqttHandler implements MqttCallback {

	@Autowired
	private DeviceMqttControllerFacade deviceMqttControllerFacade;

	private static final String MOQUETTE_CONF_DIR = "/resources/conf/moquette.conf";
	private static final String broker = "tcp://localhost:1883";
	private static final String clientId = "HomeServer";
	private static final MemoryPersistence persistence = new MemoryPersistence();
	private static final Logger logger = LoggerFactory.getLogger(MqttHandler.class);
	private static final String DEVICE_ACTIVATE = "activate";

	private final MqttClient mqttClient = new MqttClient(broker, clientId, persistence);

	public MqttHandler() throws MqttException, IOException {
		Server server = new Server();
		server.startServer(new File(System.getProperty("user.dir") + MOQUETTE_CONF_DIR));

		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		mqttClient.setCallback(this);
		mqttClient.connect(connOpts);

		mqttClient.subscribe(DEVICE_ACTIVATE);
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

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.debug("Message received [" + topic + "] : " + message.getPayload());
		String payload = "";
		byte[] imagePayload = null;
		if (topic.contains("image")) {
			imagePayload = Base64.getDecoder().decode(message.getPayload());
		} else {
			payload = new String(message.getPayload(), "UTF-8");
		}

		switch (topic) {
			case DEVICE_ACTIVATE:
				deviceMqttControllerFacade.activate(payload);
				break;
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}
}
