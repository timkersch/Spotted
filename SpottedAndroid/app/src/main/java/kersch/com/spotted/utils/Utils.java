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

	public static LatLng toLatLng(GeoPt geoPt) {
		return new LatLng((double)geoPt.getLatitude(), (double)geoPt.getLongitude());
	}
}