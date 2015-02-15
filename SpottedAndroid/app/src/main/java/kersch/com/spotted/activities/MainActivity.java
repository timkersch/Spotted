package kersch.com.spotted.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import kersch.com.spotted.model.Pin;
import kersch.com.spotted.gcmServices.*;
import kersch.com.spotted.R;
import kersch.com.spotted.utils.Constants;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sp = getSharedPreferences(MainActivity.class.getSimpleName(),Context.MODE_PRIVATE);

		if(getRegistrationId(sp).isEmpty()) {
			// Start Async task to Register device
			AsyncTask<Void, Void, String> task = new GcmRegistration(this, sp);
			try {
				task.get(3000, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {

			} catch (ExecutionException e) {

			} catch (InterruptedException e) {

			}
		}

		Intent intent = new Intent(this, MapActivity.class);
		this.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private String getRegistrationId(SharedPreferences sp) {
		final SharedPreferences prefs = sp;
		String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");

		if (registrationId.isEmpty()) {
			return "";
		}
		return registrationId;
	}

}
