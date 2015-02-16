package kersch.com.spotted.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import kersch.com.spotted.R;
import kersch.com.spotted.activities.MapActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment implements View.OnClickListener {

	private OnFragmentInteractionListener fragmentInteractionListener;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 * @return A new instance of fragment AddFragment.
	 */
	public static AddFragment newInstance() {
		AddFragment fragment = new AddFragment();
		return fragment;

	}

	public AddFragment() {
		// Required empty public constructor
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
		Button commit = (Button)view.findViewById(R.id.commit_pin);
		Button cancel = (Button)view.findViewById(R.id.cancel_pin);
		commit.setOnClickListener(this);
		cancel.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.commit_pin) {
			EditText title = (EditText)getView().findViewById(R.id.enter_title);
			EditText message = (EditText)getView().findViewById(R.id.enter_message);
			((MapActivity)getActivity()).addMarkerToMap(title.getText().toString(), message.getText().toString(), 0);
			getFragmentManager().popBackStackImmediate();
		} else if(v.getId() == R.id.cancel_pin) {
			getFragmentManager().popBackStackImmediate();
		} else {

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

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (fragmentInteractionListener != null) {
			fragmentInteractionListener.onFragmentInteraction(uri);
		}
	}

}
