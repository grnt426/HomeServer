package app;

import app.ac.AcController;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.lights.LightController;
import org.rythmengine.Rythm;
import org.rythmengine.conf.RythmConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static app.util.JsonTransformer.json;
import static spark.Spark.*;

class WebConfig {

	private static final String AUTH_HEADER_NAME = System.getenv("authHeaderName");
	private static final String AUTH_HEADER_VALUE = System.getenv("authHeaderValue");
	private static final String KEYSTORE_PASSWORD = System.getenv("keystorePassword");
	private static final String KEYSTORE_LOCATION = System.getenv("keystoreLocation");
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

		if (KEYSTORE_LOCATION == null) {
			logger.info("Starting up in devo. Serving only HTTP clients.");
		} else {
			secure(KEYSTORE_LOCATION, KEYSTORE_PASSWORD, null, null);
		}
		port(8443);

		before((req, res) -> {
			Session session = req.session(true);

			// We want to auth all pages *except* the login page
			if (req.pathInfo().equals("/loginpage") || req.pathInfo().equals("/login")) {
				logger.info("Bypassing auth check as user is going to login page or logging in.");
				return;
			}

			logger.info("Auth session val: " + session.attribute("auth"));
			if (session.attribute("auth") != null && (Boolean) session.attribute("auth")) {
				logger.info("Auth check successful");
				return;
			}

			logger.info("No auth, checking headers for secondary login...");
			String headerVal = req.headers(AUTH_HEADER_NAME);
			if (headerVal != null && headerVal.equals(AUTH_HEADER_VALUE)) {
				logger.info("Header authentication accepted");
				session.attribute("auth", true);
			} else {
				logger.error("No or bad auth");
				res.redirect("/loginpage");
			}
		});

		post("/login", (request, response) -> {
			String pass = request.queryParams("password");
			if (pass != null && pass.equals(AUTH_HEADER_VALUE)) {
				logger.info("Logged in, redirecting back home");
				request.session().attribute("auth", true);
			} else {
				halt(404, "Bad credentials");
			}
			response.redirect("/");
			return null;
		});

		get("/loginpage", (request, response) -> "<html><body>Password: <form action=\"/login\" method=\"POST\"><input type=\"text\" name=\"password\"/><input type=\"submit\" value=\"Login\"/></form></body></html>");

		get("/", (req, res) -> Rythm.render("index.rythm",
				deviceHttpControllerFacade.getAllDeviceStatus(),
				deviceHttpControllerFacade.getDeviceCapabilityAsMap()));

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
		post(Path.AC_ACTION, acController.action, json());

		after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
