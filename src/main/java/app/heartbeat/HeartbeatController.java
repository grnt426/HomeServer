package app.heartbeat;

import spark.Request;
import spark.Response;
import spark.Route;

import static app.Application.heartbeatDao;

public class HeartbeatController {

	public static Route heartbeat = (Request request, Response response) -> {
		heartbeatDao.heartbeat(request.params(":deviceId"));
		response.status(200);
		return null;
	};
}
