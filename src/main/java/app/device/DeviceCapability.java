package app.device;

import lombok.Data;

@Data
public class DeviceCapability extends Device {
	private String capabilityId;
	private String capabilityName;
	private String capabilityType;
}
