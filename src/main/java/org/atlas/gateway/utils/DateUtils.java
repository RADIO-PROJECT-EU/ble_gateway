package org.atlas.gateway.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * Convert a date to appropriate String format
	 * @param format - The new format of the Date object
	 * @param date - The date to transform
	 * @return - String representation of the date.
	 */
	public static String dateToString(String format, Date date){
		return new SimpleDateFormat(format).format(date);
	}
	
	public static Date getTimeAfterSeconds(int seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, seconds);
		return calendar.getTime();
	}
	
}
