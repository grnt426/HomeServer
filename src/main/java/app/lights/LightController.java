package app.lights;

import app.actions.Action;
import app.device.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

@Service
public class LightController {

	@Autowired
	private LightDao lightDao;

	private static final Logger logger = LoggerFactory.getLogger(LightController.class);

	public Route turnOn = (Request request, Response response) -> {

		// TODO: In the future, just expect the ID is passed in, rather than the name (saves a DB lookup).
		logger.info("Turning on " + request.params(":name"));
		Lights lights = lightDao.getLightGroup(request.params(":name"));
		if(lights != null){
			List<Device> bulbs = lightDao.getBulbs(lights);
			for(Device d : bulbs) {
				Action a = new Action();
				a.setName("percent");
				a.setValue("100");
			}
			response.status(200);
		}
		else{
			logger.warn("Could not find light group");
			response.status(404);
		}

		return "";
	};

	public Route turnOff = (Request request, Response response) -> {
		return "";
	};
}
