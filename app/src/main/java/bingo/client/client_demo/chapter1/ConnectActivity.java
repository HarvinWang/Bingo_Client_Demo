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

public class ConnectActivity extends ActionBarActivity {
	private static final int SUCCESS = 1;
	private static final int FAIL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);

		Button connect = (Button) findViewById(R.id.connect);
		connect.setOnClickListener(new ConnectEvent());

	}

	class ConnectEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(ConnectActivity.this, "connecting, please wait...", Toast.LENGTH_LONG).show();

			Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case SUCCESS:
							Toast.makeText(ConnectActivity.this,"successfully connect to the server.", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
							startActivity(intent);
							break;

						case FAIL:
							Toast.makeText(ConnectActivity.this,"failed to  connect to the server.", Toast.LENGTH_LONG).show();
							break;
					}
				}
			};

			new ConnectThread(handler).start();
		}
	}

	class ConnectThread extends Thread{
		private Handler handler = null;

		ConnectThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			String host = String.valueOf(((EditText)findViewById(R.id.host)).getText().toString());
			int port = Integer.parseInt(((EditText) findViewById(R.id.port)).getText().toString());

			XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getInstance();
			xmppConnectionManager.setHost(host);
			xmppConnectionManager.setPort(port);

			Message message = handler.obtainMessage();
			try {
				xmppConnectionManager.conServer();
			} catch (XMPPException e) {
				Log.v(getString(R.string.log_tag), "fail to connect to the server using the new config.");
				e.printStackTrace();
				message.what = FAIL;
			}

			Log.v(getString(R.string.log_tag), "successfully connect to the server using the new config.");
			message.what = SUCCESS;

			handler.sendMessage(message);
		}
	}
}
