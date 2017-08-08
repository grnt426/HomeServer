package app;

import app.ac.AcController;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.lights.LightController;
import org.rythmengine.Rythm;
import org.rythmengine.conf.RythmConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static app.util.JsonTransformer.json;
import static spark.Spark.*;

class WebConfig {

	private static final String AUTH_HEADER_NAME = System.getenv("authHeaderName");
	private static final String AUTH_HEADER_VALUE = System.getenv("authHeaderValue");
	private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

	private final AcController acController;
	private final DeviceHttpControllerFacade deviceHttpControllerFacade;
	private final HeartbeatController heartbeatController;
	private final LightController lightController;

	WebConfig(AcController acController, DeviceHttpControllerFacade deviceHttpControllerFacade,
	          HeartbeatController heartbeatController, LightController lightController) {
		this.acController = acController;
		this.deviceHttpControllerFacade = deviceHttpControllerFacade;
		this.heartbeatController = heartbeatController;
		this.lightController = lightController;
		setupRoutes();
	}

	private void setupRoutes() {

		Map<String, Object> map = new HashMap<>();
		map.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(), System.getProperty("user.dir") + "/resources/templates");
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

		get("/", (req, res) -> Rythm.render("index.rythm", deviceHttpControllerFacade.getAllDeviceStatus()));

		get("/hello", (req, res) -> {
			logger.info("Hi!");
			return "Hello World";
		});

		get(Path.STATUS, deviceHttpControllerFacade.status, json());
		get(Path.ACTIVATE, deviceHttpControllerFacade.activate, json());
		get(Path.STATUS_ALL, deviceHttpControllerFacade.statusAll, json());

		get(Path.HEARTBEAT, heartbeatController.heartbeat, json());

		get(Path.LIGHTS_ON, lightController.turnOn, json());

		get(Path.AC_ON, acController.turnOn, json());

		after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
