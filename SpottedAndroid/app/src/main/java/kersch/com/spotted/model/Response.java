package kersch.com.spotted.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.api.client.util.DateTime;
import kersch.com.backend.pinService.model.ResponseRecord;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-21
 * Time: 03:00
 */
public class Response implements Parcelable {
	private String message;
	private DateTime date;

	// Instances can only be created from the outer class.
	protected Response(String message) {
		this.message = message;
		this.date = new DateTime(System.currentTimeMillis());
	}

	protected Response(ResponseRecord response) {
		this.message = response.getMessage();
		this.date = response.getDate();
	}

	public DateTime getDate() {
		return date;
	}

	public String getMessage() {
		return message;
	}

	public int describeContents() {
		return 0;
	}

	// Write object data to parcel
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(message);
		out.writeLong(date.getValue());
	}

	// Read object data from parcel
	private Response(Parcel in) {
		message = in.readString();
		long time = in.readInt();
		date = new DateTime(time);
	}

	// All Parcelables must have a CREATOR that implements these two methods. Regenerates the object
	public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
		public Response createFromParcel(Parcel in) {
			return new Response(in);
		}

		public Response[] newArray(int size) {
			return new Response[size];
		}
	};
}
