package kersch.com.spotted.appEngineServices;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.pinService.PinService;
import kersch.com.backend.pinService.model.CollectionResponsePinRecord;
import kersch.com.backend.pinService.model.CollectionResponseResponseRecord;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.backend.registration.Registration;
import kersch.com.spotted.utils.NoMessageHandlerRegisteredException;
import kersch.com.spotted.model.Pin;
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
	private static Registration regService = null;
	private static Handler messageHandler = null;

	private static GoogleCloudMessaging gcm;
	private static SharedPreferences sharedPreferences;

	/** Method that initializes the pinservice if required and the returns it.
	 * @return a pinservice to communicate with the backend.
	 */
	public static synchronized PinService getPinService() {
		if (pinService == null) {
			PinService.Builder builder;

			// Check if
			if(Constants.LOCAL) {
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

	/** This method registers a device for receiving GoogleCloudMessaging. And saves the id in sp
	 * @param context
	 * @param sp a shared preference to store the registration id in.
	 */
	public static void registerForGcm(final Context context, final SharedPreferences sp) {
		sharedPreferences = sp;
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				initRegService();

				String regId = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}

					regId = gcm.register(Constants.SENDER_ID);

					// You should send the registration ID to your server over HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your app.
					// The request to your server should be authenticated if your app
					// is using accounts.
					regService.register(regId).execute();

				} catch (IOException ex) {
					ex.printStackTrace();
				}
				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {
				storeRegistrationId(regId);
			}
		}.execute();
	}

	/** Registers a handler to recieves messages from loadPinsFromDatabase.
	 * @return true if successfully registered the handler. false if a handler is already registered.
	 */
	public static boolean registerMessageHandler(final Handler handler) {
		if(messageHandler == null) {
			messageHandler = handler;
		} else {
			return false;
		}
		return true;
	}

	/** Unregisters the handler that recieves messages from loadPinsFromDatabase.
	 * @return true if successfully unregistered the handler. false if no handler is registered.
	 */
	public static boolean unregisterMessageHandler() {
		if(messageHandler == null) {
			return false;
		} else {
			messageHandler = null;
		}
		return true;
	}

	/** Returns true if a handler has registered for recieving messages. False otherwise.
	 */
	public static boolean isMessageHandlerRegistered() {
		return (messageHandler != null);
	}

	/** This method loads pins from a database and send the result in a message
	 * to the specified handler
	 * @throws kersch.com.spotted.utils.NoMessageHandlerRegisteredException if no handler has been registered
	 */
	public static void loadPinsFromDatabase() throws NoMessageHandlerRegisteredException {
		if(messageHandler == null) {
			throw new NoMessageHandlerRegisteredException("Not handler has been specified. Please call " +
					"registerForMessages with a handler before calling this method.");
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
							CollectionResponseResponseRecord cm = getPinService().getPinResponses(pr.get(i).getId()).execute();
							pinList.add(new Pin(pr.get(i), cm.getItems()));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return pinList;
			}

			@Override
			protected void onPostExecute(List<Pin> pins) {
				// Send pins to registered handler
				Message m = Message.obtain();
				m.what = Constants.DATABASE_UPDATE_ID;
				m.obj = pins;
				messageHandler.sendMessage(m);
			}
		}.execute();
	}

	// Initalize the registraton service
	private static synchronized void initRegService() {
		if (regService == null) {
			Registration.Builder builder;

			// Check if server is running on local machine
			if(Constants.LOCAL) {
				// Code to run local
				builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
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
				builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
						.setRootUrl("https://black-alpha-838.appspot.com/_ah/api/");
			}
			regService = builder.build();
		}
	}

	// Store registration id in sharedPreferences.
	private static void storeRegistrationId(String regId) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(Constants.PROPERTY_REG_ID, regId);
		editor.apply();
	}
}
