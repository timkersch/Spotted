package kersch.com.spotted.appEngineServices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MapActivity;
import kersch.com.spotted.utils.NotRegisteredForMessagesException;

/** Class that handles messages from Google cloud messaging
 *
 */
public class GcmIntentService extends IntentService {

	public GcmIntentService() {
		super("black-alpha-838");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
			// Since we're not using two way messaging, this is all we really to check for
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				updateDevice(extras.);
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	protected void updateDevice() {
		try {
			DbOperations.loadPinsFromDatabase();
		} catch (NotRegisteredForMessagesException e) {
			// Not in application. Show push notification.
			NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MapActivity.class), 0);

			// TODO
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
						.setContentTitle("Spotted")
						.setSmallIcon(R.drawable.yellowmarker_large)
						.setStyle(new NotificationCompat.BigTextStyle().bigText("Someone spotted something!"))
						.setContentText("Someone spotted something!");

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(1, mBuilder.build());
		}
	}
}