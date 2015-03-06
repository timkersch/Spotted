package kersch.com.spotted.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MainActivity;

/**
 * A Fragment that shows an addView
 */
public class AddFragment extends Fragment implements View.OnClickListener {

	private OnFragmentInteractionListener fragmentInteractionListener;

	public AddFragment() {
		// Mandatory empty constructor
	}

	/** Use this method to get new instance of this class. */
	public static AddFragment newInstance() {
		AddFragment fragment = new AddFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
		// Initialize spinner
		Spinner spinner = (Spinner) getView().findViewById(R.id.lifetime_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.lifetime_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_add, container, false);

		// Get Buttons and set listeners
		Button commit = (Button) view.findViewById(R.id.commit_pin);
		Button cancel = (Button) view.findViewById(R.id.cancel_pin);
		commit.setOnClickListener(this);
		cancel.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		MainActivity activity = (MainActivity)getActivity();
		if (v.getId() == R.id.commit_pin) {
			EditText title = (EditText) getView().findViewById(R.id.enter_title);
			EditText message = (EditText) getView().findViewById(R.id.enter_message);
			// TODO the lifetime
			activity.addPin(title.getText().toString(), message.getText().toString(), 30000);
			activity.hideKeyboard();
			getFragmentManager().popBackStackImmediate();
		} else if (v.getId() == R.id.cancel_pin) {
			activity.hideKeyboard();
			getFragmentManager().popBackStackImmediate();
		} else {
			// TODO
		}
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

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (fragmentInteractionListener != null) {
			fragmentInteractionListener.onFragmentInteraction(uri);
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
