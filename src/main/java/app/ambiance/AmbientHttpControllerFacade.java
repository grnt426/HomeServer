package app.ambiance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AmbientHttpControllerFacade {

	@Autowired
	AmbientDao ambientDao;

	public Map<String, AmbientState> getMostRecentAmbientStatesAsMap() {
		return ambientDao.getMostRecentAmbientStates()
				.stream().collect(Collectors.toMap(AmbientState::getDeviceId, a -> a));
	}

	;
}
