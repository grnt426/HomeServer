package app.network;

import app.actions.Action;
import app.actions.ActionController;
import app.device.Device;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageTransmitter {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	public static final OkHttpClient client = new OkHttpClient();
	private static final Logger logger = LoggerFactory.getLogger(MessageTransmitter.class);

	public static void sendMessage(Device target, Action payload){
		ActionController.recordAction(payload, target);

		RequestBody body = RequestBody.create(JSON, new Gson().toJson(payload));

		// TODO: Will later want to report failures better and implement retries.
		Response response = null;
		try {
			logger.info("Message sent!");
//			response = client.newCall(request).execute();
//			if(!response.isSuccessful()){
//				logger.error("Error from the device: '" + response.body() + "'");
//			}
		} catch (Exception e) {
			logger.error("Trouble contacting the device.", e);
		}
	}

	private static String buildAddress(Device target, String path) {
		return target.getAddress() + ":9876/" + path;
	}
}
