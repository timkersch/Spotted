package kersch.com.backend.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import kersch.com.backend.records.UserRecord;

import javax.inject.Named;

import java.util.logging.Logger;

import static kersch.com.backend.utils.OfyService.ofy;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 16:32
 */
@Api(name = "receiver", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.com.kersch", ownerName = "backend.com.kersch", packagePath = ""))
public class ReceiveEndpoint {

	private static final Logger log = Logger.getLogger(ReceiveEndpoint.class.getName());

	@ApiMethod(name = "receive")
	public void receiveMessage(@Named("id") String id, @Named("message") String message) {
		log.info("Id: " + id + " - Message: " + message);
		UserRecord userRecord = new UserRecord();
		userRecord.setFirstName(message);
		ofy().save().entity(userRecord).now();

	}
}
