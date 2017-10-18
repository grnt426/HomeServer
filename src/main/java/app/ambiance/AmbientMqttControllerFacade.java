package app.ambiance;

import app.util.JsonTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class AmbientMqttControllerFacade {

	@Autowired
	private AmbientDao ambientDao;

	public void syncDeviceState(String deviceId, String payload) {
		AmbientState state = JsonTransformer.fromJson(payload, AmbientState.class);
		state.setDeviceID(deviceId);
		state.setEventTime(LocalDateTime.now().format(
				DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.ENGLISH)));
		ambientDao.syncDeviceState(state);
	}
}
