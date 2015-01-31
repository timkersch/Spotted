package kersch.com.spotted.gcmServices;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.registration.Registration;
import kersch.com.spotted.activities.MainActivity;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistration extends AsyncTask<Void, Void, String> {
	private static Registration regService = null;
	private GoogleCloudMessaging gcm;
	private Context context;
	private SharedPreferences sp;

	// Sender ID = Project number
	private static final String SENDER_ID = "14744998140";

	public GcmRegistration(Context context, SharedPreferences sp) {
		this.sp = sp;
		this.context = context;
	}

	@Override
	protected String doInBackground(Void... params) {
		if (regService == null) {
			Registration.Builder builder;

			if(MainActivity.LOCAL == true) {
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
				// end of optional local run code
			} else {
				builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
						.setRootUrl("https://black-alpha-838.appspot.com/_ah/api/");
			}

			regService = builder.build();
		}

		String msg = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}

			String regId = gcm.register(SENDER_ID);
			msg = regId;

			// You should send the registration ID to your server over HTTP,
			// so it can use GCM/HTTP or CCS to send messages to your app.
			// The request to your server should be authenticated if your app
			// is using accounts.
			regService.register(regId).execute();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return msg;
	}

	@Override
	protected void onPostExecute(String msg) {
		Toast.makeText(context, "Device registered with id: " + msg, Toast.LENGTH_LONG).show();
		storeRegistrationId(msg);
		Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
	}

	// Store registration id in sharedPreferences.
	private void storeRegistrationId(String regId) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(MainActivity.PROPERTY_REG_ID, regId);
		editor.commit();
	}
}