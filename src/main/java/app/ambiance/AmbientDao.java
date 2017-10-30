package app.ambiance;

import app.device.Device;
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
			List<Device> sensors = conn.createQuery("SELECT * FROM Device WHERE capabilityId = :capabilityId")
					.addParameter("capabilityId", 222)
					.executeAndFetch(Device.class);
			for (Device sensor : sensors) {
				states.add(conn.createQuery("SELECT * FROM AmbientStateHistory WHERE deviceId = :deviceId ORDER BY eventTime DESC LIMIT 1")
						.addParameter("deviceId", sensor.getDeviceId())
						.executeAndFetchFirst(AmbientState.class));
			}
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
