package app.lights;

public enum LightStripFlashCode {
	ON(0xFFB04F),
	OFF(0xFFF807),
	BRIGHT_DOWN(0xFFB847),
	BRIGHT_UP(0xFF906F),
	FLASH(0xFFB24D),
	STROBE(0xFF00FF),
	FADE(0xFF58A7),
	SMOOTH(0xFF30CF),

	WHITE(0xFFA857),
	RED(0xFF9867),
	GREEN(0xFFD827),
	BLUE(0xFF8877),
	RED_ORANGE(0xFFE817),
	LIGHT_GREEN(0xFF48B7),
	LIGHT_BLUE(0xFF6897),
	PEACH(0xFF02FD),
	SEA_GREEN(0xFF32CD),
	PURPLE(0xFF20DF),
	ORANGE(0xFF50AF),
	TEAL(0xFF7887),
	LIGHT_PURPLE(0xFF708F),
	YELLOW(0xFF38C7),
	SKY_BLUE(0xFF28D7),
	PINK(0xFFF00F);

	private final int val;

	LightStripFlashCode(int val) {
		this.val = val;
	}

	public int getFlashCode() {
		return val;
	}

	public boolean isProntoCode() {
		return false;
	}
}
