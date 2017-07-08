package app.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceMqttControllerFacade {

	private static final Logger logger = LoggerFactory.getLogger(DeviceMqttControllerFacade.class);

	public static void activate(String payload) {
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
		logger.info("Activating " + deviceId + " @ " + ip);
		if (DeviceController.activate(deviceId, ip)) {
			logger.info("Activation successful");
		} else {
			logger.error("Failed to activate.");
		}
	}
}
