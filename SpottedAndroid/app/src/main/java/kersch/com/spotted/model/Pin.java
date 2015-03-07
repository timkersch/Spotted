package kersch.com.spotted.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.util.DateTime;
import kersch.com.backend.pinService.model.GeoPt;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.backend.pinService.model.ResponseRecord;
import kersch.com.spotted.appEngineServices.DbOperations;
import kersch.com.spotted.utils.RandomPins;
import kersch.com.spotted.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:34
 */
public class Pin implements Parcelable, Comparable<Pin> {
	private final GeoPt geoPt;
	private final String title;
	private final String message;
	private final long lifetimeInMilliseconds;
	private final DateTime date;
	private final int pinDrawableId;
	private final List<Response> responses = new ArrayList<>();

	private long id;
	private int likes;

	/** Create a new pin with a lat and longitude.
	 * @param latitude the latitude of the pin
	 * @param longitude the longitude of the pin
	 * @param title the title for the pin
	 * @param message the message for the pin
	 */
	public Pin(float latitude, float longitude, String title, String message, long lifetimeInMilliseconds) {
		this.geoPt = new GeoPt();
		this.geoPt.setLatitude(latitude);
		this.geoPt.setLongitude(longitude);
		this.title = title;
		this.message = message;
		this.lifetimeInMilliseconds = lifetimeInMilliseconds;
		this.pinDrawableId = RandomPins.getPinId();
		this.date = new DateTime(System.currentTimeMillis());
		this.likes = 0;
		addPinToDatabase();
	}

	/** Create a new Pin with a GeoPt.
	 * @param geoPt the geographic position of the pin
	 * @param title the title of the pin
	 * @param message the message of the pin
	 */
	public Pin(GeoPt geoPt, String title, String message, long lifetimeInMilliseconds) {
		this(geoPt.getLatitude(), geoPt.getLongitude(), title, message, lifetimeInMilliseconds);
	}

	/** Create a new Pin from a PinRecord from the database
	 * @param pinRecord the pinRecord from the database
	 * @param responses a list of the responseRecord associated with this pin
	 */
	public Pin(PinRecord pinRecord, List<ResponseRecord> responses) {
		this.pinDrawableId = RandomPins.getPinId();
		this.geoPt = pinRecord.getGeoPoint();
		this.title = pinRecord.getTitle();
		this.message = pinRecord.getMessage();
		this.lifetimeInMilliseconds = pinRecord.getLifeLengthInMilliseconds();
		this.likes = pinRecord.getLikes();
		this.date = pinRecord.getTimeStamp();
		this.id = pinRecord.getId();
		if(responses != null) {
			// Add all responses for this pin
			for (ResponseRecord record : responses) {
				this.responses.add(new Response(record));
			}
		}
	}

	// Private constructor to reconstruct object with Parcel
	private Pin(Parcel in) {
		float lat = in.readFloat();
		float lon = in.readFloat();
		title = in.readString();
		message = in.readString();
		lifetimeInMilliseconds = in.readLong();
		long time = in.readLong();
		pinDrawableId = in.readInt();
		in.readTypedList(responses, Response.CREATOR);
		id = in.readLong();
		likes = in.readInt();

		geoPt = new GeoPt();
		geoPt.setLatitude(lat);
		geoPt.setLongitude(lon);
		date = new DateTime(time);
	}

	/** Method that return the immutable GeoPt of the pin.
	 * @return
	 */
	public GeoPt getLocation() {
		return this.geoPt;
	}

	/** Method that returns the immutable Message of the pin.
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/** Method that returns the immutable date of the pin.
	 * @return
	 */
	public DateTime getDate() {
		return date;
	}

	/** Method that returns the immutable title of the pin.
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/** Method that returns the immutable lifetime of the pin.
	 * @return
	 */
	public long getLifetimeInMilliseconds() {
		return lifetimeInMilliseconds;
	}

	/** Method that increments the likes for this pin.
	 */
	public void incrementLikes() {
		likes++;
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					DbOperations.getPinService().incrementLikes(id).execute();
				} catch (IOException e) {
					e.getMessage();
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	/** Method that adds a response to this pin.
	 * @param response a string with a response message
	 */
	public void addResponse(String response) {
		final Response resp = new Response(response);
		responses.add(resp);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					DbOperations.getPinService().addResponse(id, resp.getMessage(), resp.getDate()).execute();
				} catch (IOException e) {
					e.getMessage();
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	/** Returns the marker options for this pin.
	 */
	public MarkerOptions getMarkerOptions() {
		MarkerOptions opt = new MarkerOptions();
		opt.position(Utils.toLatLng(geoPt));
		opt.title(title);
		opt.snippet(message);
		opt.icon(BitmapDescriptorFactory.fromResource(pinDrawableId));
		return opt;
	}

	/** Returns the pin drawable id for this pin.
	 */
	public int getPinDrawableId() {
		return pinDrawableId;
	}

	/** Returns the number of likes on this pin.
	 */
	public int getLikes() {
		return likes;
	}

	/** Returns the number of repsonses made to this pin.
	 */
	public int getNumberOfResponses() {
		return responses.size();
	}

	/** Returns a list of responses associated with this pin.
	 * @return
	 */
	public List<Response> getResponses() {
		return responses;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(o.getClass() == Pin.class) {
			Pin p = (Pin)o;
			return p.geoPt.equals(this.geoPt)
					&& p.title.equals(this.title)
					&& p.message.equals(this.message);
		}
		return false;
	}

	// Adds a pin to the database
	private void addPinToDatabase() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					PinRecord record = DbOperations.getPinService()
							.registerPin(title, message, lifetimeInMilliseconds, date, geoPt)
							.execute();
					// Set the id of this pin
					id = record.getId();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	// Write object to parcel
	public void writeToParcel(Parcel out, int flags) {
		out.writeFloat(geoPt.getLatitude());
		out.writeFloat(geoPt.getLongitude());
		out.writeString(title);
		out.writeString(message);
		out.writeFloat(lifetimeInMilliseconds);
		out.writeLong(date.getValue());
		out.writeInt(pinDrawableId);
		out.writeTypedList(responses);
		out.writeLong(id);
		out.writeInt(likes);
	}

	// Method to reconstruct object with parcel
	public static final Parcelable.Creator<Pin> CREATOR = new Parcelable.Creator<Pin>() {
		public Pin createFromParcel(Parcel in) {
			return new Pin(in);
		}

		public Pin[] newArray(int size) {
			return new Pin[size];
		}
	};

	// Describe content of parcel
	public int describeContents() {
		return 0;
	}

	@Override
	public int compareTo(Pin pin) {
		if(date.getValue() == pin.date.getValue()) {
			return 0;
		} else if(date.getValue() > pin.date.getValue()) {
			return -1;
		} else {
			return 1;
		}
	}
}
