package app.actions;

import app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

public class ActionDao {

	// TODO: Will later want the user that did this.
	private static String RecordAction = "INSERT INTO Actions(deviceId, date, action, value) VALUES(:deviceId, :date, :action, :value)";
	Logger logger = LoggerFactory.getLogger(ActionDao.class);

	public void recordAction(ActionMemento action) {
		try(Connection conn = Application.sql2o.open()){
			conn.createQuery(RecordAction)
					.addParameter("deviceId", action.getDeviceId())
					.addParameter("date", action.getDate())
					.addParameter("action", action.getName())
					.addParameter("value", action.getValue())
					.executeUpdate();
		}
		catch(Exception e){
			logger.error("Something failed with the action write. Ignoring error...", e);
		}
	}
}
