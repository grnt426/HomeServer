package app.ambiance;

import app.util.DateHandler;
import app.util.JsonTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmbientMqttControllerFacade {

	@Autowired
	private AmbientDao ambientDao;

	public void syncDeviceState(String deviceId, String payload) {
		AmbientState state = JsonTransformer.fromJson(payload, AmbientState.class);
		state.setDeviceId(deviceId);
		state.setEventTime(DateHandler.getDateTimeNow());
		ambientDao.syncDeviceState(state);
	}
}
