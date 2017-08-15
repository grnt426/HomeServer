package app.ac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

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
}
