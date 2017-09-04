package app.device;

import app.mqtt.MqttHandler;
import app.util.DateHandler;
import app.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceController {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private MqttHandler mqttHandler;

	/**
	 * Receives the initial connection from the device.
	 */
	public boolean activate(Device device) {
		boolean success;
		if (success = deviceDao.registerDevice(device)) {
			success = deviceDao.updateStatus(buildStatus(device, Status.REGISTERED));
		} else {
			deviceDao.updateStatus(buildStatus(device, Status.REGISTER_FAILED));
		}

		return success;
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
}
