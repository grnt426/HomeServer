package app.lights;

import app.device.Device;
import lombok.Data;

import java.util.List;

@Data
public class Lights {
	private String groupId;
	private String name;
	private String state;
	private List<Device> bulbs;
}
