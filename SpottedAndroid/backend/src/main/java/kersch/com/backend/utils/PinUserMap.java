package kersch.com.backend.utils;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.CollectionResponse;
import kersch.com.backend.records.PinRecord;
import kersch.com.backend.records.RegistrationRecord;

import javax.inject.Named;
import java.util.Iterator;
import java.util.List;

import static kersch.com.backend.utils.OfyService.ofy;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-01
 * Time: 00:36
 */
public class PinUserMap {

	public static List<PinRecord> findPins(RegistrationRecord user) {
		return findPins(user.getRegId());
	}

	public static List<PinRecord> findPins(String regId) {
		List<PinRecord> records = ofy().load().type(PinRecord.class).list();
		Iterator<PinRecord> it = records.iterator();
		while(it.hasNext()) {
			if(!(it.next().getRegId().equals(regId))) {
				it.remove();
			}
		}
		return records;
	}

	public static RegistrationRecord findUser(PinRecord pinRecord) {
		return findUser(pinRecord.getRegId());
	}

	public static RegistrationRecord findUser(String regId) {
		return ofy().load().type(RegistrationRecord.class).filter("regId", regId).first().now();
	}
}
