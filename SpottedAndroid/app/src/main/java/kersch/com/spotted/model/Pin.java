package kersch.com.spotted.model;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.pinService.PinService;
import kersch.com.backend.pinService.model.GeoPt;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:34
 */
public class Pin {
	private static PinService pinService;
	private static final Semaphore semaphore = new Semaphore(1, true);

	// These Pin attributes are fix.
	private final GeoPt geoPt;
	private final String title;
	private final String message;
	private final long lifetimeInMilliseconds;

	// These can be updated after creation
	private List<String> responses;
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
		this.title = pinRecord.getTitle();
		this.message = pinRecord.getMessage();
		this.geoPt = pinRecord.getGeoPoint();
		this.likes = pinRecord.getLikes();
		this.lifetimeInMilliseconds = pinRecord.getLifeLengthInMilliseconds();
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
		return opt;
	}


	/** This method removes a pin from the database.
	 */
	public void removePin() {
		removeFromDatabase();
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
				try {
					semaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				initPinService();
				semaphore.release();
				try {
					try {
						semaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pinService.registerpin(title, message, lifetimeInMilliseconds, geoPt).execute();
					semaphore.release();

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
				try {
					semaphore.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				initPinService();
				semaphore.release();
				try {
					try {
						semaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					pinService.removepin(title).execute();
					semaphore.release();

				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return null;
			}
		}.execute();
	}

	// Updates this pins information to the database
	private void updateDatabase() {
	}

	// Initalizes the pinService
	public static void initPinService() {
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
