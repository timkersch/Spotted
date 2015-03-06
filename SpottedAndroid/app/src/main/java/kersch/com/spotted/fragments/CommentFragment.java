package kersch.com.spotted.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MainActivity;
import kersch.com.spotted.model.Pin;

/**
 * A Fragment that shows a comment view.
 */
public class CommentFragment extends Fragment implements View.OnClickListener {
	private static final String PARAM = "Pin";

	private OnFragmentInteractionListener mListener;

	private Pin pin;

	private Button commit;
	private Button cancel;
	private TextView comment;

	public CommentFragment() {
		// Mandatory empty constructor
	}

	public static CommentFragment newInstance(Pin pin) {
		CommentFragment fragment = new CommentFragment();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comment, container, false);

		commit = (Button) view.findViewById(R.id.commit_comment);
		cancel = (Button) view.findViewById(R.id.cancel_comment);
		comment = (TextView) view.findViewById(R.id.comment_field);
		cancel.setOnClickListener(this);
		commit.setOnClickListener(this);

		return view;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
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
	public void onClick(View v) {
		MainActivity activity = (MainActivity)getActivity();
		if(v.getId() == commit.getId()) {
			if(comment.getText() != null && comment.getText().length() > 0) {
				activity.addResponse(pin, comment.getText() + "");
				activity.hideKeyboard();
				getFragmentManager().popBackStackImmediate();
			}
		} else if(v.getId() == cancel.getId()) {
			activity.hideKeyboard();
			getFragmentManager().popBackStackImmediate();
		} else {
			// TODO
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
		public void onFragmentInteraction(Uri uri);
	}

}
