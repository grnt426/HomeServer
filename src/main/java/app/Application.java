package app;

import app.actions.ActionDao;
import app.device.DeviceDao;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.heartbeat.HeartbeatDao;
import app.lights.LightController;
import app.lights.LightDao;
import app.mqtt.MqttHandler;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.sql2o.Sql2o;

import java.io.IOException;

import static app.util.JsonTransformer.json;
import static spark.Spark.after;
import static spark.Spark.get;

public class Application {

	public static DeviceDao deviceDao = new DeviceDao();
	public static HeartbeatDao heartbeatDao = new HeartbeatDao();
	public static LightDao lightDao = new LightDao();
	public static ActionDao actionDao = new ActionDao();

	public static Sql2o sql2o;

	public static void main(String[] args) throws ClassNotFoundException, IOException, MqttException {

		// DB Connection
		Class.forName("org.sqlite.JDBC");
		sql2o = new Sql2o("jdbc:sqlite:database.db", null, null);

		// MQTT Broker and Client
		new MqttHandler();

		get("/hello", (req, res) -> "Hello World");

		get(Path.STATUS, DeviceHttpControllerFacade.status, json());
		get(Path.ACTIVATE, DeviceHttpControllerFacade.activate, json());
		get(Path.STATUS_ALL, DeviceHttpControllerFacade.statusAll, json());

		get(Path.HEARTBEAT, HeartbeatController.heartbeat, json());

		get(Path.LIGHTS_ON, LightController.turnOn, json());

		after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
