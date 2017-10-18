package app.ambiance;

import lombok.Data;

@Data
public class AmbientState {
	private String deviceId;
	private int temperature;
	private int humidity;
	private int lightLevel;
	private String eventTime;
}
