package bingo.client.client_demo.chapter1;

import android.content.Intent;
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

public class RegistActivity extends ActionBarActivity {
	private final int SUCCESS = 1;
	private final int FAIL = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);

		Button register = (Button) findViewById(R.id.register);
		register.setOnClickListener(new RegisterEvent());
	}

	class RegisterEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			Toast.makeText(RegistActivity.this, "logining, please wait...", Toast.LENGTH_LONG).show();

			android.os.Handler handler = new android.os.Handler(){
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
						case SUCCESS:
							Toast.makeText(RegistActivity.this,"successfully register .", Toast.LENGTH_LONG).show();
							Intent intent = new Intent(RegistActivity.this, MainActivity.class);
							startActivity(intent);
							break;

						case FAIL:
							Toast.makeText(RegistActivity.this,"failed to login.\n"+msg.obj.toString(), Toast.LENGTH_LONG).show();
							break;
					}
				}
			};

			new RegisterThread(handler).start();
		}
	}

	class RegisterThread extends Thread{
		private android.os.Handler handler = null;

		RegisterThread(android.os.Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			String username = String.valueOf(((EditText)findViewById(R.id.username)).getText().toString());
			String password = String.valueOf(((EditText) findViewById(R.id.password)).getText().toString());

			XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getInstance();
			int result = xmppConnectionManager.register(username, password);

			Message message = handler.obtainMessage();
			//1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
			switch (result) {
				case 1:
					Log.v(getString(R.string.log_tag), "successfully login.");
					message.what = SUCCESS;
					break;
				case 0:
					message.what = FAIL;
					message.obj = "server has no response.";
					break;
				case 2:
					message.what = FAIL;
					message.obj = "user has already exist.";
					break;
				case 3:
					message.what = FAIL;
					message.obj = "fail to register.please try again.";
					break;
			}
			handler.sendMessage(message);
		}
	}
}
