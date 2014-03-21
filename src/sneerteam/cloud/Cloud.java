package sneerteam.cloud;

import android.net.Uri;

public interface Cloud {

	/** Creates path if it does not already exist. */
	void pub(Uri path);
	/** Creates path if it does not already exist and sets the value at that path.
	 * @param value the NON-NULL value to be set at the given path*/
	void pub(Uri path, Object value);

	void sub(Subscriber subscriber, Uri path);
	void cancelAllSubscriptions(Subscriber subscriber);

	interface Subscriber {
		void on(Uri path);
		void on(Uri path, Object value);
	}

}
