package kersch.com.spotted.model;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.util.DateTime;
import kersch.com.backend.pinService.PinService;
import kersch.com.backend.pinService.model.GeoPt;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.R;
import kersch.com.spotted.utils.Constants;
import kersch.com.spotted.utils.RandomPins;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:34
 */
public class Pin implements Serializable {
	private static PinService pinService;

	// These Pin attributes are fix.
	private final GeoPt geoPt;
	private final String title;
	private final String message;
	private final long lifetimeInMilliseconds;
	private final DateTime date;
	private final int pinDrawableId;

	// These can be updated after creation
	private List<String> responses;
	// Initial likes
	private int likes;

	/** Create a new pin with a lat and longitude.
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param message
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

		addToDatabase();
	}

	/** Create a new Pin with a GeoPt.
	 * @param geoPt
	 * @param title
	 * @param message
	 */
	public Pin(GeoPt geoPt, String title, String message, long lifetimeInMilliseconds) {
		this(geoPt.getLatitude(), geoPt.getLongitude(), title, message, lifetimeInMilliseconds);
	}

	/** Create a new Pin from a PinRecord from the database
	 * @param pinRecord the pinrecord from the database
	 */
	public Pin(PinRecord pinRecord) {
		this.geoPt = pinRecord.getGeoPoint();
		this.title = pinRecord.getTitle();
		this.message = pinRecord.getMessage();
		this.lifetimeInMilliseconds = pinRecord.getLifeLengthInMilliseconds();
		this.likes = pinRecord.getLikes();
		this.date = pinRecord.getTimeStamp();
		this.pinDrawableId = RandomPins.getPinId();
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
	public void incrementLike() {
		likes++;
		// TODO update database
	}

	/** Method that adds a response to this pin.
	 * @param response
	 */
	public void addResponse(String response) {
		if(responses == null) {
			responses = new ArrayList<>();
		}
		responses.add(response);
		// TODO update database
	}

	public MarkerOptions getMarkerOptions() {
		MarkerOptions opt = new MarkerOptions();
		opt.position(toLatLng());
		opt.title(title);
		opt.snippet(message);
		opt.icon(BitmapDescriptorFactory.fromResource(pinDrawableId));
		return opt;
	}

	/** Returns the pin drawable id for this pin.
	 * @return
	 */
	public int getPinDrawableId() {
		return pinDrawableId;
	}

	/** This method removes a pin from the database.
	 */
	public void removePin() {
		removeFromDatabase();
	}

	// Updates this pins information to the database
	private void updateDatabase() {
		// TODO
	}

	/** Method that returns the pinService backend object.
	 * @return
	 */
	public static PinService getPinService() {
		return pinService;
	}

	// Convert GeoPt to LatLng
	private LatLng toLatLng() {
		return new LatLng((double)geoPt.getLatitude(), (double)geoPt.getLongitude());
	}

	// Adds a pin to the database
	private void addToDatabase() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				initPinService();
				try {
					pinService.registerpin(title, message, lifetimeInMilliseconds, date, geoPt).execute();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	private void removeFromDatabase() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				initPinService();
				try {
					pinService.removepin(title).execute();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	// Initalizes the pinService
	public static synchronized void initPinService() {
		if (pinService == null) {
			PinService.Builder builder;

			if(Constants.LOCAL == true) {
				// Code to run local
				builder = new PinService.Builder(AndroidHttp.newCompatibleTransport(),
						new AndroidJsonFactory(), null)
						// Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
						// otherwise they can be skipped
						.setRootUrl("http://10.0.2.2:8080/_ah/api/")
						.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
							@Override
							public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
									throws IOException {
								abstractGoogleClientRequest.setDisableGZipContent(true);
							}
						});
			} else {
				// Code to run on app engine
				builder = new PinService.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
						.setRootUrl("https://black-alpha-838.appspot.com/_ah/api/");
			}

			pinService = builder.build();
		}
	}
}
