package kersch.com.spotted.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

	private static final String ARG_PARAM = "PINLIST";

	private OnFragmentInteractionListener mListener;
	private List<Pin> pinList;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PinListFragment() {
	}

	public static PinListFragment newInstance(List<Pin> pinList) {
		PinListFragment fragment = new PinListFragment();
		// TODO
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

/*	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_list_item, container, false);
	}*/

	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		updateList();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void updateList() {
		pinList = ((MapActivity)getActivity()).getPinList();
		setListAdapter(new PinListViewAdapter(getActivity(), R.layout.fragment_list_item, pinList));
	}

	public boolean updateList(List<Pin> pinList) {
		boolean fragmentCreated = false;
		if(getActivity() != null) {
			this.pinList = pinList;
			setListAdapter(new PinListViewAdapter(getActivity(), R.layout.fragment_list_item, pinList));
			fragmentCreated = true;
		}
		return fragmentCreated;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			//TODO
			//mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
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
