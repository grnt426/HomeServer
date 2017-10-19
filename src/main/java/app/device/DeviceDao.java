package app.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeviceDao {

	private static String INSERT_DEVICE = "INSERT INTO Device (deviceId, name) VALUES (:deviceId, :name, :capabilityId)";
	private static String INSERT_STATUS = "INSERT INTO DeviceStatus(deviceId, date, status) VALUES (:deviceId, :date, :status)";
	private static String SELECT_ALL_STATUS = "SELECT d1.* FROM DeviceStatus d1 LEFT JOIN DeviceStatus d2 ON (d1.deviceId = d2.deviceId AND d1.date < d2.date) WHERE d2.date IS NULL";
	Logger logger = LoggerFactory.getLogger(DeviceDao.class);

	@Autowired
	private Sql2o sql2o;

	public boolean registerDevice(Device device) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(INSERT_DEVICE)
					.addParameter("deviceId", device.getDeviceId())
					.addParameter("name", device.getName())
					.addParameter("capabilityId", device.getCapabilityId())
					.executeUpdate();
			return true;
		} catch(Exception e){
			logger.error("Error updating device address.", e);
		}
		return false;
	}

	public boolean updateStatus(DeviceStatus deviceStatus) {
		try (Connection conn = sql2o.open()) {
			conn.createQuery(INSERT_STATUS)
					.addParameter("deviceId", deviceStatus.getDeviceId())
					.addParameter("date", deviceStatus.getDate())
					.addParameter("status", deviceStatus.getStatus())
					.executeUpdate();
			return true;
		} catch(Exception e){
			logger.error("Error inserting device status.", e);
		}
		return false;
	}

	public List<DeviceStatus> getStatusOfAllDevices(){
		List<DeviceStatus> statuses = new ArrayList<>();
		try (Connection conn = sql2o.open()) {
			statuses = conn.createQuery(SELECT_ALL_STATUS)
					.executeAndFetch(DeviceStatus.class);
		} catch(Exception e){
			logger.error("Error retrieving statuses.", e);
		}
		return statuses;
	}

	public List<DeviceCapability> getAllDeviceCapabilities() {
		List<DeviceCapability> devices = new ArrayList<>();
		try (Connection conn = sql2o.open()) {
			devices = conn.createQuery(
					"SELECT d.deviceId, d.name, c.capabilityId, c.name capabilityName, c.type capabilityType FROM Device d " +
							"LEFT JOIN Capability c ON d.capabilityId = c.capabilityId"
			)
					.executeAndFetch(DeviceCapability.class);
		} catch (Exception e) {
			logger.error("Error retrieving statuses.", e);
		}
		return devices;
	}

	public List<Device> getAllDevices() {
		List<Device> devices = new ArrayList<>();
		try (Connection conn = sql2o.open()) {
			devices = conn.createQuery("SELECT * FROM Device")
					.executeAndFetch(Device.class);
		} catch (Exception e) {
			logger.error("Error retrieving devices.", e);
		}
		return devices;
	}
}
