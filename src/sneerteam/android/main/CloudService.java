package sneerteam.android.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CloudService extends Service {
	
	private CloudMaster master;

	@Override public void onCreate()  { super.onCreate(); master = new CloudMasterImpl(getApplicationContext()); };
	@Override public void onDestroy() {	if (master != null) master.close();	super.onDestroy(); }
	
	@Override
	public IBinder onBind(Intent intent) {
		String caller = getPackageManager().getNameForUid(Binder.getCallingUid());
		log("onBind(" + intent.getAction() + ") Caller: " + caller);
		return master.freshCloudFor(caller);
	}
	
	private void log(String message) {
		Log.d(getClass().getCanonicalName(), message);
	}
}
