package kersch.com.backend.records;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:09
 */
@Entity
public class PinRecord {

	@Id
	private Long id;

	@Index
	// Geographical location
	private GeoPt geoPoint;

	// Time of this pin
	private Date date;

	// The message associated with the pin
	private String message;

	public Date getTimeStamp() {
		return date;
	}

	public void setTimeStamp(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GeoPt getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(float latitude, float longitude) {
		geoPoint = new GeoPt(latitude, longitude);
	}

	public void setGeoPoint(GeoPt geoPoint) {
		this.geoPoint = geoPoint;
	}
}
