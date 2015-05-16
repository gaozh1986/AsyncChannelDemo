package com.example.hello;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class FullConnectTest extends Activity {
	AsyncChannel c1 = new AsyncChannel();
	AsyncChannel c2 = new AsyncChannel();
	Handler test1 = null;

	Handler test2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		Log.d("gaozh", "gaozh FullConnectTest onCreate");

		HandlerThread h1 = new HandlerThread("SyncHandler1");
		h1.start();
		test1 = new Handler(h1.getLooper()) {
			public void handleMessage(android.os.Message msg) {
				if (msg != null) {
					Log.d("gaozh", "gaozh FullConnectTest test1 " + msg.what);
				}
				switch (msg.what) {
				case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
					c1.disconnected();
					break;
				}
			};
		};

		HandlerThread h2 = new HandlerThread("SyncHandler2");
		h2.start();
		test2 = new Handler(h2.getLooper()) {
			public void handleMessage(android.os.Message msg) {
				if (msg != null) {
					Log.d("gaozh", "gaozh FullConnectTest test2 " + msg.what);
					switch (msg.what) {
					case AsyncChannel.CMD_CHANNEL_FULL_CONNECTION:
						c2.connected(FullConnectTest.this, test2, msg.replyTo);
						c2.replyToMessage(msg,
								AsyncChannel.CMD_CHANNEL_FULLY_CONNECTED,
								AsyncChannel.STATUS_SUCCESSFUL);
						break;
					case AsyncChannel.CMD_CHANNEL_DISCONNECTED:
						c2.disconnected();
						break;
					}
				}

			};
		};

		Thread a = new Thread() {
			@Override
			public void run() {
				super.run();
				// 这就是个坑，没琢磨清楚前，不可贸然使用！！
				int status = c1.fullyConnectSync(FullConnectTest.this, test1,
						test2);

				Log.d("gaozh", "gaozh FullConnectTest status " + status);

				try {
					Thread.sleep(5000);
					Log.d("gaozh", "gaozh FullConnectTest c1->c2:8000");
					c1.sendMessage(8000);
					Thread.sleep(1000);
					Log.d("gaozh", "gaozh FullConnectTest c1->c2:8001");
					c1.sendMessage(8001);
					Thread.sleep(1000);
					Log.d("gaozh", "gaozh FullConnectTest c1->c2:8002");
					c1.sendMessage(8002);
					Thread.sleep(1000);
					Log.d("gaozh", "gaozh FullConnectTest c2->c1:9000");
					c2.sendMessage(9000);
					Thread.sleep(1000);
					Log.d("gaozh", "gaozh FullConnectTest c2->c1:9001");
					c2.sendMessage(9001);
					Thread.sleep(1000);
					Log.d("gaozh", "gaozh FullConnectTest c2->c1:9002");
					c2.sendMessage(9002);

					// c1.disconnect();
					c2.disconnect();
				} catch (Exception e) {

				}
			}
		};
		a.start();
	}
}
