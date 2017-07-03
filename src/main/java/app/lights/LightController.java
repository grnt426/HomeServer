package app.lights;

import app.actions.Action;
import app.device.Device;
import app.network.MessageTransmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

import static app.Application.lightDao;

public class LightController {

	private static final Logger logger = LoggerFactory.getLogger(LightController.class);

	public static Route turnOn = (Request request, Response response) -> {

		// TODO: In the future, just expect the ID is passed in, rather than the name (saves a DB lookup).
		logger.info("Turning on " + request.params(":name"));
		Lights lights = lightDao.getLightGroup(request.params(":name"));
		if(lights != null){
			List<Device> bulbs = lightDao.getBulbs(lights);
			for(Device d : bulbs) {
				Action a = new Action();
				a.setName("percent");
				a.setValue("100");
				MessageTransmitter.sendMessage(d, a);
			}
			response.status(200);
		}
		else{
			logger.warn("Could not find light group");
			response.status(404);
		}

		return "";
	};

	public static Route turnOff = (Request request, Response response) -> {
		return "";
	};
}
