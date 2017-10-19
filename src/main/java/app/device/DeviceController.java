package app.device;

import app.mqtt.MqttHandler;
import app.util.DateHandler;
import app.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceController {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private MqttHandler mqttHandler;

	/**
	 * Registers the device for the first time, before it comes online.
	 */
	public boolean register(Device device) {
		boolean success;
		if (success = deviceDao.registerDevice(device)) {
			success = deviceDao.updateStatus(buildStatus(device, Status.REGISTERED));
		} else {
			deviceDao.updateStatus(buildStatus(device, Status.REGISTER_FAILED));
		}

		return success;
	}

	/**
	 * When a device comes online for the first time, and expects to stay online.
	 */
	public boolean activate(Device device) {
		return deviceDao.updateStatus(buildStatus(device, Status.ON));
	}


	public boolean reconnect(Device device) {
		boolean success;
		if (success = deviceDao.registerDevice(device)) {
			success = deviceDao.updateStatus(buildStatus(device, Status.RECONNECTED));
		} else {
			deviceDao.updateStatus(buildStatus(device, Status.RECONNECTED_FAILED));
		}

		return success;
	}

	private DeviceStatus buildStatus(Device device, Status status) {
		DeviceStatus deviceStatus = new DeviceStatus();
		deviceStatus.setDeviceId(device.getDeviceId());
		deviceStatus.setDate(DateHandler.getDateTimeNow());
		deviceStatus.setStatus(status.name());
		return deviceStatus;
	}

	public List<Device> getAllDevices() {
		return deviceDao.getAllDevices();
	}
}
