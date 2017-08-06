package app.lights;

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
public class LightDao {

	private static String SELECT_LIGHTS = "SELECT * FROM Lights WHERE name = :name";
	private static String SELECT_BULBS = "SELECT * FROM Device WHERE deviceId IN (SELECT deviceId FROM LightGroupings where groupId = :groupId)";
	private final Logger logger = LoggerFactory.getLogger(LightDao.class);

	@Autowired
	private Sql2o sql2o;

	public Lights getLightGroup(String name) {
		try (Connection conn = sql2o.open()) {
			List<Lights> lights = conn.createQuery(SELECT_LIGHTS)
					.addParameter("name", name)
					.executeAndFetch(Lights.class);

			if(lights.size() != 1){
				logger.warn("Could not find light group.");
				return null;
			} else{
				return lights.get(0);
			}
		} catch(Exception e){
			logger.error("Error retrieving lights.", e);
		}
		return null;
	}

	public List<Device> getBulbs(Lights lights) {
		try (Connection conn = sql2o.open()) {
			List<Device> bulbs = conn.createQuery(SELECT_BULBS)
					.addParameter("groupId", lights.getGroupId())
					.executeAndFetch(Device.class);
			if(bulbs != null){
				return bulbs;
			}
		} catch(Exception e){
			logger.error("Error retrieving bulbs.", e);
		}
		return new ArrayList<>();
	}
}
