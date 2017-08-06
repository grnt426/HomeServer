package app.actions;

import app.device.Device;
import app.util.DateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionController {

	@Autowired
	private ActionDao actionDao;

	public void recordAction(Action a, Device d) {
		actionDao.recordAction(buildActionDao(a, d));
	}

	private ActionMemento buildActionDao(Action a, Device d) {
		ActionMemento dao = new ActionMemento();
		dao.setName(a.getName());
		dao.setValue(a.getValue());
		dao.setDeviceId(d.getDeviceId());
		dao.setDate(DateHandler.getDateTimeNow());
		return dao;
	}
}
