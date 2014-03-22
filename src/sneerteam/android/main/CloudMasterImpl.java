package sneerteam.android.main;

import java.util.HashMap;
import java.util.Map;

import sneerteam.android.keys.Keys;
import sneerteam.api.ICloud;
import sneerteam.keys.PublicKey;
import sneerteam.network.Network;
import sneerteam.network.ToServer;
import sneerteam.network.impl.NetworkImpl;
import android.content.Context;

public class CloudMasterImpl implements CloudMaster {

	private final Map<Object, ICloudImpl> cloudsById = new HashMap<Object, ICloudImpl>();
	private final Network network;
	private final PublicKey publicKey;

	
	public CloudMasterImpl(Context context) {
		Keys.initKeys(context);
		publicKey = PublicKey.fromByteArray(Keys.publicKey());
		network = new NetworkImpl();
	}
	
	@Override
	synchronized
	public void close() {
		for (ICloudImpl cloud : cloudsById.values())
			cloud.close();
		cloudsById.clear();
	}

	@Override
	synchronized
	public ICloud.Stub freshCloudFor(Object id) {
		ICloudImpl fresh = new ICloudImpl(publicKey);
		ToServer toServer = network.connect(fresh);
		fresh.connect(toServer);
		
		ICloudImpl old = cloudsById.put(id, fresh);
		old.close();
		
		return fresh;
	}

	
	
}
