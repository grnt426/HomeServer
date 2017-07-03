package app.actions;

import app.Application;
import app.device.Device;
import app.util.DateHandler;

public class ActionController {

	public static void recordAction(Action a, Device d){
		Application.actionDao.recordAction(buildActionDao(a, d));
	}

	private static ActionMemento buildActionDao(Action a, Device d){
		ActionMemento dao = new ActionMemento();
		dao.setName(a.getName());
		dao.setValue(a.getValue());
		dao.setDeviceId(d.getDeviceId());
		dao.setDate(DateHandler.getDateTimeNow());
		return dao;
	}
}
