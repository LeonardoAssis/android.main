package sneerteam.android.main.ui;

import java.util.ArrayList;
import java.util.List;

import sneerteam.android.main.Contact;
import sneerteam.android.main.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ContactPickerActivity extends Activity {

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

		ContactsAdapter adapter = new ContactsAdapter(this, R.layout.list_item_contact, contacts());
		listView.setAdapter(adapter);

	}

	private List<Contact> contacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("Altz"));
		contacts.add(new Contact("Rafa"));
		contacts.add(new Contact("Jao"));
		return contacts;
	}

}
