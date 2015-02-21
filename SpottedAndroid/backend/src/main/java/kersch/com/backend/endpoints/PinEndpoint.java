package kersch.com.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.GeoPt;
import kersch.com.backend.records.ResponseRecord;
import kersch.com.backend.records.PinRecord;

import javax.inject.Named;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static kersch.com.backend.utils.OfyService.ofy;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 16:32
 */
@Api(name = "pinService", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.com.kersch", ownerName = "backend.com.kersch", packagePath = ""))
public class PinEndpoint {

	private static final Logger log = Logger.getLogger(PinEndpoint.class.getName());

	private static final String PIN_ADDED = "PIN_ADDED";
	private static final String LIKES_INCREMENTED = "LIKES_INCREMENTED";
	private static final String RESPONSE_ADDED = "RESPONSE_ADDED";
	private static final String PIN_REMOVED = "PIN_REMOVED";

	@ApiMethod(name = "registerPin")
	public PinRecord registerPin(@Named("title") String title,
	                        @Named("message") String message,
	                        @Named("lifetime") long lifetime,
	                        @Named("date") Date date,
	                        GeoPt geoPt) {

		PinRecord record = new PinRecord();
		record.setTitle(title);
		record.setLifeLengthInMilliseconds(lifetime);
		record.setGeoPoint(geoPt);
		record.setMessage(message);
		record.setTimeStamp(date);

		ofy().save().entity(record).now();
		sendUpdateMessage(PIN_ADDED);
		return record;
	}

	@ApiMethod(name = "incrementLikes")
	public void incrementLikes(@Named("entityId") long id) {
		PinRecord record = findRecord(id);
		if (record == null) {
			log.info("Pin does not exist");
			return;
		}
		record.incrementLikes();
		ofy().save().entity(record).now();
		sendUpdateMessage(LIKES_INCREMENTED);
	}

	@ApiMethod(name = "addResponse")
	public void addResponse(@Named("pinId") long pinId, @Named("response") String response, @Named("date") Date date) {
		ResponseRecord record = new ResponseRecord();
		record.setDate(date);
		record.setMessage(response);
		record.setBelongsToPinId(pinId);
		ofy().save().entity(record).now();
		sendUpdateMessage(RESPONSE_ADDED);
	}

	@ApiMethod(name = "removePin")
	public void removePin(@Named("entityId") long id) {
		PinRecord record = findRecord(id);
		if (record == null) {
			log.info("Pin does not exist");
			return;
		}
		ofy().delete().entity(record).now();
		sendUpdateMessage(PIN_REMOVED);
	}

	@ApiMethod(name = "getPinResponses")
	public CollectionResponse<ResponseRecord> getPinResponses(@Named("pinId") long belongsToPinId) {
		// TODO should use .filter() but it is not working?
		List<ResponseRecord> records = ofy().load().type(ResponseRecord.class).list();
		Iterator<ResponseRecord> it = records.iterator();
		while(it.hasNext()) {
			if(it.next().getBelongsToPinId() != belongsToPinId) {
				it.remove();
			}
		}
		return CollectionResponse.<ResponseRecord>builder().setItems(records).build();
	}

	/**
	 * Return a collection of pinned points
	 *
	 * @param count The number of points
	 * @return a list of Google Cloud pins
	 */
	@ApiMethod(name = "listPinsWithLimit")
	public CollectionResponse<PinRecord> listPinsWithLimit(@Named("count") int count) {
		List<PinRecord> records = ofy().load().type(PinRecord.class).limit(count).list();
		return CollectionResponse.<PinRecord>builder().setItems(records).build();
	}

	@ApiMethod(name = "findLocalPins")
	public CollectionResponse<PinRecord> findLocalPins(GeoPt geoPt) {
		// TODO user this method to filter only to local pins
		return null;
	}

	/**
	 * Return a collection of pinned points
	 *
	 * @return a list of Google Cloud pins
	 */
	@ApiMethod(name = "listPins")
	public CollectionResponse<PinRecord> listPins() {
		List<PinRecord> records = ofy().load().type(PinRecord.class).list();
		return CollectionResponse.<PinRecord>builder().setItems(records).build();
	}

	private PinRecord findRecord(long id) {
		return ofy().load().type(PinRecord.class).filter("id", id).first().now();
	}

	private void sendUpdateMessage(String message) {
		try {
			//TODO
			new MessagingEndpoint().sendMessage(message);
		} catch (IOException e) {
			// TODO
		}
	}

	private void removePin(PinRecord record) {
		ofy().delete().entity(record).now();
		sendUpdateMessage(PIN_REMOVED);
	}

	/*private void threadSchedueler(final PinRecord record, @Named("waitTime") final long waitTime) {
		Thread schedThread = ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				try {
					new Object().wait(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				removePin(record);
			}
		});
		schedThread.start();
	} */
}