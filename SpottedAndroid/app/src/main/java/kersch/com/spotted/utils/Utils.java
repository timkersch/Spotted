package kersch.com.spotted.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;
import kersch.com.backend.pinService.model.GeoPt;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-21
 * Time: 02:11
 */
public class Utils {

	/** Returns a formatted string of the date. On the form 00:00 - 2000-01-01
	 */
	public static String getFormatedDate(DateTime date) {
		return date.toString().substring(11,16) + " - " + date.toString().substring(0,10);
	}

	/** Returns a formatted string of the time rounded to
	 *  week, days, hours, minutes or seconds.
	 * @param milliseconds the number of milliseconds to be converted.
	 * @return a string containing the time and a identifier. Ex: "5 hours"
	 */
	public static String getFormatedTime(long milliseconds) {
		// More than one week, return weeks
		if(milliseconds > 7 * Constants.ONE_DAY_IN_MS) {
		double weeks = milliseconds / (7 * Constants.ONE_DAY_IN_MS);
		return (int)weeks == 1 ? (int)weeks + " week" : (int)weeks + " weeks";

		// More than one day, return days
		} else if(milliseconds > Constants.ONE_DAY_IN_MS) {
			double days = milliseconds / (Constants.ONE_DAY_IN_MS);
			return (int)days == 1 ? (int)days + " day" : (int)days + " days";

		// More than one hour, return hours
		} else if(milliseconds > Constants.ONE_DAY_IN_MS/24) {
			double hours = milliseconds / (Constants.ONE_DAY_IN_MS / 24);
			return (int)hours == 1 ? (int)hours + " hour" : (int)hours + " hours";

		// More than one minute, return minutes
		} else if(milliseconds > Constants.ONE_DAY_IN_MS/(24*60)) {
			double minutes = milliseconds / (Constants.ONE_DAY_IN_MS/(24*60));
			return (int)minutes == 1 ? (int)minutes + " minute" : (int)minutes + " minutes";

		// Else, return seconds
		} else {
			return (int)(milliseconds * 0.001) + " seconds";
		}
	}

	/** Returns a string on the form x Months or y Week to milliseconds
	 * @param s the string to be converted
	 * @return the time in milliseconds
	 */
	public static long stringToMilliseconds(String s) {
		String regex = "\\D";
		String ns = s.toLowerCase();

		if(ns.contains("month") || ns.contains("months")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * 30 * (long)Constants.ONE_DAY_IN_MS;

		} else if(ns.contains("week") || ns.contains("weeks")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * 7 * (long)Constants.ONE_DAY_IN_MS;

		} else if(ns.contains("day") || ns.contains("days")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * (long)Constants.ONE_DAY_IN_MS;

		} else if(ns.contains("hour") || ns.contains("hours")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * (long)Constants.ONE_DAY_IN_MS/24;

		} else if(ns.contains("minute") || ns.contains("minutes")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * (long)Constants.ONE_DAY_IN_MS/(24*60);

		} else if(ns.contains("second") || ns.contains("seconds")) {
			long time = Long.parseLong(ns.replaceAll(regex, ""));
			return time * (long)Constants.ONE_DAY_IN_MS/(24*60*60);
		} else {
			return -1;
		}
	}

	/** Converts a GeoPt to a LatLng object.
	 * @param geoPt the GeoPt to be converted
	 * @return a LatLng equal to the parameter
	 */
	public static LatLng toLatLng(GeoPt geoPt) {
		return new LatLng((double)geoPt.getLatitude(), (double)geoPt.getLongitude());
	}
}