package kersch.com.spotted.model;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.maps.model.LatLng;
import kersch.com.spotted.R;
import kersch.com.spotted.utils.Utils;

import java.util.Collections;
import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-15
 * Time: 20:19
 */
public class PinListViewAdapter extends ArrayAdapter<Pin> {
	private Context context;
	private final LatLng currentPosition;
	private OnButtonClickedListener listener;

	public PinListViewAdapter(Context context, int resourceId, List<Pin> items, LatLng currentPosition) {
		super(context, resourceId, items);
		this.currentPosition = currentPosition;
		this.context = context;
	}

	/* Private view holder class*/
	private class ViewHolder {
		private ImageButton marker;
		private TextView title;
		private TextView date;
		private TextView message;
		private Button likesButton;
		private Button commentButton;
		private Button likeButton;
		private Button commentsButton;
		// TODO
		private TextView distance;
		private TextView expires;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Pin pin = getItem(position);

		final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_list_pin, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.marker = (ImageButton) convertView.findViewById(R.id.pin_icon);
			holder.likesButton = (Button) convertView.findViewById(R.id.likes);
			holder.likeButton = (Button) convertView.findViewById(R.id.like);
			holder.commentsButton = (Button) convertView.findViewById(R.id.comments);
			holder.commentButton = (Button) convertView.findViewById(R.id.comment);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.expires = (TextView) convertView.findViewById(R.id.expire_time);
 			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		float[] distance = new float[3];
		Location.distanceBetween(currentPosition.latitude, currentPosition.longitude,
				pin.getLocation().getLatitude(), pin.getLocation().getLongitude(), distance);

		holder.title.setText(pin.getTitle());
		holder.message.setText(pin.getMessage());
		holder.marker.setImageResource(pin.getPinDrawableId());
		holder.date.setText(Utils.getFormatedDate(pin.getDate()));
		holder.likesButton.setText(pin.getLikes() + " Likes");
		holder.commentsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.commentsButtonClicked(pin);
			}
		});
		holder.commentsButton.setText(pin.getNumberOfResponses() + " Comments");
		holder.commentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.commentButtonClicked(pin);
			}
		});
		holder.likeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.likeButtonClicked(pin);
			}
		});

		holder.distance.setText("Distance: " + Math.round(distance[0]) + "m");

		return convertView;
	}

	/** Interface to have te ability to handle clicks in activity.
	 * Register an object to this interface with setButtonClickedListeners()
	 */
	public interface OnButtonClickedListener {
		public void commentsButtonClicked(Pin pin);
		public void commentButtonClicked(Pin pin);
		public void likeButtonClicked(Pin pin);
	}

	/** Set a OnButtonClickedListener
	 * @param listener the listener
	 */
	public void setButtonClickedListener(OnButtonClickedListener listener) {
		this.listener= listener;
	}
}
