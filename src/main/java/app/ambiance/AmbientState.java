package app.ambiance;

import lombok.Data;

@Data
public class AmbientState {
	private String deviceID;
	private int temperature;
	private int humidity;
	private int lightLevel;
	private String eventTime;
}
