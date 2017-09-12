package app.device;

import lombok.Data;

@Data
public class DeviceCapability extends Device {
	public int capabilityId;
	public String capabilityName;
	public String capabilityType;
}
