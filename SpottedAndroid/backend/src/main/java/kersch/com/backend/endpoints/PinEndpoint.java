package kersch.com.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import kersch.com.backend.records.PinRecord;
import kersch.com.backend.records.RegistrationRecord;

import javax.inject.Named;

import java.util.List;
import java.util.logging.Logger;

import static kersch.com.backend.utils.OfyService.ofy;


/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 16:32
 */
@Api(name = "pin", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.com.kersch", ownerName = "backend.com.kersch", packagePath = ""))
public class PinEndpoint {

	private static final Logger log = Logger.getLogger(PinEndpoint.class.getName());

	@ApiMethod(name = "registerpin")
	public void registerPin(@Named("regId") String id, @Named("latitude") double latitude,
	                           @Named("longitude") double longitude, @Named("message") String message) {

		PinRecord record = new PinRecord();
		record.setRegId(id);
		record.setLatitude(latitude);
		record.setLongitude(longitude);
		record.setMessage(message);
		record.setTimeStamp(System.nanoTime());

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

	private PinRecord findRecord(String regId) {
		return ofy().load().type(PinRecord.class).filter("regId", regId).first().now();
	}
}