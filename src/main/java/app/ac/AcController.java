package app.ac;

import app.Application;
import spark.Request;
import spark.Response;
import spark.Route;

public class AcController {

	public static Route turnOff = (Request request, Response response) -> {
		return "";
	};

	public static Route turnOn = (Request request, Response response) -> {
		Application.mqttHandler.publishMessage("ac/ac_alpha", "1");
		response.status(200);
		return "";
	};
}
