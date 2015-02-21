package kersch.com.spotted.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MapActivity;
import kersch.com.spotted.model.Pin;
import kersch.com.spotted.model.PinListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Pins.
 */
public class PinListFragment extends ListFragment implements PinListViewAdapter.OnCommentsButtonClicked {

	private static final String PARAM = "PinList";

	private OnFragmentInteractionListener fragmentInteractionListener;

	private List<Pin> pinList;

	public PinListFragment() {
		// Mandatory empty constructor
	}

	// Use this method to pass arguments to fragment
	public static PinListFragment newInstance(ArrayList<Pin> pinList) {
		PinListFragment fragment = new PinListFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList(PARAM, pinList);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			pinList = getArguments().getParcelableArrayList(PARAM);
		}
		updateList();
	}

	private void updateList() {
		PinListViewAdapter adapter = new PinListViewAdapter(getActivity(), R.layout.fragment_list_pin, pinList);
		adapter.setOnCommentsButtonClickedListener(this);
		setListAdapter(adapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			fragmentInteractionListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		fragmentInteractionListener = null;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != fragmentInteractionListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			//fragmentInteractionListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
		}
	}

	@Override
	public void commentsButtonClicked(Pin pin) {
		MapActivity activity = (MapActivity) getActivity();
		activity.addCommentsFragment(pin);
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(String id);
	}

}
