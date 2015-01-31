package kersch.com.spotted.gcmServices;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import kersch.com.backend.receiver.Receiver;
import kersch.com.spotted.activities.MainActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 16:01
 */
public class GcmMessageSender extends AsyncTask<String, Void, String> {

	private static final String SENDER_ID = "14744998140";
	private static Receiver recService = null;

	private GoogleCloudMessaging gcm;
	private Context context;
	private AtomicInteger msgId;

	public GcmMessageSender(Context context) {
		super();
		this.context = context;
		this.msgId = new AtomicInteger();
	}

	@Override
	protected String doInBackground(String... params) {
		if (recService == null) {
			Receiver.Builder builder;
			if(MainActivity.LOCAL == true) {
				// For local debugging
				builder = new Receiver.Builder(AndroidHttp.newCompatibleTransport(),
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
				// For app engine debugging
				builder = new Receiver.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
						.setRootUrl("https://black-alpha-838.appspot.com/_ah/api/");
			}

			recService = builder.build();
		}

		String key = params[0];
		String msg = params[1];
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}
			Bundle data = new Bundle();
			data.putString(key, msg);
			String id = Integer.toString(msgId.incrementAndGet());
			gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);

			recService.receive(id, msg).execute();

			Log.d("Sent message: ", msg);
		} catch (IOException ex) {
			Log.d("Error: ", ex.getMessage());
		}
		return msg;
	}
}
