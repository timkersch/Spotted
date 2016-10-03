package kersch.com.spotted.utils;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-20
 * Time: 17:31
 */

/** If no messageHandler is specified this exception is thrown.
 */
public class NoMessageHandlerRegisteredException extends Exception {

	public NoMessageHandlerRegisteredException() {
		super();
	}

	public NoMessageHandlerRegisteredException(String message) {
		super(message);
	}
}
