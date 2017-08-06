package app.heartbeat;

import app.util.DateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Repository
public class HeartbeatDao {

	private static String HEARTBEAT = "INSERT INTO Heartbeat(deviceId, date) VALUES(:deviceId, :date)";
	private final Logger logger = LoggerFactory.getLogger(HeartbeatDao.class);

	@Autowired
	private Sql2o sql2o;

	public void heartbeat(String deviceId) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(HEARTBEAT)
					.addParameter("deviceId", deviceId)
					.addParameter("date", DateHandler.getDateTimeNow())
					.executeUpdate();
		} catch(Exception e){
			logger.error("Something failed with the heartbeat write. Suppressing error...");
		}
	}
}
