package app.device;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Device {

	@NonNull
	private String deviceId;
	private String name;

	@NonNull
	private String address;

	private int capabilityId;
}
