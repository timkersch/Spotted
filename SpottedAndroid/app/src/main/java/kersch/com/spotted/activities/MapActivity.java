package kersch.com.spotted.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import kersch.com.backend.pinService.model.CollectionResponsePinRecord;
import kersch.com.backend.pinService.model.PinRecord;
import kersch.com.spotted.R;
import kersch.com.spotted.fragments.AddFragment;
import kersch.com.spotted.fragments.PinListFragment;
import kersch.com.spotted.gcmServices.GcmRegistration;
import kersch.com.spotted.model.Pin;
import kersch.com.spotted.utils.Constants;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener,
		PinListFragment.OnFragmentInteractionListener,
		AddFragment.OnFragmentInteractionListener {

	private final Map<Marker, Pin> pinMarkerMap = new HashMap<>();
	private GoogleMap map;
	private GoogleApiClient googleApiClient;
	private Location currentLocation;
	private LocationRequest locationRequest;

	// TODO
	private String lastUpdateTime;
	// TODO
	private boolean requestingLocationUpdates = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		SharedPreferences sp = getSharedPreferences(MapActivity.class.getSimpleName(), Context.MODE_PRIVATE);

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

		buildGoogleApiClient();
		Pin.initPinService();

		try {
			setUpMapIfNeeded();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		loadMarkersFromDatabase();
		initFabButton();
		initTabs();
		initMarkerListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
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

	@Override
	protected void onResume() {
		super.onResume();
		try {
			setUpMapIfNeeded();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (googleApiClient.isConnected() && !requestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	/** Add a marker to the map
	 * @param title the title of the marker
	 * @param message the message of the marker
	 * @param lifetimeInMilliseconds the time tha marker is supposed to live
	 */
	public void addMarkerToMap(String title, String message, long lifetimeInMilliseconds) {
		addMarkerToMap(title, message, lifetimeInMilliseconds, currentLocation);
	}

	/** Returns a list of pins added to the map.
	 * @return
	 */
	public List<Pin> getPinList() {
		return new ArrayList<>(pinMarkerMap.values());
	}

	// Setup map
	private void setUpMapIfNeeded() throws InterruptedException {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			map.getUiSettings().setMapToolbarEnabled(false);
			map.getUiSettings().setMyLocationButtonEnabled(true);
			map.getUiSettings().setCompassEnabled(true);
			map.setMyLocationEnabled(true);
			// Check if we were successful in obtaining the map.
			if (map != null) {
				createLocationRequest();
			}
		}
	}

	// Initialize the fab button
	private void initFabButton() {
		FloatingActionButton addMarkerButton = (FloatingActionButton)findViewById(R.id.add_marker);
		addMarkerButton.setColorNormal(Color.parseColor("#C40000"));
		addMarkerButton.setColorPressed((Color.parseColor("#DE5050")));
		View.OnClickListener addListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

				AddFragment fragment = new AddFragment();
				ft.add(R.id.fragment_container, fragment);
				ft.addToBackStack("addFragment");
				ft.commit();
			}
		};
		addMarkerButton.setOnClickListener(addListener);
	}

	// Initialize tabs
	private void initTabs() {
		TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();

		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals("map")) {
					// TODO
				} else {
					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					PinListFragment fragment = new PinListFragment();
					ft.replace(R.id.list_frame, fragment);
					ft.commit();
				}
			}
		});

		TabHost.TabSpec ts = tabHost.newTabSpec("map");
		ts.setContent(R.id.map_tab);
		ts.setIndicator("Map");
		tabHost.addTab(ts);

		ts = tabHost.newTabSpec("list");
		ts.setContent(R.id.list_tab);
		ts.setIndicator("List");
		tabHost.addTab(ts);
	}

	// Initialize a marker listener
	private void initMarkerListener() {
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO
				return false;
			}
		});
	}

	// Build the google api bclient
	protected synchronized void buildGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		if(!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
			googleApiClient.connect();
		}
	}

	// Private method that adds a marker with a custom location
	private void addMarkerToMap(String title, String message, long lifetimeInMilliseconds, Location location) {
		Pin pin = new Pin((float)location.getLatitude(), (float)location.getLongitude(), title, message, lifetimeInMilliseconds);
		addMarker(pin);
	}

	// This places the marker in the map
	private void addMarker(Pin pin) {
		pinMarkerMap.put(map.addMarker(pin.getMarkerOptions()), pin);
	}

	// Remove a marker from map, list and db
	private void removeMarker(Marker marker) {
		if(pinMarkerMap.containsKey(marker)) {
			marker.remove();
			pinMarkerMap.get(marker).removePin();
			pinMarkerMap.remove(marker);
		}
	}

	protected void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(5000);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (requestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("Connection", " Suspended! - " + i);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("Connection", "Failed! - " + connectionResult.toString());
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
	}

	// Result in map "pins"
	private void loadMarkersFromDatabase() {
		new AsyncTask<Void, Void, List<Pin>>() {
			@Override
			protected List<Pin> doInBackground(Void... params) {
				List<Pin> pinList = null;
				try {
					CollectionResponsePinRecord cp = Pin.getPinService().listPins().execute();
					List<PinRecord> pr = cp.getItems();
					if (pr != null) {
						pinList = new ArrayList<>(pr.size());
						for (int i = 0; i < pr.size(); i++) {
							pinList.add(new Pin(pr.get(i)));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return pinList;
			}

			@Override
			protected void onPostExecute(List<Pin> pinList) {
				// Add pins to map
				if (pinList != null) {
					for (Pin pin : pinList) {
						addMarker(pin);
					}
				}
			}
		}.execute();
	}

	private String getRegistrationId(SharedPreferences sp) {
		final SharedPreferences prefs = sp;
		String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");

		if (registrationId.isEmpty()) {
			return "";
		}
		return registrationId;
	}

	@Override
	public void onFragmentInteraction(String id) {
		// TODO
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO
	}
}
