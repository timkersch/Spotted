package kersch.com.spotted.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.*;

import kersch.com.spotted.R;
import kersch.com.spotted.activities.MapActivity;
import kersch.com.spotted.model.Pin;
import kersch.com.spotted.model.PinListViewAdapter;

import java.util.List;

/**
 * A fragment representing a list of Pins.
 */
public class PinListFragment extends ListFragment {

	private OnFragmentInteractionListener fragmentInteractionListener;

	public PinListFragment() {
		// Mandatory empty constructor
	}

	// Use this method to pass arguments to fragment
	public static PinListFragment newInstance() {
		PinListFragment fragment = new PinListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		this.updateList();
	}

	private void updateList() {
		List<Pin> pinList = ((MapActivity)getActivity()).getPinList();
		setListAdapter(new PinListViewAdapter(getActivity(), R.layout.fragment_list_item, pinList));
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
