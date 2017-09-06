package app.device;

import app.ac.AcDao;
import app.ac.AcState;
import app.mqtt.MqttHandler;
import app.util.JsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceMqttControllerFacade {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private AcDao acDao;

	@Autowired
	private DeviceController deviceController;

	@Autowired
	private MqttHandler mqttHandler;

	private static final Logger logger = LoggerFactory.getLogger(DeviceMqttControllerFacade.class);

	public void activate(String payload) {
		Device device = extractDevice(payload);
		logger.info("Activating " + device.getDeviceId() + " @ " + device.getAddress());
		if (deviceController.activate(device)) {
			AcState state = acDao.getCurrentState(device.getDeviceId());
			String json = JsonTransformer.toJson(state);
			mqttHandler.asyncPublishMessage("ac/overwrite/" + device.getDeviceId(), json);
			logger.info("Activation successful");
		} else {
			logger.error("Failed to activate.");
		}
	}

	public void reconnect(String payload) {
		Device device = extractDevice(payload);
		if (deviceController.reconnect(device)) {
			logger.info("Reconnection successful");
		} else {
			logger.error("Failed to log reconnection.");
		}
	}

	private Device extractDevice(String payload) {
		String deviceId = "";
		String ip = "";
		for (String map : payload.split(",")) {
			String[] kv = map.split(":");
			String k = kv[0];
			String v = kv[1];
			switch (k) {
				case "name":
					deviceId = v;
					break;
				case "ip":
					ip = v;
					break;
			}
		}
		Device d = new Device();
		d.setAddress(ip);
		d.setDeviceId(deviceId);
		return d;
	}
}
