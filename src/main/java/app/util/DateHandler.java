package app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler {
	public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(FORMAT);
	
	public static String getDateTimeNow(){
		return dateFormatter.format(new Date());
	}
}
