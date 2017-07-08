package app.ac;

/**
 * Kenmore window AC unit.
 */
public enum AcFlashCode {
	ON_OFF(0x10AF8877),
	COOL(0x10AF906F),
	FAN_UP(0x10AF807F),
	FAN_DOWN(0x10AF20DF),
	TEMP_UP(0x10AF708F),
	TEMP_DOWN(0x10AFB04F),
	ENERGY_SAVE(0x10AF40BF),
	AUTO_FAN(0x10AFF00F),
	FAN_ONLY(0x10AFE01F);

	private final int val;

	AcFlashCode(int val) {
		this.val = val;
	}

	public int getFlashCode() {
		return val;
	}

	public boolean isProntoCode() {
		return false;
	}
}
