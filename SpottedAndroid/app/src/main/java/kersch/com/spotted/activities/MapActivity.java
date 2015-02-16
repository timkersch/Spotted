package kersch.com.spotted.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
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
import kersch.com.spotted.model.Pin;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

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
	private PinListFragment pinListFragment;

	// TODO
	private String lastUpdateTime;
	// TODO
	private boolean requestingLocationUpdates = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

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
		addMarkerListener();
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

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.
	 */
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

	private void initFabButton() {
		FloatingActionButton addMarkerButton = (FloatingActionButton)findViewById(R.id.add_marker);
		addMarkerButton.setColorNormal(Color.parseColor("#C40000"));
		addMarkerButton.setColorPressed((Color.parseColor("#DE5050")));
		View.OnClickListener addListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO what to be added
				//addMarkerToMap("This is a marker", "Hello this is a message", 100);
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

					ListFragment fragment = new ListFragment();
					ft.add(R.id.list_frame, fragment);
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

	private void addMarkerListener() {
		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO
				return false;
			}
		});
	}

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

	public void addMarkerToMap(String title, String message, long lifetimeInMilliseconds) {
		addMarkerToMap(title, message, lifetimeInMilliseconds, currentLocation);
	}

	private void addMarkerToMap(String title, String message, long lifetimeInMilliseconds, Location location) {
		Pin pin = new Pin((float)location.getLatitude(), (float)location.getLongitude(), title, message, lifetimeInMilliseconds);
		addMarker(pin);
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

	private void addMarker(Pin pin) {
		pinMarkerMap.put(map.addMarker(pin.getMarkerOptions()), pin);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
		lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
	}

	private void removeMarker(Marker marker) {
		if(pinMarkerMap.containsKey(marker)) {
			marker.remove();
			pinMarkerMap.get(marker).removePin();
			pinMarkerMap.remove(marker);
		}
	}

	// Result in instance variable "pins"
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

	@Override
	public void onFragmentInteraction(String id) {
		// TODO
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO
	}
}
