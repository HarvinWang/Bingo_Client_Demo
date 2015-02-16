package bingo.client.client_demo.chapter2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import bingo.client.client_demo.R;
import bingo.client.client_demo.XmppConnectionManager;

public class ReceiveActivity extends ActionBarActivity {
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive);

		handler = new Handler(){
			@Override
			public void handleMessage(android.os.Message msg) {
				Message message = (Message) msg.obj;

				TextView textView = (TextView) findViewById(R.id.hello);
				textView.setText(message.getBody());
			}
		};


		new Thread(){
			@Override
			public void run() {
				Log.v("== inside receiver activity", "start to login");
				final XmppConnectionManager xmppConnectionManager = XmppConnectionManager.getInstance();
				try {
					xmppConnectionManager.setUsername("admin");
					xmppConnectionManager.setPassword("admin");
					xmppConnectionManager.login();
					System.out.println("+++ start to login: +++");
				} catch (XMPPException e) {
					e.printStackTrace();
				}
				ChatManager chatManager = xmppConnectionManager.getConnection().getChatManager();
				chatManager.addChatListener(
						new ChatManagerListener() {
							@Override
							public void chatCreated(Chat chat, boolean b) {
								chat.addMessageListener(new MessageListener() {
									@Override
									public void processMessage(Chat arg0, Message msg) {
										System.out.println("+++ the message is: +++"+msg);
										System.out.println("+++ the message body is: +++" + msg.getBody());


										handler.sendMessage(handler.obtainMessage(1, msg));
									}
								});
							}
						}
				);

			}
		}.start();

	}
}
