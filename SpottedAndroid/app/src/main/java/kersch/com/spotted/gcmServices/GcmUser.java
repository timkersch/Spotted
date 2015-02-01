package kersch.com.spotted.gcmServices;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.registration.Registration;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmUser extends AsyncTask<String, Void, String> {
	private static Registration regService = null;
	private GoogleCloudMessaging gcm;
	private Context context;
	private SharedPreferences sp;

	private String firstName;
	private String lastName;
	private String userName;
	private String eMail;
	private String password;

	private AtomicInteger msgId;

	public GcmUser(String firstName, String lastName, String userName, String eMail, String password, Context context, SharedPreferences sp) {
		this(userName, eMail, password, context, sp);
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public GcmUser(String userName, String eMail, String password, Context context, SharedPreferences sp) {
		this.userName = userName;
		this.eMail = eMail;
		this.password = password;
		this.sp = sp;
		this.context = context;
		this.msgId = new AtomicInteger();
	}

	@Override
	protected String doInBackground(String... params) {
		initRegService();

		String regId = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}

			Bundle data = new Bundle();
			data.putString("firstName", firstName);
			data.putString("lastName", lastName);
			data.putString("userName", userName);
			data.putString("eMail", eMail);
			data.putString("password", password);

			regId = gcm.register(Constants.SENDER_ID);
			String id = Integer.toString(msgId.incrementAndGet());
			gcm.send(Constants.SENDER_ID + "@gcm.googleapis.com", id, data);

			// You should send the registration ID to your server over HTTP,
			// so it can use GCM/HTTP or CCS to send messages to your app.
			// The request to your server should be authenticated if your app
			// is using accounts.
			regService.register(regId, firstName, lastName, userName, eMail, password).execute();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return regId;
	}

	@Override
	protected void onPostExecute(String regId) {
		Toast.makeText(context, "Device registered with id: " + regId, Toast.LENGTH_LONG).show();
		storeRegistrationId(regId);
		Logger.getLogger("REGISTRATION").log(Level.INFO, regId);
	}

	// Store registration id in sharedPreferences.
	private void storeRegistrationId(String regId) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Constants.PROPERTY_REG_ID, regId);
		editor.commit();
	}

	private void initRegService() {
		if (regService == null) {
			Registration.Builder builder;

			if(Constants.LOCAL == true) {
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
}