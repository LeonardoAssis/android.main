package sneerteam.android.main;

import sneerteam.api.ICloud;
import sneerteam.api.ISubscriber;
import sneerteam.api.ISubscription;
import sneerteam.keys.PublicKey;
import sneerteam.network.Client;
import sneerteam.network.ToServer;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;

class ICloudImpl extends ICloud.Stub implements Client {

	private final PublicKey publicKey;
	private ToServer toServer;

	public ICloudImpl(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	void connect(ToServer toServer) {
		this.toServer = toServer;
	}
	
	public void close() {
		int implementMe;
	}

	@Override
	public void pubPath(Uri path) throws RemoteException {
		//toServer.send(arg0);
	}

	@Override
	public void pubValue(Uri path, Bundle value) throws RemoteException {
		if (value == null) throw new IllegalArgumentException("pub value must not be null.");
		int implementMe;
		
	}

	@Override
	public ISubscription sub(Uri path, ISubscriber subscriber) throws RemoteException {
		int implementMe;
		return null;
	}

	@Override
	public PublicKey publicKey() {
		return publicKey;
	}

	@Override
	public void receive(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendCompleted(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

}
