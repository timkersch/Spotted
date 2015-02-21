package kersch.com.spotted.appEngineServices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MapActivity;
import kersch.com.spotted.utils.NoMessageHandlerRegisteredException;

/** Class that handles messages from Google cloud messaging
 *
 */
public class GcmIntentService extends IntentService {

	private static final String PIN_ADDED = "PIN_ADDED";
	private static final String LIKES_INCREMENTED = "LIKES_INCREMENTED";
	private static final String RESPONSE_ADDED = "RESPONSE_ADDED";
	private static final String PIN_REMOVED = "PIN_REMOVED";

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
				updateDevice(extras.getString("message"));
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	protected void updateDevice(String message) {
		if(message.equals(PIN_ADDED)) {
			tryLoadPins("Spotted", "Someone spotted something!");
		} else if(message.equals(PIN_REMOVED)) {
			// TODO
		} else if(message.equals(LIKES_INCREMENTED)) {
			// TODO
		} else if(message.equals(RESPONSE_ADDED)) {
			tryLoadPins("Spotted", "Someone responded to a spot!");
		} else {

		}
	}

	private void tryLoadPins(String notificationTitle, String notificationMessage) {
		try {
			DbOperations.loadPinsFromDatabase();
		} catch (NoMessageHandlerRegisteredException e) {
			// Not in application. Show push notification.
			NotificationManager mNotificationManager = (NotificationManager)
					this.getSystemService(Context.NOTIFICATION_SERVICE);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MapActivity.class), 0);

			// TODO
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setContentTitle(notificationTitle)
							// TODO set marker
					.setSmallIcon(R.drawable.yellowmarker_large)
					.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
					.setContentText(notificationMessage);

			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(1, mBuilder.build());
		}
	}
}