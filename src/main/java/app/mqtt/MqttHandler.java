package app.mqtt;

import app.device.DeviceMqttControllerFacade;
import io.moquette.server.Server;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class MqttHandler implements MqttCallback {

	private static final String MOQUETTE_CONF_DIR = "/src/main/resources/conf/moquette.conf";
	private static String broker = "tcp://localhost:1883";
	private static String clientId = "HomeServer";
	private static MemoryPersistence persistence = new MemoryPersistence();

	private static final Logger logger = LoggerFactory.getLogger(MqttHandler.class);

	private static final String DEVICE_ACTIVATE = "activate";

	public MqttHandler() throws MqttException, IOException {
		Server server = new Server();
		server.startServer(new File(System.getProperty("user.dir") + MOQUETTE_CONF_DIR));

		MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		mqttClient.setCallback(this);
		mqttClient.connect(connOpts);

		mqttClient.subscribe(DEVICE_ACTIVATE);
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
				DeviceMqttControllerFacade.activate(payload);
				break;
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}
}
