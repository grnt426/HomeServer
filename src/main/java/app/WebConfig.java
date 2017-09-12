package app;

import app.ac.AcController;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.lights.LightController;
import org.rythmengine.Rythm;
import org.rythmengine.conf.RythmConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import spark.Session;

import java.util.HashMap;
import java.util.Map;

import static app.util.JsonTransformer.json;

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
		Service service = Service.ignite();

		if (KEYSTORE_LOCATION == null) {
			logger.info("Starting up in devo. Serving only HTTP clients.");
		} else {
			logger.info("Starting up in prod.");
			service.secure(KEYSTORE_LOCATION, KEYSTORE_PASSWORD, null, null);

			// We need to setup a second server to redirect http to https
			Service http = Service.ignite();
			http.port(58080);
			http.before((req, res) -> {
				logger.info("Redirecting to https...");
				res.redirect("https://" + req.host() + req.uri());
			});
		}

		service.port(8443);

		service.before((req, res) -> {
			logger.info("https://" + req.host() + req.uri());
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

		service.post("/login", (request, response) -> {
			String pass = request.queryParams("password");
			if (pass != null && pass.equals(AUTH_HEADER_VALUE)) {
				logger.info("Logged in, redirecting back home");
				request.session().attribute("auth", true);
			} else {
				service.halt(404, "Bad credentials");
			}
			response.redirect("/");
			return null;
		});

		service.get("/loginpage", (request, response) -> "<html><body>Password: <form action=\"/login\" method=\"POST\"><input type=\"text\" name=\"password\"/><input type=\"submit\" value=\"Login\"/></form></body></html>");

		service.get("/", (req, res) -> Rythm.render("index.rythm",
				deviceHttpControllerFacade.getAllDeviceStatus(),
				deviceHttpControllerFacade.getDeviceCapabilityAsMap()));

		service.get("/hello", (req, res) -> {
			logger.info("Hi!");
			return "Hello World";
		});

		service.get(Path.STATUS, deviceHttpControllerFacade.status, json());
		service.get(Path.ACTIVATE, deviceHttpControllerFacade.activate, json());
		service.get(Path.STATUS_ALL, deviceHttpControllerFacade.statusAll, json());

		service.get(Path.HEARTBEAT, heartbeatController.heartbeat, json());

		service.get(Path.LIGHTS_ON, lightController.turnOn, json());

		service.get(Path.AC_ON, acController.turnOn, json());
		service.post(Path.AC_ACTION, acController.action, json());

		service.after("api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
