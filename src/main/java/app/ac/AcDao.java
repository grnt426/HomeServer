package app.ac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AcDao {

	@Autowired
	private Sql2o sql2o;

	Logger logger = LoggerFactory.getLogger(AcDao.class);

	public AcState getCurrentState(String deviceId) {
		AcState state = AcState.EMPTY_REQUEST;
		try (Connection conn = sql2o.open()) {
			state = conn.createQuery("SELECT * FROM DeviceAcState WHERE deviceId = :deviceId")
					.addParameter("deviceId", deviceId)
					.executeAndFetchFirst(AcState.class);
			if (state == null) {
				state = AcState.EMPTY_REQUEST;
			}
		} catch (Exception e) {
			logger.error("Error retrieving statuses.", e);
		}
		return state;
	}

	public void syncDeviceState(AcState state) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery("INSERT OR REPLACE INTO DeviceAcState " +
					"(deviceId, powered, temperature, fanSpeed, mode) " +
					"VALUES (:deviceId, :powered, :temperature, :fanSpeed, :mode)")
					.bind(state)
					.executeUpdate();
		} catch (Exception e) {
			logger.error("Unable to sync device state with database!");
		}
	}

	public List<AcState> getAllAcStates() {
		List<AcState> states = new ArrayList<>();
		try (Connection conn = sql2o.open()) {
			states = conn.createQuery("SELECT * FROM DeviceAcState")
					.executeAndFetch(AcState.class);
		} catch (Exception e) {
			logger.error("Unable to retrieve AC states");
		}
		return states;
	}
}
