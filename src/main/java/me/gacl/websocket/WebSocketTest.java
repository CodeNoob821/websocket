package me.gacl.websocket;

import java.io.IOException;
import com.google.gson.Gson;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.*;


/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketTest {
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet<WebSocketTest>();

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	
	private static int count = 0;
	


	/**
	 * 连接建立成功调用的方法
	 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		webSocketSet.add(this);     //加入set中
		addOnlineCount();           //在线数加1
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){
		webSocketSet.remove(this);  //从set中删除
		subOnlineCount();           //在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 * @throws InterruptedException 
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws InterruptedException {
		System.out.println("来自客户端的消息:" + message);
		//群发消息
		for(WebSocketTest item: webSocketSet){
			try {
				JSONObject object = JSONObject.parseObject(message);
				
				//item.sendMessage(object.getString("xxx"));
				//item.sendMessage(object.getString("yyy"));
				TestModel tm1 = new TestModel();
				tm1.setId(111);tm1.setName("xxx");
				TestModel tm2 = new TestModel();
				tm2.setId(222);tm2.setName("yyy");
				TestModel tm3 = new TestModel();
				tm3.setId(333);tm3.setName("zzz");
				TestModel tm4 = new TestModel();
				tm4.setId(444);tm4.setName("aaa");				
				
				List<TestModel> list = Arrays.asList(tm1,tm2);
				List<TestModel> list2 = Arrays.asList(tm3,tm4);
				//list.add(tm1);
				//list.add(tm2);
				Gson gson = new Gson();
				String t = gson.toJson(list);
				String s = gson.toJson(list2);
				//System.out.println(object.getString("xxx")+object.getString("yyy"));
				System.out.println(t);
				item.sendMessage(t);
				Thread.sleep(10000);
				item.sendMessage(s);
				System.out.println(s);
				//Timer mTimer = new Timer();
				//mTimer.schedule(new myTimer(), 0, 1000);
//				while(count<10)
//				{
//					count++;
//					item.sendMessage(count+"个小僵尸");
//					Thread.sleep(1000);
//				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		
		}
		count = 0;
	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException{
		this.session.getBasicRemote().sendText(message);
		//this.session.getAsyncRemote().sendText(message);
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketTest.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketTest.onlineCount--;
	}
}
class myTimer extends TimerTask{
	@Override
	public void run() {
		System.out.println("xxx");
	}
	public void sendmsg(Session session) {
		try {
			session.getBasicRemote().sendText("xxx");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
