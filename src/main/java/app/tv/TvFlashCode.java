package app.tv;

public enum TvFlashCode {
	ON_OFF(0x20DF10EF),
	EXIT(0x20DF926D),
	INPUT(0x20DFF40B),
	UP(0x20DFA25D),
	RIGHT(0x20DF12ED),
	DOWN(0x20DF629D),
	LEFT(0x20DFE21D),
	OK(0x20DF22DD),
	FAN_ONLY(0x10AFE01F);

	private final int val;

	TvFlashCode(int val) {
		this.val = val;
	}

	public int getFlashCode() {
		return val;
	}

	public boolean isProntoCode() {
		return false;
	}
}
