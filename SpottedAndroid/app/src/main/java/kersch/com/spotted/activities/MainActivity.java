package kersch.com.spotted.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import kersch.com.spotted.gcmServices.GcmPin;
import kersch.com.spotted.gcmServices.*;
import kersch.com.spotted.R;
import kersch.com.spotted.utils.Constants;


public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sp = getSharedPreferences(MainActivity.class.getSimpleName(),Context.MODE_PRIVATE);

		if(getRegistrationId(sp).isEmpty()) {
			// Start Async task to Register device
			new GcmUser("Tim", "Kerschbaumer", "timkersch", "tim.kersch@gmail.com", "pass", this, sp).execute();
		} else {
			Toast.makeText(this, "Already registered with id " + sp.getString(Constants.PROPERTY_REG_ID, ""), Toast.LENGTH_LONG).show();
		}

		double latitude = 16.4553;
		double longitude = 64.6543;
		String message = "This is the first pin";

		// Start Async task to send location to server
		new GcmPin(latitude+50, longitude+50, message, this).execute(sp.getString(Constants.PROPERTY_REG_ID, ""));
		new GcmPin(latitude-60, longitude-50, message + " - 2", this).execute(sp.getString(Constants.PROPERTY_REG_ID, ""));

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
