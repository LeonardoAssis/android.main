package sneerteam.android.main.ui;

import java.util.ArrayList;
import java.util.List;

import sneerteam.android.main.Contact;
import sneerteam.android.main.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ListView listView = (ListView) findViewById(R.id.listView);
		// listView.setOnItemClickListener(new OnItemClickListener() { @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// String name = contacts().get(position).getName();
		// Intent intent = new Intent(Contacts.this, ChatActivity.class);
		// intent.putExtra("contact_seal", name);
		// startActivity (intent);
		// }});

		ContactsAdapter adapter = new ContactsAdapter(this, R.layout.list_item_contact, contacts());
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_friend:
			
			// Bundle bundleNick = new Bundle();
			// bundleNick.putString(":value", friendNickname);
			// cloud.pub("/contacts/" + friendPublicKeyHex + "/nickname", bundleNick);
			//
			// Bundle bundleName = new Bundle();
			// bundleName.putString(":value", friendName);
			// cloud.pub("/contacts/" + friendPublicKeyHex + "/name", bundleName);
			
			Toast.makeText(this, "Show add friend view!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_profile:
			showActionProfile();
			break;
		case R.id.action_send_pk:
			Toast.makeText(this, "Send my pk!", Toast.LENGTH_SHORT).show();
			break;
		}

		return true;
	}

	private void showActionProfile() {
		Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
		startActivity(intent);
	}

	private List<Contact> contacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact(PreferenceManager.getDefaultSharedPreferences(this).getString("nickname", null)));
		contacts.add(new Contact("Altz"));
		contacts.add(new Contact("Rafa"));
		contacts.add(new Contact("Jao"));
		return contacts;
	}

}
