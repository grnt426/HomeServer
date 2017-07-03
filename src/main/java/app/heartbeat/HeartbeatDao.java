package app.heartbeat;

import app.Application;
import app.util.DateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

public class HeartbeatDao {

	private static String HEARTBEAT = "INSERT INTO Heartbeat(deviceId, date) VALUES(:deviceId, :date)";
	Logger logger = LoggerFactory.getLogger(HeartbeatDao.class);

	public void heartbeat(String deviceId) {
		try(Connection conn = Application.sql2o.open()){
			conn.createQuery(HEARTBEAT)
					.addParameter("deviceId", deviceId)
					.addParameter("date", DateHandler.getDateTimeNow())
					.executeUpdate();
		}
		catch(Exception e){
			logger.error("Something failed with the heartbeat write. Suppressing error...");
		}
	}
}
