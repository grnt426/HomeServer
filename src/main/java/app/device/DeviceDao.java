package app.device;

import app.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import java.util.ArrayList;
import java.util.List;

public class DeviceDao {

	private static String UPDATE_DEVICE = "UPDATE Device SET address = :address WHERE deviceId = :deviceId";
	private static String INSERT_STATUS = "INSERT INTO DeviceStatus(deviceId, date, status) VALUES (:deviceId, :date, :status)";
	private static String SELECT_ALL_STATUS = "SELECT d1.* FROM DeviceStatus d1 LEFT JOIN DeviceStatus d2 ON (d1.deviceId = d2.deviceId AND d1.date < d2.date) WHERE d2.date IS NULL";
	Logger logger = LoggerFactory.getLogger(DeviceDao.class);

	public boolean registerDevice(Device device) {
		try (Connection conn = Application.sql2o.open()) {
			conn.createQuery(UPDATE_DEVICE)
					.addParameter("deviceId", device.getDeviceId())
					.addParameter("address", device.getAddress())
					.executeUpdate();
			return true;
		}
		catch(Exception e){
			logger.error("Error updating device address.", e);
		}
		return false;
	}

	public boolean updateStatus(DeviceStatus deviceStatus) {
		try (Connection conn = Application.sql2o.open()) {
			conn.createQuery(INSERT_STATUS)
					.addParameter("deviceId", deviceStatus.getDeviceId())
					.addParameter("date", deviceStatus.getDate())
					.addParameter("status", deviceStatus.getStatus())
					.executeUpdate();
			return true;
		}
		catch(Exception e){
			logger.error("Error inserting device status.", e);
		}
		return false;
	}

	public List<DeviceStatus> getStatusOfAllDevices(){
		List<DeviceStatus> statuses = new ArrayList<>();
		try (Connection conn = Application.sql2o.open()) {
			statuses = conn.createQuery(SELECT_ALL_STATUS)
					.executeAndFetch(DeviceStatus.class);
		}
		catch(Exception e){
			logger.error("Error retrieving statuses.", e);
		}
		return statuses;
	}
}
