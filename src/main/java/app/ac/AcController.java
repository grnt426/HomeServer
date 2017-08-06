package app.ac;

import app.mqtt.MqttHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;
import spark.Route;

@Service
public class AcController {

	@Autowired
	private MqttHandler mqttHandler;

	private static final Logger logger = LoggerFactory.getLogger(AcController.class);

	public Route turnOff = (Request request, Response response) -> {
		return "";
	};

	public Route turnOn = (Request request, Response response) -> {
//		Application.mqttHandler.publishMessage("ac/ac_alpha", AcFlashCode.ON_OFF);
		mqttHandler.publishMessage("ac/ac_alpha", "1");
		response.status(200);
		return "";
	};

	private String mapnameToDevice(String name) {
		return "ac/ac_alpha";
	}

	private void sendSequence(AcFlashCode... codes) {

	}
}
