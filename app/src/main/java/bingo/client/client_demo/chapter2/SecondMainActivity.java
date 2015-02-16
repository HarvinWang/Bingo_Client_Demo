package bingo.client.client_demo.chapter2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;

import bingo.client.client_demo.R;
import bingo.client.client_demo.XmppConnectionManager;

public class SecondMainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_main);

		Button send = (Button) findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					@Override
					public void run() {
						EditText friend = (EditText) findViewById(R.id.friend);
						EditText message = (EditText) findViewById(R.id.message);
						XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getInstance();

						try {
							xmppConnectionManager.conServer();
							xmppConnectionManager.login();
							xmppConnectionManager.sendMessage(friend.getText().toString(),
									message.getText().toString());
						} catch (XMPPException e) {
							e.printStackTrace();
						}

						int result = xmppConnectionManager.IsUserOnLine(friend.getText().toString());
						Log.v("======", "the result is :"+result);
						//Toast.makeText(SecondMainActivity.this,  "the result is :"+result, Toast.LENGTH_LONG).show();
					}
				}.start();
			}
		});

	}

}
