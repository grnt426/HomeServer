package app.heartbeat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;
import spark.Route;

@Service
public class HeartbeatController {

	@Autowired
	private HeartbeatDao heartbeatDao;

	public Route heartbeat = (Request request, Response response) -> {
		heartbeatDao.heartbeat(request.params(":deviceId"));
		response.status(200);
		return null;
	};
}
