package app.device;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceCapability extends Device {
	public int capabilityId;
	public String capabilityName;
	public String capabilityType;
}
