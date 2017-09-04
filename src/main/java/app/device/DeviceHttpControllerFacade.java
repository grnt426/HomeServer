package app.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeviceHttpControllerFacade {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private DeviceController deviceController;

	public Route status = (Request request, Response response) -> {
		return new ArrayList<Object>();
	};

	public Route statusAll = (Request request, Response response) -> deviceDao.getStatusOfAllDevices();

	public Route activate = (Request request, Response response) -> {
		boolean success = deviceController.activate(new Device(request.params(":deviceId"), request.ip()));

		if (success) {
			response.status(200);
		} else {
			response.status(503);
		}

		return null;
	};

	public List<DeviceStatus> getAllDeviceStatus() {
		return deviceDao.getStatusOfAllDevices();
	}

	public Map<String, DeviceCapability> getDeviceCapabilityAsMap() {
		return deviceDao.getAllDeviceCapabilities().stream().collect(Collectors.toMap(DeviceCapability::getDeviceId,
				d -> d));
	}
}
