package kersch.com.backend.utils;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import kersch.com.backend.records.ResponseRecord;
import kersch.com.backend.records.PinRecord;
import kersch.com.backend.records.RegistrationRecord;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

	static {
		factory().register(RegistrationRecord.class);
		factory().register(PinRecord.class);
		factory().register(ResponseRecord.class);
		// Add other records here
	}

	public static Objectify ofy() {
		return ObjectifyService.ofy();
	}

	public static ObjectifyFactory factory() {
		return ObjectifyService.factory();
	}
}
