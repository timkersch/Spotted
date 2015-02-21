package kersch.com.backend.records;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.google.appengine.api.datastore.Text;

import java.util.Date;
import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:09
 */
@Entity
public class PinRecord {
	@Id
	@Index
	private Long id;

	private String title;

	// Geographical location
	private GeoPt geoPoint;

	// Time of this pin
	private Date date;

	// The message associated with the pin
	private String message;

	private long lifeLengthInMilliseconds;

	private int likes;

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public Date getTimeStamp() {
		return date;
	}

	public void setTimeStamp(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public long getLifeLengthInMilliseconds() {
		return lifeLengthInMilliseconds;
	}

	public void setLifeLengthInMilliseconds(long lifeTime) {
		this.lifeLengthInMilliseconds = lifeTime;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public long getID() {
		return id;
	}

	public void incrementLikes() {
		likes = likes + 1;
	}
}
