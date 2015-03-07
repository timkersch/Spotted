package kersch.com.spotted.utils;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-01-31
 * Time: 23:50
 */
public class Constants {
	// Only used in debugging
	public static final boolean LOCAL = false;

	// Property for registration id
	public static final String PROPERTY_REG_ID = "registration_id";

	// Sender ID = Project number
	public static final String SENDER_ID = "14744998140";

	// ID for database update messages
	public static final int DATABASE_UPDATE_ID = 0x01;

	// One day in milliseconds
	public static final double ONE_DAY_IN_MS = 8.64 * Math.pow(10,7);

	public static final int NO_FILTER = 0x100;
	public static final int RADIUS_FILTER = 0x101;
	public static final int CREATION_FILTER = 0x102;
	public static final int EXPIRES_FILTER = 0x103;
}
