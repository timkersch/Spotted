package kersch.com.spotted.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import kersch.com.spotted.R;

import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-15
 * Time: 20:19
 */
public class PinListViewAdapter extends ArrayAdapter<Pin> {
	Context context;

	public PinListViewAdapter(Context context, int resourceId, List<Pin> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* Private view holder class*/
	private class ViewHolder {
		ImageButton marker;
		TextView title;
		TextView distance;
		TextView date;
		TextView expires;
		TextView message;
		Button likesButton;
		Button commentButton;
		Button likeButton;
		Button commentsButton;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Pin pin = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_list_item, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.marker = (ImageButton) convertView.findViewById(R.id.pin_icon);
			holder.likesButton = (Button) convertView.findViewById(R.id.likes);
			holder.commentButton = (Button) convertView.findViewById(R.id.comments);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.expires = (TextView) convertView.findViewById(R.id.expire_time);
 			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(pin.getTitle());
		holder.message.setText(pin.getMessage());
		holder.marker.setImageResource(pin.getPinDrawableId());

		return convertView;
	}
}
