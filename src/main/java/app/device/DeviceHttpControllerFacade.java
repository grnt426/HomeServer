package app.device;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;

import static app.Application.deviceDao;

public class DeviceHttpControllerFacade {
	public static Route status = (Request request, Response response) -> {
		return new ArrayList<Object>();
	};

	public static Route statusAll = (Request request, Response response) -> deviceDao.getStatusOfAllDevices();

	public static Route activate = (Request request, Response response) -> {
		boolean success = DeviceController.activate(request.params(":deviceId"), request.ip());

		if (success) {
			response.status(200);
		} else {
			response.status(503);
		}

		return null;
	};
}
