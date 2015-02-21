package kersch.com.backend.records;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-21
 * Time: 01:09
 */
@Entity
public class ResponseRecord {
	@Id
	private Long id;

	@Index
	private Long belongsToPinId;

	private String message;

	private Date date;

	public void setBelongsToPinId(Long id) {
		this.belongsToPinId = id;
	}

	public Long getBelongsToPinId() {
		return belongsToPinId;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return this.date;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
