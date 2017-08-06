package app.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceMqttControllerFacade {

	@Autowired
	private DeviceDao deviceDao;

	@Autowired
	private DeviceController deviceController;

	private static final Logger logger = LoggerFactory.getLogger(DeviceMqttControllerFacade.class);

	public void activate(String payload) {
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
		if (deviceController.activate(deviceId, ip)) {
			logger.info("Activation successful");
		} else {
			logger.error("Failed to activate.");
		}
	}
}
