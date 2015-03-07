package kersch.com.spotted.model;

import android.location.Location;
import com.google.android.gms.maps.model.Marker;

import java.util.*;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-03-07
 * Time: 00:06
 */
public class MapWithFilter<K extends Marker, E extends Pin> extends HashMap<K, E>{

	/** Method that calculates and extracts all points within a radius
	 * @param latitude the latitude of the current location
	 * @param longitude the longitude of the current location
	 * @param radius the radius in meters from current location.
	 * @return a list with only the pins within the radius
	 */
	public List<Pin> valueListWithRadiusFilter(double latitude, double longitude, double radius) {
		List<Pin> newList = new ArrayList<>();
		for(Pin p : this.values()) {
			if(pinInRadius(p, latitude, longitude, radius)) {
				newList.add(p);
			}
		}
		return newList;
	}

	/** Method that calculates and extracts all markers within a radius
	 * @param latitude the latitude of the current location
	 * @param longitude the longitude of the current location
	 * @param radius the radius in meters from current location.
	 * @return a list with only the markers within the radius
	 */
	public List<Marker> keyListWithRadiusFilter(double latitude, double longitude, double radius) {
		List<Marker> newList = new ArrayList<>();
		for(Marker m : this.keySet()) {
			if(pinInRadius(this.get(m), latitude, longitude, radius)) {
				newList.add(m);
			}
		}
		return newList;
	}

	/** Method extracts which pins are recently created and returns them.
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has existed to be included.
	 * @return a new list of pins with only the pins that are recently created
	 */
	public List<Pin> valueListWithCreatedFilter(long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Pin> newList = new ArrayList<>();
		for(Pin p : this.values()) {
			if(pinRecentlyCreated(p, currentTime, limitInMilliseconds)) {
				newList.add(p);
			}
		}
		return newList;
	}

	/** Method extracts which markers are recently created and returns them.
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has existed to be included.
	 * @return a new list of markers with only the pins that are recently created
	 */
	public List<Marker> keyListWithCreatedFilter(long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Marker> newList = new ArrayList<>();
		for(Marker m : this.keySet()) {
			if(pinRecentlyCreated(this.get(m), currentTime, limitInMilliseconds)) {
				newList.add(m);
			}
		}
		return newList;
	}

	/** Method that extracts which pins are soon to expire.
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has left to live to be included.
	 * @return a new list of only the pins that are soon to expire
	 */
	public List<Pin> valueListWithExpiresFilter(long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Pin> newList = new ArrayList<>();
		for(Pin p : this.values()) {
			if(pinExpiresSoon(p, currentTime, limitInMilliseconds)) {
				newList.add(p);
			}
		}
		return newList;
	}

	/** Method that extracts which markers are soon to expire.
	 * @param limitInMilliseconds the maximum number of milliseconds the pin has left to live to be included.
	 * @return a new list of only the pins that are soon to expire
	 */
	public List<Marker> keyListWithExpiresFilter(long limitInMilliseconds) {
		long currentTime = System.currentTimeMillis();
		List<Marker> newList = new ArrayList<>();
		for(Marker m : this.keySet()) {
			if(pinExpiresSoon(this.get(m), currentTime, limitInMilliseconds)) {
				newList.add(m);
			}
		}
		return newList;
	}

	// Determines if a pin is inside a radius
	private boolean pinInRadius(Pin p, double latitude, double longitude, double radius) {
		float distance[] = new float[1];
		Location.distanceBetween(latitude, longitude, p.getLocation().getLatitude(), p.getLocation().getLongitude(), distance);
		return (distance[0] <= radius);
	}

	// Determines if a pin is recently created
	private boolean pinRecentlyCreated(Pin p, long currentTime, long limitInMilliseconds) {
		return ((currentTime - p.getDate().getValue()) <= limitInMilliseconds);
	}

	// Determines if a pin expires soon
	private boolean pinExpiresSoon(Pin p, long currentTime, long limitInMilliseconds) {
		return (p.getLifetimeInMilliseconds() - (currentTime - p.getDate().getValue()) <= limitInMilliseconds);
	}
}
