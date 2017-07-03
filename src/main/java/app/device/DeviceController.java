package app.device;

import app.util.DateHandler;
import app.util.Status;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;

import static app.Application.deviceDao;

public class DeviceController {
	public static Route status = (Request request, Response response) -> {
		return new ArrayList<Object>();
	};

	public static Route statusAll = (Request request, Response response) -> deviceDao.getStatusOfAllDevices();

	/**
	 * Receives the initial connection from the device.
	 */
	public static Route activate = (Request request, Response response) -> {
		Device device = new Device();
		device.setDeviceId(request.params(":deviceId"));
		device.setAddress(request.ip());
		boolean success = true;

		if((success &= deviceDao.registerDevice(device))){
			success &= deviceDao.updateStatus(buildStatus(device, Status.REGISTERED));
		} else{
			success &= deviceDao.updateStatus(buildStatus(device, Status.REGISTER_FAILED));
		}

		if(success){
			response.status(200);
		} else{
			response.status(503);
		}

		return null;
	};

	public static Route heartbeat = (Request request, Response response) -> {
		response.status(200);
		return null;
	};

	private static DeviceStatus buildStatus(Device device, Status status) {
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDeviceId(device.getDeviceId());
		deviceStatus.setDate(DateHandler.getDateTimeNow());
		deviceStatus.setStatus(status.name());
		return deviceStatus;
	}
}
