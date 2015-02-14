package kersch.com.spotted.activities;

import android.content.BroadcastReceiver;
import android.location.Location;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import kersch.com.backend.pinService.model.CollectionResponsePinRecord;
import kersch.com.backend.pinService.model.GeoPt;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.R;
import kersch.com.spotted.model.Pin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapActivity extends FragmentActivity {

	private GoogleMap mMap;
	private Location myLocation;
	private Map<Marker, Pin> pinMarkerMap = new HashMap<>();
	private List<Pin> pins = new ArrayList<>();
	private static Semaphore semaphore = new Semaphore(1,true);
	private AsyncTask<Void, Void, Void> task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Pin.initPinService();
		try {
			setUpMapIfNeeded();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			setUpMapIfNeeded();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.
	 * <p/>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p/>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() throws InterruptedException {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				loadPinsFromDatabase();
				try {
					task.get(10000, TimeUnit.MILLISECONDS);
				} catch (ExecutionException e) {

				} catch (TimeoutException e) {

				}
			}
		}
	}

	private void addLocationMarker(String title, String message, long lifetimeInMilliseconds) {
		Pin pin = new Pin(57.682800f, 11.9790009f, title, message,lifetimeInMilliseconds);
		pinMarkerMap.put(mMap.addMarker(pin.getMarkerOptions()), pin);
	}

	private void addCustomMarker(String title, String message, long lifetimeInMilliseconds, LatLng location) {
		Pin pin = new Pin((float)location.latitude, (float)location.longitude, title, message, lifetimeInMilliseconds);
		pinMarkerMap.put(mMap.addMarker(pin.getMarkerOptions()), pin);
	}

	private void removeMarker(Marker marker) {
		if(pinMarkerMap.containsKey(marker)) {
			marker.remove();
			pinMarkerMap.get(marker).removePin();
			pinMarkerMap.remove(marker);
		}
	}

	// Result in instance variable "pins"
	private void loadPinsFromDatabase() {
		task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				List<Pin> pinList = null;
				try {
					CollectionResponsePinRecord cp = Pin.getPinService().listPins().execute();
					List<PinRecord> pr = cp.getItems();
					pinList = new ArrayList<>(pr.size());
					for (int i = 0; i < pr.size(); i++) {
						pinList.add(new Pin(pr.get(i)));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				pins = pinList;
				//for (Pin pin : pinList) {
				//	pinMarkerMap.put(mMap.addMarker(pin.getMarkerOptions()), pin);
				//}
				return null;
			}
		}.execute();
	}
}
