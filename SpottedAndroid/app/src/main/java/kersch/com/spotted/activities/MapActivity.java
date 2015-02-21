package kersch.com.spotted.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.maps.model.Marker;
import kersch.com.spotted.R;
import kersch.com.spotted.appEngineServices.DbOperations;
import kersch.com.spotted.fragments.AddFragment;
import kersch.com.spotted.fragments.CommentFragment;
import kersch.com.spotted.fragments.CommentListFragment;
import kersch.com.spotted.fragments.PinListFragment;
import kersch.com.spotted.model.Pin;
import kersch.com.spotted.utils.Constants;
import kersch.com.spotted.utils.NoMessageHandlerRegisteredException;

import java.text.DateFormat;
import java.util.*;

public class MapActivity extends FragmentActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener,
		PinListFragment.OnFragmentInteractionListener,
		AddFragment.OnFragmentInteractionListener,
		CommentListFragment.OnFragmentInteractionListener,
		CommentFragment.OnFragmentInteractionListener {

	private final Map<Marker, Pin> pinMarkerMap = new HashMap<>();
	private final FragmentManager fragmentManager = getFragmentManager();
	// This handler recieves messages when the database is updated
	private final Handler dbHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message.what == Constants.DATABASE_UPDATE_ID) {
				List<Pin> pinList = (List<Pin>) message.obj;
				updatePins(pinList);
			}
		}
	};
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

		if (getRegistrationId(sp).isEmpty()) {
			// Start Async task to Register device for Google cloud messages
			DbOperations.registerForGcm(this, sp);
		}

		// Build the google API client
		buildGoogleApiClient();

		try {
			// Set up the map
			setUpMapIfNeeded();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Register the handler for reciving messages
		DbOperations.registerMessageHandler(dbHandler);

		try {
			// Initially load pins form database
			DbOperations.loadPinsFromDatabase();
		} catch (NoMessageHandlerRegisteredException e) {
			e.printStackTrace();
		}

		// Initialize UI components and listeners.
		initUI();
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
		} else if (id == R.id.action_refresh) {
			try {
				DbOperations.loadPinsFromDatabase();
			} catch (NoMessageHandlerRegisteredException e) {
				Log.d(e.getMessage() + "", "");
			}
		} else if (id == R.id.action_filter) {
			// TODO
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (fragmentManager.getBackStackEntryCount() >= 1) {
			fragmentManager.popBackStackImmediate();
			fragmentManager.beginTransaction().commit();
		} else {
			super.onBackPressed();
		}
	}

	// Call methods for initializing the UI
	private void initUI() {
		initFabButton();
		initTabs();
		initMarkerListener();
	}

	/** Adds a commentListFragment (connected to the pin) to the screen.
	 * @param pin the pin that has the comments.
	 */
	public void addCommentListFragment(Pin pin) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		CommentListFragment clf = CommentListFragment.newInstance(pin);
		ft.add(R.id.fragment_container, clf);
		ft.addToBackStack("commentListFragment");
		ft.commit();
	}

	/** Adds a commentFragment (connected to the pin) to the screen
	 * @param pin the pin that is to be commented.
	 */
	public void addCommentFragment(Pin pin) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		CommentFragment cf = CommentFragment.newInstance(pin);
		ft.add(R.id.fragment_container, cf);
		ft.addToBackStack("commentFragment");
		ft.commit();
	}

	// Initialize the fab button
	private void initFabButton() {
		FloatingActionButton addMarkerButton = (FloatingActionButton) findViewById(R.id.add_marker);
		addMarkerButton.setColorNormal(Color.parseColor("#C40000"));
		addMarkerButton.setColorPressed((Color.parseColor("#DE5050")));
		View.OnClickListener addListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = fragmentManager.beginTransaction();
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
				if (tabId.equals("map")) {
					// TODO
				} else {
					FragmentTransaction ft = fragmentManager.beginTransaction();
					PinListFragment fragment = PinListFragment.newInstance(new ArrayList<>(pinMarkerMap.values()));
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

	/**
	 * Add a marker to the map
	 *
	 * @param title                  the title of the marker
	 * @param message                the message of the marker
	 * @param lifetimeInMilliseconds the time tha marker is supposed to live
	 */
	public void addPin(String title, String message, long lifetimeInMilliseconds) {
		addPin(title, message, lifetimeInMilliseconds, currentLocation);
	}

	// Private method that adds a marker with a custom location - Used for testing only
	private void addPin(String title, String message, long lifetimeInMilliseconds, Location location) {
		Pin pin = new Pin((float) location.getLatitude(), (float) location.getLongitude(), title, message, lifetimeInMilliseconds);
		addPin(pin);
	}

	// Private method that adds an already created pin to the map
	private void addPin(Pin pin) {
		pinMarkerMap.put(map.addMarker(pin.getMarkerOptions()), pin);
	}

	// Removes a marker from the map and from the pinMarkerMap
	private void removePin(Marker marker) {
		marker.remove();
		pinMarkerMap.remove(marker);
	}

	// Removes all markers from the map and clears the pinMarkerMap
	private void removeAllPins() {
		Set<Marker> markerList = pinMarkerMap.keySet();
		for (Marker m : markerList) {
			removePin(m);
		}
	}

	public void updatePins(List<Pin> pinList) {
		if (pinList != null) {
			for (Pin pin : pinList) {
				if (!pinMarkerMap.values().contains(pin)) {
					addPin(pin);
				}
			}
			for (Marker marker : pinMarkerMap.keySet()) {
				if (!pinList.contains(pinMarkerMap.get(marker))) {
					removePin(marker);
				}
			}
		} else {
			removeAllPins();
		}
	}

	// Setup map
	private void setUpMapIfNeeded() throws InterruptedException {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
	public void onLocationChanged(Location location) {
		currentLocation = location;
		lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
	}

	private String getRegistrationId(SharedPreferences sp) {
		final SharedPreferences prefs = sp;
		String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");

		if (registrationId.isEmpty()) {
			return "";
		}
		return registrationId;
	}

	// Build the google api bclient
	protected synchronized void buildGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API)
				.build();
		if (!googleApiClient.isConnecting() && !googleApiClient.isConnected()) {
			googleApiClient.connect();
		}
	}

	protected void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("Connection", " Suspended! - " + i);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("Connection", "Failed! - " + connectionResult.toString());
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
