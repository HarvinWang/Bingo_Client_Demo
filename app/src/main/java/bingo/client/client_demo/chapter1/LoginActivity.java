package bingo.client.client_demo.chapter1;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;

import bingo.client.client_demo.MainActivity;
import bingo.client.client_demo.R;
import bingo.client.client_demo.XmppConnectionManager;

public class LoginActivity extends ActionBarActivity {
	private static final int SUCCESS = 1;
	private static final int FAIL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button login = (Button) findViewById(R.id.login);
		login.setOnClickListener(new LoginEvent());
	}

	class LoginEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(LoginActivity.this, "logining, please wait...", Toast.LENGTH_LONG).show();

			Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case SUCCESS:
							Toast.makeText(LoginActivity.this,"successfully login .", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							break;

						case FAIL:
							Toast.makeText(LoginActivity.this,"failed to login", Toast.LENGTH_LONG).show();
							break;
					}
				}
			};

			new LoginThread(handler).start();
		}
	}

	class LoginThread extends Thread{
		private Handler handler = null;

		LoginThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			String username = String.valueOf(((EditText)findViewById(R.id.username)).getText().toString());
			String password = String.valueOf(((EditText) findViewById(R.id.password)).getText().toString());

			XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getInstance();
			xmppConnectionManager.setUsername(username);
			xmppConnectionManager.setPassword(password);


			Message message = handler.obtainMessage();
			try {
				xmppConnectionManager.login();
				Log.v(getString(R.string.log_tag), "successfully login.");
				message.what = SUCCESS;
			} catch (XMPPException e) {
				Log.v(getString(R.string.log_tag), "fail to login.");
				e.printStackTrace();
				message.what = FAIL;
			}


			handler.sendMessage(message);
		}
	}
}
