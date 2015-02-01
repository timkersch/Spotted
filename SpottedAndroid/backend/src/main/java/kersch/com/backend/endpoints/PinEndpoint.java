package kersch.com.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.GeoPt;
import kersch.com.backend.records.PinRecord;

import javax.inject.Named;

import java.util.Date;
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

	@ApiMethod(name = "registerpin")
	public void registerPin(@Named("latitude") float latitude,
	                           @Named("longitude") float longitude, @Named("message") String message) {

		PinRecord record = new PinRecord();
		record.setGeoPoint(latitude, longitude);
		record.setMessage(message);
		record.setTimeStamp(new Date(System.currentTimeMillis()));

		ofy().save().entity(record).now();
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
}