package kersch.com.backend.records;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

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
	// The user that has dropped the pin
	//private RegistrationRecord user;
	private String regId;
	// Range -90 to 90 degrees
	private double latitude;
	// Range -180 to 180 degrees
	private double longitude;

	// Time of this pin
	private long timeStamp;

	// The message associated with the pin
	private String message;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getRegId() {
		return regId;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
