package kersch.com.spotted.appEngineServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
				// TODO
				updateDevice("msg");
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	protected void updateDevice(String msg) {
		try {
			DbOperations.loadPinsFromDatabase();
		} catch (NotRegisteredForMessagesException e) {
			// Not in application. Show push notification.

			/*NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MapActivity.class), 0);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
						.setContentTitle("GCM Notification")
						.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(msg))
						.setContentText(msg);

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(1, mBuilder.build()); */
		}
	}
}