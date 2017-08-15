package app;

public class Path {
	public static final String STATUS = "/api/status/:deviceId";
	public static final String STATUS_ALL = "/api/status";

	public static final String ACTIVATE = "/api/activate/:deviceId";

	public static final String HEARTBEAT = "/api/heartbeat/:deviceId";

	public static final String LIGHTS_ON = "/api/lights/on/:name";

	public static final String AC_ON = "/api/ac/on";

	public static final String AC_ACTION = "/api/ac/action/:name";
}
