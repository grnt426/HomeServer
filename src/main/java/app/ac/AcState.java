package app.ac;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AcState {
	private String deviceId;
	private int powered;
	private int temperature;
	private int fanSpeed;
	private String mode;

	/**
	 * Indicates if a particular state is not known, not set, or not requested.
	 */
	public static final int UNDF = -1;

	public static final int ON = 1;
	public static final int OFF = 0;

	public static final Map<String, Integer> FAN_SPEED = new HashMap<>();

	public static final Map<String, String> MODE = new HashMap<>();

	static {
		FAN_SPEED.put("h", 3);
		FAN_SPEED.put("high", 3);

		FAN_SPEED.put("m", 2);
		FAN_SPEED.put("med", 2);
		FAN_SPEED.put("medium", 2);

		FAN_SPEED.put("l", 1);
		FAN_SPEED.put("low", 1);

		FAN_SPEED.put("a", 0);
		FAN_SPEED.put("auto", 0);

		MODE.put("energy save", "energy_save");
		MODE.put("energy_save", "energy_save");
		MODE.put("save", "energy_save");

		MODE.put("fan", "fan_only");
		MODE.put("fan only", "fan_only");
		MODE.put("fan_only", "fan_only");

		MODE.put("cool", "cool");
	}

	/**
	 * This is the EMPTY state, which is useful for checking if the state of the AC is known, or if a request
	 * was malformed.
	 */
	public AcState() {
		powered = UNDF;
		temperature = UNDF;
		fanSpeed = UNDF;
		mode = "UNKNOWN";
	}

	public static final AcState EMPTY_REQUEST = new AcState();
}
