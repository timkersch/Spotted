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
		double weeks = milliseconds % (7 * Constants.ONE_DAY_IN_MS - 1);
		return weeks == 1 ? weeks + " week" : weeks + " weeks";

		// More than one day, return days
		} else if(milliseconds > Constants.ONE_DAY_IN_MS) {
			double days = milliseconds % (Constants.ONE_DAY_IN_MS - 1);
			return days == 1 ? days + " day" : days + " days";

		// More than one hour, return hours
		} else if(milliseconds > Constants.ONE_DAY_IN_MS/24) {
			double hours = milliseconds % (Constants.ONE_DAY_IN_MS / 24 - 1);
			return hours == 1 ? hours + " hour" : hours + " hours";

		// More than one minute, return minutes
		} else if(milliseconds > Constants.ONE_DAY_IN_MS/(24*60)) {
			double minutes = milliseconds % (Constants.ONE_DAY_IN_MS/(24*60) - 1);
			return minutes == 1 ? minutes + " minute" : minutes + " minutes";

		// Else, return seconds
		} else {
			return milliseconds * 0.001 + " seconds";
		}
	}

	public static LatLng toLatLng(GeoPt geoPt) {
		return new LatLng((double)geoPt.getLatitude(), (double)geoPt.getLongitude());
	}
}