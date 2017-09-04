package app.ac;

import app.util.JsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AcMqttControllerFacade {

	@Autowired
	private AcDao acDao;

	private static final Logger logger = LoggerFactory.getLogger(AcMqttControllerFacade.class);

	public void syncDeviceState(String deviceId, String payload) {
		AcState state = JsonTransformer.fromJson(payload, AcState.class);
		state.setDeviceId(deviceId);
		logger.info("State to sync: " + state);
		acDao.syncDeviceState(state);
	}
}
