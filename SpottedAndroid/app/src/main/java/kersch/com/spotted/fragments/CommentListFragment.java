package kersch.com.spotted.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import kersch.com.spotted.R;
import kersch.com.spotted.model.CommentListViewAdapter;
import kersch.com.spotted.model.Pin;

/**
 * A fragment representing a list of Comments.
 */
public class CommentListFragment extends ListFragment {

	private static final String PARAM = "Pin";

	private OnFragmentInteractionListener mListener;

	private Pin pin;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CommentListFragment() {
	}

	public static CommentListFragment newInstance(Pin pin) {
		CommentListFragment fragment = new CommentListFragment();
		Bundle args = new Bundle();
		args.putParcelable(PARAM, pin);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			pin = getArguments().getParcelable(PARAM);
		}
		updateList();
	}

	private void updateList() {
		setListAdapter(new CommentListViewAdapter(getActivity(), R.layout.fragment_list_comments, pin.getResponses()));
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


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
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
