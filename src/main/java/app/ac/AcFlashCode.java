package app.ac;

import java.util.Arrays;

/**
 * Kenmore window AC unit.
 */
public enum AcFlashCode {
	ON_OFF(0x10AF8877L),
	COOL(0x10AF906FL),
	FAN_UP(0x10AF807FL),
	FAN_DOWN(0x10AF20DFL),
	TEMP_UP(0x10AF708FL),
	TEMP_DOWN(0x10AFB04FL),
	ENERGY_SAVE(0x10AF40BFL),
	AUTO_FAN(0x10AFF00FL),
	FAN_ONLY(0x10AFE01FL);

	private final long val;

	AcFlashCode(long val) {
		this.val = val;
	}

	public String getFlashCode() {
		return "0x" + Long.toHexString(val);
	}

	public boolean isProntoCode() {
		return false;
	}

	public static AcFlashCode getFlashCode(String name) {
		return Arrays.stream(AcFlashCode.values()).filter(e -> e.name().toLowerCase().equals(name)).findFirst().get();
	}
}
