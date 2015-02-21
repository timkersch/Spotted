package kersch.com.spotted.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import kersch.com.spotted.R;
import kersch.com.spotted.utils.Utils;

import java.util.List;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-21
 * Time: 02:12
 */
public class CommentListViewAdapter extends ArrayAdapter<Response> {
	Context context;

	public CommentListViewAdapter(Context context, int resourceId, List<Response> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* Private view holder class*/
	private class ViewHolder {
		TextView date;
		TextView message;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Response response = getItem(position);

		final LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_list_comments, parent, false);
			holder = new ViewHolder();
			holder.date = (TextView) convertView.findViewById(R.id.response_date);
			holder.message = (TextView) convertView.findViewById(R.id.response_message);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.date.setText(Utils.getFormatedDate(response.getDate()));
		holder.message.setText(response.getMessage());

		return convertView;
	}
}
