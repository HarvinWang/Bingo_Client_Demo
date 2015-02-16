package bingo.client.client_demo;

import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Harvin on 2015/2/12 0012.
 */
public class XmppConnectionManager {
	private final String LOG_TAG = "=== Bingo: XmppConnectionManager ===";
	private volatile static XmppConnectionManager instance = new XmppConnectionManager();
	private String host = "192.168.199.119";
	private int port = 5222;
	private String username = "harvin";
	private String password = "whbfcc";
	private XMPPConnection connection = null;

	/*
	 * disable the constructor
	 */
	private XmppConnectionManager(){
		Log.v(LOG_TAG, "load the XmppConnectionManger.");
	}

	/*
	 * using singleton ,
	 * to make sure the application using the same internet connection.
	 */
	public static XmppConnectionManager getInstance(){
		return instance;
	}

	/**
	 * connect to the server.
	 */
	public boolean conServer() throws XMPPException {
		if (null != connection && connection.isConnected()){
			connection.disconnect();
		}

		ConnectionConfiguration config = new ConnectionConfiguration(host, port);
		config.setSASLAuthenticationEnabled(false);

		connection = new XMPPConnection(config);
		connection.connect();

		Log.v(LOG_TAG, "connect to the server using the new config.");
		return true;
	}

	/**
	 * login.
	 */
	public boolean login() throws XMPPException {
		if (null == connection || connection.isConnected()){
			this.conServer();
		}

		connection.login(username, password);

		Log.v("config connection ID: ", connection.getConnectionID());
		Log.v("config user: ", connection.getUser());
		Log.v("config host:", connection.getHost());
		Log.v("config service name: ", connection.getServiceName());
		Log.v("config port: ", String.valueOf(connection.getPort()));

		return true;
	}

	/**
	 * register
	 *
	 * @param username the name of new user
	 * @param password the password of new user
	 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
	 */
	public int register(String username, String password){
		if (connection == null)
			return 0;
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(username);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		PacketFilter filter = new AndFilter(new PacketIDFilter(
				reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection
				.createPacketCollector(filter);
		connection.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
				.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
			Log.e(LOG_TAG+"RegistActivity", "No response from server.");
			return 0;
		} else if (result.getType() == IQ.Type.RESULT) {
			return 1;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.e(LOG_TAG+"RegistActivity", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return 2;
			} else {
				Log.e(LOG_TAG+"RegistActivity", "IQ.Type.ERROR: "
						+ result.getError().toString());
				return 3;
			}
		}
	}

	/**
	 * 判断OpenFire用户的状态 strUrl :
	 * url格式 - http://my.openfire.com:9090/plugins/presence
	 * /status?jid=user1@SERVER_NAME&type=xml
	 * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
	 * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问
	 */
	public int IsUserOnLine(String user) {
		String url = "http://"+host+":9090/plugins/presence/status?" +
				"jid="+ user +"@"+ "harvin-pc" +"&type=xml";
		int shOnLineState = 0; // 不存在
		try {
			URL oUrl = new URL(url);
			URLConnection oConn = oUrl.openConnection();
			if (oConn != null) {
				BufferedReader oIn = new BufferedReader(new InputStreamReader(
						oConn.getInputStream()));
				if (null != oIn) {
					String strFlag = oIn.readLine();
					oIn.close();
					System.out.println("strFlag"+strFlag);
					if (strFlag.indexOf("type=\"unavailable\"") >= 0) {
						shOnLineState = 2;
					}
					if (strFlag.indexOf("type=\"error\"") >= 0) {
						shOnLineState = 0;
					} else if (strFlag.indexOf("priority") >= 0
							|| strFlag.indexOf("id=\"") >= 0) {
						shOnLineState = 1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return shOnLineState;
	}

	/**
	 *
	 * @param friend the name of the friend.
	 * @param msg the message.
	 */
	public void sendMessage(String friend, final String msg) {
		ChatManager chatManager = connection.getChatManager();
		Chat newChat = chatManager.createChat(friend+"@harvin-pc", new MessageListener() {
			@Override
			public void processMessage(Chat chat, Message message) {
				Log.v("=== receive the message", message.getBody());
				System.out.println("=== Received Message: "+ message);
				Log.v("=== username", username);
			}
		});

		try {
			newChat.sendMessage(msg);
			Log.v("==== the message to be sent: ", msg);
			Log.v("=== username", username);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public XMPPConnection getConnection() {
		return connection;
	}
}
