package app;

import app.ac.AcController;
import app.actions.ActionDao;
import app.device.DeviceDao;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.heartbeat.HeartbeatDao;
import app.lights.LightController;
import app.lights.LightDao;
import app.mqtt.MqttHandler;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.rythmengine.Rythm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static app.util.JsonTransformer.json;
import static spark.Spark.*;

public class Application {

	public static DeviceDao deviceDao = new DeviceDao();
	public static HeartbeatDao heartbeatDao = new HeartbeatDao();
	public static LightDao lightDao = new LightDao();
	public static ActionDao actionDao = new ActionDao();

	public static MqttHandler mqttHandler;

	public static Sql2o sql2o;

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	private static final String AUTH_HEADER_NAME = System.getenv("authHeaderName");
	private static final String AUTH_HEADER_VALUE = System.getenv("authHeaderValue");

	public static void main(String[] args) throws ClassNotFoundException, IOException, MqttException {

		// DB Connection
		Class.forName("org.sqlite.JDBC");
		sql2o = new Sql2o("jdbc:sqlite:database.db", null, null);

		// MQTT Broker and Client
		mqttHandler = new MqttHandler();

		Map<String, Object> map = new HashMap<>();
		map.put("home.template", System.getProperty("user.dir") + "/resources/templates");
		Rythm.init(map);

		port(8443);

		before("/*", (req, res) -> {
			String authHeaderName = req.headers(AUTH_HEADER_NAME);
			if (authHeaderName == null || !authHeaderName.equals(AUTH_HEADER_VALUE)) {
				logger.error("No auth");
				halt(401);
			}
			logger.info("All good on auth");
		});

		get("/", (req, res) -> Rythm.render("index.html", ", World!"));

		get("/hello", (req, res) -> {
			logger.info("Hi!");
			return "Hello World";
		});

		get(Path.STATUS, DeviceHttpControllerFacade.status, json());
		get(Path.ACTIVATE, DeviceHttpControllerFacade.activate, json());
		get(Path.STATUS_ALL, DeviceHttpControllerFacade.statusAll, json());

		get(Path.HEARTBEAT, HeartbeatController.heartbeat, json());

		get(Path.LIGHTS_ON, LightController.turnOn, json());

		get(Path.AC_ON, AcController.turnOn, json());

		after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
