package app.actions;

import lombok.Data;

@Data
public class ActionMemento {
	private String name;
	private String value;
	private String deviceId;
	private String date;
}
