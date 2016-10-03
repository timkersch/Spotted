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
	// The message of the response
	private String message;
	// The date of the response
	private DateTime date;

	// Pass a message to create an instance of this class
	protected Response(String message) {
		this.message = message;
		// Date is generated
		this.date = new DateTime(System.currentTimeMillis());
	}

	// This constructor is used to reconstruct Response objects from database
	protected Response(ResponseRecord response) {
		this.message = response.getMessage();
		this.date = response.getDate();
	}

	// Private constructor to reconstruct from parcel
	private Response(Parcel in) {
		message = in.readString();
		long time = in.readInt();
		date = new DateTime(time);
	}

	/** Returns the date of this response.
	 */
	public DateTime getDate() {
		return date;
	}

	/** Returns the message of this response.
	 */
	public String getMessage() {
		return message;
	}

	// Used to describe parcelable contents
	public int describeContents() {
		return 0;
	}

	// Write object data to parcel
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(message);
		out.writeLong(date.getValue());
	}

	// Calls private constructor to regenerate object.
	public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
		public Response createFromParcel(Parcel in) {
			return new Response(in);
		}

		public Response[] newArray(int size) {
			return new Response[size];
		}
	};
}
