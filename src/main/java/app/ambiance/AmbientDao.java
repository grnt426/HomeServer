package app.ambiance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AmbientDao {

	@Autowired
	private Sql2o sql2o;

	private final Logger logger = LoggerFactory.getLogger(AmbientDao.class);

	public List<AmbientState> getMostRecentAmbientStates() {
		List<AmbientState> states = new ArrayList<>();
		try (Connection conn = sql2o.open()) {
			states = conn.createQuery("SELECT a1.* FROM AmbientStateHistory a1 " +
					"LEFT JOIN AmbientStateHistory a2 ON (a1.deviceId = a2.deviceId AND a1.eventTime < a2.eventTime) " +
					"WHERE a2.eventTime IS NULL")
					.executeAndFetch(AmbientState.class);
		} catch (Exception e) {
			logger.error("Error retrieving AmbientStateHistory", e);
		}
		return states;
	}

	public void syncDeviceState(AmbientState state) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery("INSERT INTO AmbientStateHistory" +
					"(deviceId, temperature, humidity, lightLevel, eventTime)" +
					"VALUES (:deviceId, :temperature, :humidity, :lightLevel, :eventTime)")
					.bind(state)
					.executeUpdate();
		} catch (Exception e) {
			logger.error("Could not write ambient device state.", e);
		}
	}
}
