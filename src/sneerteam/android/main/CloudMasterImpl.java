package sneerteam.android.main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import sneerteam.android.keys.Keys;
import sneerteam.api.ICloud;
import sneerteam.keys.PublicKey;
import sneerteam.network.Client;
import sneerteam.network.Network;
import sneerteam.network.ToServer;
import sneerteam.network.impl.NetworkImpl;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

public class CloudMasterImpl implements CloudMaster {

	private final Map<Object, ICloudImpl> cloudsByCallerId = new HashMap<Object, ICloudImpl>();
	private final Network network;
	private final PublicKey publicKey;

	private final AtomicLong messagesBeingSent = new AtomicLong();
	private final Map<Long, ISubscriptionImpl> subscriptionsById = new ConcurrentHashMap<Long, ISubscriptionImpl>();
	private final AtomicLong nextSubscriptionId = new AtomicLong(System.currentTimeMillis()); // There might be old garbage subs still hanging on the server.
	private final ToServer toServer;

	private boolean isClosed = false;
	
	public CloudMasterImpl(Context context) {
		Keys.initKeys(context);
		publicKey = PublicKey.fromByteArray(Keys.publicKey());
		
		network = new NetworkImpl();
		toServer = network.connect(new Client() {

			@Override
			public PublicKey publicKey() {
				return publicKey;
			}

			@Override
			public void receive(byte[] packet) {
				int subId = 0; // Rod: Decodar do packet.
				Bundle value = null; // Rod: Decodar do packet. Pode ser null.
				Uri path = null; // Rod: Decodar do packet
				ISubscriptionImpl sub = subscriptionsById.get(subId);
				if (sub != null)
					sub.notify(path, value);
			}

			@Override
			public void sendCompleted(byte[] message) {
				messagesBeingSent.decrementAndGet();
			}
		});
		
		publishKey();
	}
	
	@Override
	synchronized
	public void close() {
		isClosed = true;
		for (ICloudImpl cloud : cloudsByCallerId.values())
			cloud.close();
		cloudsByCallerId.clear();
	}

	@Override
	synchronized
	public ICloud.Stub freshCloudFor(Object id) {
		if (isClosed) throw new IllegalStateException("freshCloudFor(" + id + ") was called on a closed CloudMaster.");
			
		ICloudImpl fresh = new ICloudImpl(subscriptionsById, nextSubscriptionId, messagesBeingSent, toServer);
		ICloudImpl old = cloudsByCallerId.put(id, fresh);
		old.close();
		return fresh;
	}

	private void publishKey() {
		ICloudImpl cloud = (ICloudImpl)freshCloudFor("Key Publisher");
		cloud.pubPath(Uri.parse("/keys/public/" + publicKey));
		//cloud.close(); //TODO Future: Make sure pending pubs are sent before close.
	}
}

