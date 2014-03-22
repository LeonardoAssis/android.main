package sneerteam.android.main.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import sneerteam.android.main.Contact;
import sneerteam.android.main.R;
import sneerteam.api.ICloud;
import sneerteam.api.ISubscriber;
import sneerteam.api.ISubscription;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactPickerActivity extends Activity {

	protected ICloud cloud;
	
	final CopyOnWriteArrayList<ISubscription> subscriptions = new CopyOnWriteArrayList<ISubscription>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() { @Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Contact contact = contacts().get(position);
				String name = contact.getName();
				
				Intent intent = new Intent();
				intent.putExtra("public_key", name);
				intent.putExtra("nickname", name);
				
				setResult(RESULT_OK, intent);
				finish();
		 }});

		adapter = new ContactsAdapter(this, R.layout.list_item_contact, contacts());
		listView.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		
		for (ISubscription sub : subscriptions) {
			try {
				sub.dispose();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		subscriptions.clear();
		
		super.onDestroy();
	}
	
	final List<Contact> contacts = new ArrayList<Contact>();
	private ContactsAdapter adapter;
	
	private List<Contact> contacts() {
		contacts.add(new Contact("Altz"));
		contacts.add(new Contact("Rafa"));
		contacts.add(new Contact("Jao"));
		return contacts;
	}
	
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message m) {
			contacts.add((Contact)m.obj);
			adapter.notifyDataSetChanged();
		}
	};
	
	final ServiceConnection snapi = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			cloud = (ICloud)binder;
			try {
				subscriptions.add(cloud.sub(Uri.parse("/contacts"), new ISubscriber() {
					
					@Override
					public void onPath(Uri publicKeyPath) throws RemoteException {
						toast("onPath(" + publicKeyPath + ")");
						subscriptions.add(
								cloud.sub(Uri.withAppendedPath(publicKeyPath, "nickname"), this));
					}
					
					@Override
					public void onValue(Uri nicknamePath, Bundle value) throws RemoteException {
						String nickname = value.getString(":value");
						Contact contact = new Contact(nickname);
						handler.dispatchMessage(
								android.os.Message.obtain(handler, 1, contact));
					}
					
					@Override
					public IBinder asBinder() {
						toast("asBinder");
						throw new UnsupportedOperationException();
					}
				}));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			toast("disconnected");
			subscriptions.clear();
			cloud = null;
		}
	};
	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
