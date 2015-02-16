package bingo.client.client_demo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jivesoftware.smack.XMPPConnection;

import bingo.client.client_demo.chapter1.ConnectActivity;
import bingo.client.client_demo.chapter1.LoginActivity;
import bingo.client.client_demo.chapter1.RegistActivity;
import bingo.client.client_demo.chapter2.ReceiveActivity;
import bingo.client.client_demo.chapter2.SecondMainActivity;


public class MainActivity extends ActionBarActivity {
	private XMPPConnection connection = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ClickEvent clickEvent = new ClickEvent();

		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(clickEvent);
		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(clickEvent);
		Button register = (Button) findViewById(R.id.register);
		register.setOnClickListener(clickEvent);
		Button checkReceiver = (Button) findViewById(R.id.checkReceiver);
		checkReceiver.setOnClickListener(clickEvent);

		Button second = (Button) findViewById(R.id.second);
		second.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SecondMainActivity.class);
				startActivity(intent);
			}
		});
	}

	class ClickEvent implements View.OnClickListener {
		Intent intent = null;
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
				case R.id.connect:
					intent = new Intent(MainActivity.this, ConnectActivity.class);
					break;
				case R.id.login:
					intent = new Intent(MainActivity.this, LoginActivity.class);
					break;
				case R.id.register:
					intent = new Intent(MainActivity.this, RegistActivity.class);
					break;
				case R.id.checkReceiver:
					intent = new Intent(MainActivity.this, ReceiveActivity.class);
					break;
			}

			Log.v(getString(R.string.log_tag), "start the activity of :"+ intent.getClass());
			startActivity(intent);
		}
	}
}
