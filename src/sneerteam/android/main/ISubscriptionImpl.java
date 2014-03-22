package sneerteam.android.main;

import java.util.Map;

import sneerteam.api.ISubscriber;
import sneerteam.api.ISubscription;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

class ISubscriptionImpl extends ISubscription.Stub {

	final long id;
	final Uri path;
	private final ISubscriber subscriber;
	private final Map<Long, ISubscriptionImpl> subscriptionsById;
	boolean isDisposed = false;
	
	ISubscriptionImpl(long id, Uri path, ISubscriber subscriber, Map<Long, ISubscriptionImpl> subscriptionsById) {
		this.id = id;
		this.path = path;
		this.subscriber = subscriber;
		this.subscriptionsById = subscriptionsById;
	}

	@Override
	public void dispose() throws RemoteException {
		doDispose();
	}

	void notify(Uri path, Bundle value) {
		try {
			tryToNotify(path, value);
		} catch (RemoteException e) {
			doDispose();
		}
	}

	private void tryToNotify(Uri path, Bundle value) throws RemoteException {
		if (value == null)
			subscriber.onPath(path);
		else
			subscriber.onValue(path, value);
	}

	void doDispose() {
		subscriptionsById.remove(id);
		isDisposed = true;
	}

}
