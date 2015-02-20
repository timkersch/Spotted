package kersch.com.spotted.utils;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-20
 * Time: 17:31
 */
public class NotRegisteredForMessagesException extends Exception {

	public NotRegisteredForMessagesException() {
		super();
	}

	public NotRegisteredForMessagesException(String message) {
		super(message);
	}
}
