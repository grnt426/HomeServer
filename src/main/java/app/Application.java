package app;

import app.device.DeviceController;
import app.device.DeviceDao;
import app.heartbeat.HeartbeatController;
import app.heartbeat.HeartbeatDao;
import org.sql2o.Sql2o;

import static app.util.JsonTransformer.json;
import static spark.Spark.after;
import static spark.Spark.get;

public class Application {

	public static DeviceDao deviceDao = new DeviceDao();
	public static HeartbeatDao heartbeatDao = new HeartbeatDao();

	public static Sql2o sql2o;

	public static void main(String[] args) throws ClassNotFoundException {

		Class.forName("org.sqlite.JDBC");
		sql2o = new Sql2o("jdbc:sqlite:database.db", null, null);

		get("/hello", (req, res) -> "Hello World");

		get(Path.STATUS, DeviceController.status, json());
		get(Path.ACTIVATE, DeviceController.activate, json());
		get(Path.STATUS_ALL, DeviceController.statusAll, json());

		get(Path.HEARTBEAT, HeartbeatController.heartbeat, json());

		after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
