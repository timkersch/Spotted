package kersch.com.spotted.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.pinService.PinService;
import kersch.com.backend.pinService.model.CollectionResponsePinRecord;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-20
 * Time: 00:08
 */
public class DbOperations {

	private static PinService pinService = null;
	private static Handler messageHandler = null;

	/** Method that initializes a pinservice if recuired and the returns it.
	 * @return a pinservice to communicate with the backend.
	 */
	public static synchronized PinService getPinService() {
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
		return pinService;
	}

	/** A method that loads pins from the database. The result is recieved in a handler
	 * defined in the other loadPinsFromDatabase method.
	 * @return true if a handler has been registered false otherwise.
	 */
	public static boolean loadPinsFromDatabase() {
		if(messageHandler == null) {
			return false;
		} else {
			loadPinsFromDatabase(messageHandler);
			return true;
		}
	}

	/** This method loads pins from a database and send the result in a message
	 * to the specified handler
	 * @param handler the handler that recieves the pins.
	 */
	public static void loadPinsFromDatabase(final Handler handler) {
		if(messageHandler == null) {
			messageHandler = handler;
		}
		new AsyncTask<Void, Void, List<Pin>>() {
			@Override
			protected List<Pin> doInBackground(Void... params) {
				List<Pin> pinList = null;
				try {
					CollectionResponsePinRecord cp = getPinService().listPins().execute();
					List<PinRecord> pr = cp.getItems();
					if (pr != null) {
						pinList = new ArrayList<>(pr.size());
						for (int i = 0; i < pr.size(); i++) {
							pinList.add(new Pin(pr.get(i)));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return pinList;
			}

			@Override
			protected void onPostExecute(List<Pin> pins) {
				// Add pins to map
				Message m = Message.obtain();
				m.what = Constants.DATABASE_UPDATE_ID;
				m.obj = pins;
				handler.sendMessage(m);
			}
		}.execute();
	}
}
