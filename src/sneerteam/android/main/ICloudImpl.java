package sneerteam.android.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import sneerteam.api.ICloud;
import sneerteam.api.ISubscriber;
import sneerteam.api.ISubscription;
import sneerteam.network.ToServer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

class ICloudImpl extends ICloud.Stub {

	private final Map<Long, ISubscriptionImpl> subscriptionsById;
	private final AtomicLong nextSubscriptionId;
	private final AtomicLong messagesBeingSent;
	private final ToServer toServer;
	
	private final HashSet<ISubscriptionImpl> mySubs = new HashSet<ISubscriptionImpl>();
	private volatile boolean isClosed = false;

	
	public ICloudImpl(Map<Long, ISubscriptionImpl> subscriptionsById, AtomicLong nextSubscriptionId, AtomicLong messagesBeingSent, ToServer toServer) {
		this.subscriptionsById = subscriptionsById;
		this.nextSubscriptionId = nextSubscriptionId;
		this.messagesBeingSent = messagesBeingSent;
		this.toServer = toServer;
		
		startRefreshingServer();
	}

	void close() {
		isClosed = true;
		for (ISubscriptionImpl sub : mySubsCopy())
			sub.doDispose();
	}

	private List<ISubscriptionImpl> mySubsCopy() {
		synchronized (mySubs) {
			return new ArrayList<ISubscriptionImpl>(mySubs);
		}
	}

	@Override
	public void pubPath(Uri path) throws RemoteException {
		byte[] encoded = null; // Rod: encodar de modo a saber que é um pub de um path sem value. N precisa encodar o client pq o network já diz de quem é o pacote.
		send(encoded);
	}

	@Override
	public void pubValue(Uri path, Bundle value) throws RemoteException {
		if (value == null) throw new IllegalArgumentException("pub value was null.");
		byte[] encoded = null; // Rod: encodar de modo a saber que é um pub de um path com value. N precisa encodar o client pq o network já diz de quem é o pacote.
		send(encoded);
	}

	@Override
	public ISubscription sub(Uri path, ISubscriber subscriber) throws RemoteException {
		if (isClosed) throw new IllegalStateException("sub was called on closed cloud");
		
		long id = nextSubscriptionId.incrementAndGet();
		ISubscriptionImpl sub = new ISubscriptionImpl(id, path, subscriber, subscriptionsById);
		subscriptionsById.put(id, sub);
		synchronized (mySubs) {
			mySubs.add(sub);
		}
		sendSub(sub);
		return sub;
	}
	
	//Cycles through the subs and sends them again to the server (one every few seconds). This keeps the connection alive and registers the subs on the server again in case it is restarted.
	private void startRefreshingServer() {
		new Thread() { {setDaemon(true);} @Override public void run() {
			while (true) {
				List<ISubscriptionImpl> subs = mySubsCopy();
				if (subs.isEmpty() && isClosed) break;
				for (ISubscriptionImpl sub : subs) {
					if (sub.isDisposed)
						synchronized (mySubs) { mySubs.remove(sub); }
					sendSub(sub);
					while (messagesBeingSent.get() > 0)
						sleepAWhile();
				}
			}
		}}.start();
	}

	private void sendSub(ISubscriptionImpl sub) {
		byte[] encoded = null; // Rod: encodar sub.id, sub.path e sub.disposed. O server tem q descartar esta sub se ela estiver disposed. N precisa encodar o client pq o network já diz de quem é o pacote.
		send(encoded);
	}

	private static final Random RANDOM = new Random();
	private void sleepAWhile() {
		try {
			Thread.sleep(4000 + RANDOM.nextInt(2000)); //Five seconds on average. Random so that all clouds get a chance of sending their subs.
		} catch (InterruptedException e) {
		}		
	}

	private void send(byte[] encoded) {
		messagesBeingSent.incrementAndGet();
		toServer.send(encoded);
	}

}

