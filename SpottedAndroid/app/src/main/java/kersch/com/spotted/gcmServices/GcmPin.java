package kersch.com.spotted.gcmServices;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.pin.Pin;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:34
 */
public class GcmPin extends AsyncTask<String, Void, Void> {
	private static Pin pinService;
	private static Semaphore semaphore;
	private Location location;
	private String message;
	private AtomicInteger msgId;
	private GoogleCloudMessaging gcm;
	private Context context;

	public GcmPin(Location location, String message, Context context) {
		this(location.getLatitude(), location.getLongitude(), message, context);
	}

	public GcmPin(double latitude, double longitude, String message, Context context) {
		location = new Location("Provider");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		msgId = new AtomicInteger();
		semaphore = new Semaphore(1, true);
		this.message = message;
		this.context = context;
	}

	public Location getLocation() {
		return location;
	}

	public String getMessage() {
		return message;
	}

	@Override
	protected Void doInBackground(String... params)  {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		initPinService();
		semaphore.release();
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}

			String regid = params[0];

			Bundle data = new Bundle();
			data.putString("regId", regid);
			data.putString("message", message);
			data.putDouble("latitude", location.getLatitude());
			data.putDouble("longitude", location.getLongitude());

			String id = Integer.toString(msgId.incrementAndGet());
			gcm.send(Constants.SENDER_ID + "@gcm.googleapis.com", id, data);

			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pinService.registerpin(regid, location.getLatitude(), location.getLongitude(), message).execute();
			semaphore.release();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void initPinService() {
		if (pinService == null) {
			Pin.Builder builder;

			if(Constants.LOCAL == true) {
				// Code to run local
				builder = new Pin.Builder(AndroidHttp.newCompatibleTransport(),
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
				builder = new Pin.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
						.setRootUrl("https://black-alpha-838.appspot.com/_ah/api/");
			}

			pinService = builder.build();
		}
	}
}
