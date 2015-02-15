package kersch.com.spotted.utils;
import kersch.com.spotted.R;

import java.util.Random;

/**
 * Created by: Tim Kerschbaumer
 * Project: Spotted
 * Date: 15-02-15
 * Time: 19:49
 */
public class RandomPins {

	/** Returns a random pin drawable id.
	 * @return a random id for a pin drawable
	 */
	public static int getPinId() {
		Random random = new Random();
		double randomNumber = random.nextDouble();
		if(randomNumber >= 0 && randomNumber < 0.2) {
			return R.drawable.bluemarker_large;
		} else if(randomNumber >= 0.2 && randomNumber < 0.4) {
			return R.drawable.greenmarker_large;
		} else if(randomNumber >= 0.4 && randomNumber < 0.6) {
			return R.drawable.orangemarker_large;
		} else if(randomNumber >= 0.6 && randomNumber < 0.8) {
			return R.drawable.redmarker_large;
		} else {
			return R.drawable.yellowmarker_large;
		}
	}
}
