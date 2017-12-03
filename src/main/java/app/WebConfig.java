package app;

import app.ac.AcController;
import app.ambiance.AmbientHttpControllerFacade;
import app.device.DeviceHttpControllerFacade;
import app.heartbeat.HeartbeatController;
import app.lights.LightController;
import org.rythmengine.Rythm;
import org.rythmengine.conf.RythmConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;
import spark.Session;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static app.util.JsonTransformer.json;
import static spark.Spark.halt;

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
	private final AmbientHttpControllerFacade ambientHttpControllerFacade;

	WebConfig(AcController acController, DeviceHttpControllerFacade deviceHttpControllerFacade,
	          HeartbeatController heartbeatController, LightController lightController,
	          AmbientHttpControllerFacade ambientHttpControllerFacade) {
		this.acController = acController;
		this.deviceHttpControllerFacade = deviceHttpControllerFacade;
		this.heartbeatController = heartbeatController;
		this.lightController = lightController;
		this.ambientHttpControllerFacade = ambientHttpControllerFacade;
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

			// This is used during SSL certificate renewal ONLY.
			http.get("/.certdir/*", (req, res) -> {
				res.type("text/plain");
				res.raw().getOutputStream().write(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + req.uri())));
				res.raw().getOutputStream().flush();
				res.raw().getOutputStream().close();
				return res.raw();
			});

			// If we aren't serving certificate data, then the user needs to be redirected to HTTPS. HTTP is only for
			// cert renewal.
			http.get("*", (req, res) -> {
				logger.info("Redirecting to https...");
				res.redirect("https://" + req.host() + req.uri());
				res.type("text/html");
				return null;
			});
		}

		service.port(8443);

		service.externalStaticFileLocation(System.getProperty("user.dir") + "/resources");

		service.before((req, res) -> {
			Session session = req.session(true);

			// We want to auth all pages *except* the login page
			if (req.pathInfo().equals("/home/loginpage") || req.pathInfo().equals("/home/login")) {
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
				res.redirect("/home/loginpage");
				halt();
			}
		});

		service.post("/home/login", (request, response) -> {
			String pass = request.queryParams("password");
			if (pass != null && pass.equals(AUTH_HEADER_VALUE)) {
				logger.info("Logged in, redirecting back home");
				request.session().attribute("auth", true);
			} else {
				service.halt(404, "Bad credentials");
			}
			response.redirect("/home");
			return null;
		});

		service.get("/home/loginpage", (request, response) -> "<html><body>Password: <form action=\"/home/login\" method=\"POST\"><input type=\"text\" name=\"password\"/><input type=\"submit\" value=\"Login\"/></form></body></html>");

		service.get("/", (req, res) -> {
			res.redirect("/home");
			halt();
			return null;
		});

		service.get("/home", (req, res) -> Rythm.render("index.rythm",
				deviceHttpControllerFacade.getAllDeviceStatus(),
				deviceHttpControllerFacade.getDeviceCapabilityAsMap(),
				deviceHttpControllerFacade.getAllDevices(),
				acController.getAllAcStates(),
				ambientHttpControllerFacade.getMostRecentAmbientStatesAsMap())
		);

		service.get("/home/hello", (req, res) -> {
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

		service.after("/home", (req, res) -> {
			res.type("text/html");
		});

		service.after("/api/*", (req, res) -> {
			res.type("application/json");
		});
	}
}
