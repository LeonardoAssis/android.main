package sneerteam.android.main;

import sneerteam.api.ICloud;

public interface CloudMaster {

	/** Returns a new ICloud. All previous IClouds with the same callerId are closed. */
	ICloud.Stub freshCloudFor(Object callerId);

	void close();

}
