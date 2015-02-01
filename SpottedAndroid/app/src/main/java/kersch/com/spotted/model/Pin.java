package kersch.com.spotted.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.pinService.PinService;
import kersch.com.backend.pinService.model.CollectionResponsePinRecord;
import kersch.com.backend.pinService.model.GeoPt;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:34
 */
public class Pin extends AsyncTask<Void, Void, Void> {
	private static PinService pinService;
	private static Semaphore semaphore;
	private GeoPt geoPt;
	private String message;
	private Context context;

	public Pin(float latitude, float longitude, String message, Context context) {
		this.geoPt = new GeoPt();
		this.geoPt.setLatitude(latitude);
		this.geoPt.setLongitude(longitude);
		this.message = message;
		this.context = context;
		semaphore = new Semaphore(1, true);
	}

	public Pin(GeoPt geoPt, String message, Context context) {
		this(geoPt.getLatitude(), geoPt.getLongitude(), message, context);
	}

	public GeoPt getLocation() {
		return this.geoPt;
	}

	public String getMessage() {
		return message;
	}

	@Override
	protected Void doInBackground(Void... params)  {
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
			pinService.registerpin(geoPt.getLatitude(), geoPt.getLongitude(), message).execute();
			semaphore.release();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void initPinService() {
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

	// TODO
	public void printPins() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					CollectionResponsePinRecord cp = pinService.listPins().execute();
					List<PinRecord> pr = cp.getItems();
					for (int i = 0; i < pr.size(); i++) {
						Log.d("Pin Mes: ", pr.get(i).getMessage());
						Log.d("Pin Tim: ", pr.get(i).getTimeStamp().toString() + "");
						Log.d("Pin Lat: ", pr.get(i).getGeoPoint().getLatitude() + "");
						Log.d("Pin Lon: ", pr.get(i).getGeoPoint().getLongitude() + "");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute();
	}
}
