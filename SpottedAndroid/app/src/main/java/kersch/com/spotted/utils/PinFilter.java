package kersch.com.spotted.utils;

import android.location.Location;
import kersch.com.spotted.model.Pin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-03-07
 * Time: 00:06
 */
public class PinFilter {

	/** Method that calculates and extracts all points within a radius
	 * @param pinList a list of pins to check
	 * @param latitude the latitude of the current location
	 * @param longitude the longitude of the current location
	 * @param radius the radius in meters from current location.
	 * @return a list with only the pins within the radius
	 */
	public static List<Pin> filterWithinRadius(List<Pin> pinList, double latitude, double longitude, double radius) {
		List<Pin> newList = new ArrayList<>();
		for(Pin p : pinList) {
			float distance[] = new float[1];
			// Calculates distance between current location and pin location
			Location.distanceBetween(latitude, longitude, p.getLocation().getLatitude(), p.getLocation().getLongitude(), distance);
			if(distance[0] <= radius) {
				newList.add(p);
			}
		}
		return newList;
	}

	/** Method extracts which pins are recently created and returns them.
	 * @param pinList the list of pins to check
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has existed to be included.
	 * @return a new list of pins with only the pins that are recently created
	 */
	public static List<Pin> filterRecentlyCreated(List<Pin> pinList, long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Pin> newList = new ArrayList<>();
		for(Pin p : pinList) {
			if(currentTime - p.getDate().getValue() <= limitInMilliseconds) {
				newList.add(p);
			}
		}
		return newList;
	}

	/** Method that extracts which pins are soon to expire.
	 * @param pinList the list of pins to check
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has left to live to be included.
	 * @return a new list of only the pins that are soon to expire
	 */
	public static List<Pin> filterExpiresSoon(List<Pin> pinList, long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Pin> newList = new ArrayList<>();
		for(Pin p : pinList) {
			if(p.getLifetimeInMilliseconds() - (currentTime - p.getDate().getValue()) <= limitInMilliseconds) {
				newList.add(p);
			}
		}
		return newList;
	}
}
