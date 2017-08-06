package app.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

@Repository
public class ActionDao {

	// TODO: Will later want the user that did this.
	private static String RecordAction = "INSERT INTO Actions(deviceId, date, action, value) VALUES(:deviceId, :date, :action, :value)";
	private final Logger logger = LoggerFactory.getLogger(ActionDao.class);

	@Autowired
	private Sql2o sql2o;

	public void recordAction(ActionMemento action) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(RecordAction)
					.addParameter("deviceId", action.getDeviceId())
					.addParameter("date", action.getDate())
					.addParameter("action", action.getName())
					.addParameter("value", action.getValue())
					.executeUpdate();
		} catch(Exception e){
			logger.error("Something failed with the action write. Ignoring error...", e);
		}
	}
}
