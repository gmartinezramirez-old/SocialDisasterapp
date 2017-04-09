/*
 * ChatService.java
 * 
 * Copyright (C) 2011 IBR, TU Braunschweig
 *
 * Written-by: Johannes Morgenroth <morgenroth@ibr.cs.tu-bs.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.socialdisasters.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import de.tubs.ibr.dtn.api.Block;
import de.tubs.ibr.dtn.api.Bundle;
import de.tubs.ibr.dtn.api.Bundle.ProcFlags;
import de.tubs.ibr.dtn.api.BundleID;
import de.tubs.ibr.dtn.api.DTNClient;
import de.tubs.ibr.dtn.api.DTNClient.Session;
import de.tubs.ibr.dtn.api.DTNIntentService;
import de.tubs.ibr.dtn.api.DataHandler;
import de.tubs.ibr.dtn.api.GroupEndpoint;
import de.tubs.ibr.dtn.api.Registration;
import de.tubs.ibr.dtn.api.ServiceNotAvailableException;
import de.tubs.ibr.dtn.api.SessionDestroyedException;
import de.tubs.ibr.dtn.api.SingletonEndpoint;
import de.tubs.ibr.dtn.api.TransferMode;



public class ChatService extends DTNIntentService {
	
	public enum Debug {
		NOTIFICATION,
		BUDDY_ADD,
		SEND_PRESENCE,
		UNREGISTER
	}
	
	private static final String TAG = "ChatService";
	
	public static final String EXTRA_USER_ID = "com.socialdisasters.USER_ID";
	public static final String EXTRA_TEXT_BODY = "com.socialdisasters.TEXT_BODY";
	public static final String EXTRA_DISPLAY_NAME = "com.socialdisasters.DISPLAY_NAME";
	public static final String EXTRA_PRESENCE = "com.socialdisasters.EXTRA_PRESENCE";
	public static final String EXTRA_STATUS = "com.socialdisasters.EXTRA_STATUS";
	public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
	
	// mark a specific bundle as delivered
	public static final String MARK_DELIVERED_INTENT = "com.socialdisasters.MARK_DELIVERED";
	public static final String REPORT_DELIVERED_INTENT = "com.socialdisasters.REPORT_DELIVERED";
	
	public static final String ACTION_NEW_MESSAGE = "com.socialdisasters.ACTION_NEW_MESSAGE";
	public static final String ACTION_PRESENCE_ALARM = "com.socialdisasters.PRESENCE_ALARM";
	public static final String ACTION_SEND_MESSAGE = "com.socialdisasters.SEND_MESSAGE";
	public static final String ACTION_REFRESH_PRESENCE = "com.socialdisasters.REFRESH_PRESENCE";
	public static final String ACTION_USERINFO_UPDATED = "com.socialdisasters.USERINFO_UPDATED";
	
	private static final int MESSAGE_NOTIFICATION = 1;
	private static final int REFRESH_BUDDY_DATA = 2;
	
	public static final String ACTION_OPENCHAT = "com.socialdisasters.OPENCHAT";
	public static final GroupEndpoint PRESENCE_GROUP_EID = new GroupEndpoint("dtn://chat.dtn/presence");
	
	private boolean _unregister_on_destroy = false;
	
	private final static String FALLBACK_NICKNAME = "Nobody";

	// This is the object that receives interactions from clients.  See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();
	
	
	// DTN client to talk with the DTN service
	private DTNClient.Session _session = null;
	
	// handler for scheduled refreshes
	private UpdateHandler mUpdateHandler = null;
	private HandlerThread mUpdateThread = null;
	
	public ChatService() {
		super(TAG);
	}
	
	public final static class UpdateHandler extends Handler {
		
		public UpdateHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
		}
	}
	
	private DataHandler _data_handler = new DataHandler()
	{
		ByteArrayOutputStream stream = null;
		Bundle current;
		Long flags = 0L;

		public void startBundle(Bundle bundle) {
			this.current = bundle;
			this.flags = 0L;
		
		}

		public void endBundle() {
			de.tubs.ibr.dtn.api.BundleID received = new de.tubs.ibr.dtn.api.BundleID(this.current);
			
			// run the queue and delivered process asynchronously
			Intent i = new Intent(ChatService.this, ChatService.class);
			i.setAction(MARK_DELIVERED_INTENT);
			i.putExtra("bundleid", received);
			startService(i);

			this.current = null;
		}

		public TransferMode startBlock(Block block) {
			// ignore messages with a size larger than 8k
			if ((block.length > 8196) || (block.type != 1)) return TransferMode.NULL;
			
			// create a new bytearray output stream
			stream = new ByteArrayOutputStream();
			
			return TransferMode.SIMPLE;
		}

		public void endBlock() {
			if (stream != null) {
				String msg = new String(stream.toByteArray());
				stream = null;
				
				if (current.getDestination().equals(PRESENCE_GROUP_EID))
				{
					eventNewPresence(current.getSource(), current.getTimestamp().getDate(), msg, flags);
				}
				else
				{
					eventNewMessage(current.getSource(), current.getTimestamp().getDate(), msg, flags);
				}
			}
		}

		public void payload(byte[] data) {
			if (stream == null) return;
			// write data to the stream
			try {
				stream.write(data);
			} catch (IOException e) {
				Log.e(TAG, "error on writing payload", e);
			}
		}

		public ParcelFileDescriptor fd() {
			return null;
		}

		public void progress(long current, long length) {
		}
	
		private void eventNewPresence(SingletonEndpoint source, Date created, String payload, Long flags)
		{
			Log.i(TAG, "Presence received from " + source);
			
			// buddy info
			String nickname = null;
			String presence = null;
			String status = null;
			String voiceeid = null;
			String language = null;
			String country = null;
			
			StringTokenizer tokenizer = new StringTokenizer(payload, "\n");
			while (tokenizer.hasMoreTokens())
			{
				String data = tokenizer.nextToken();
				
				// search for the delimiter
				int delimiter = data.indexOf(':');
				
				// if the is no delimiter, ignore the line
				if (delimiter == -1) return;
				
				// split the keyword and data pair
				String keyword = data.substring(0, delimiter);
				String value = data.substring(delimiter + 1, data.length()).trim();
				
				if (keyword.equalsIgnoreCase("Presence"))
				{
					presence = value;
				}
				else if (keyword.equalsIgnoreCase("Nickname"))
				{
					nickname = value;
				}
				else if (keyword.equalsIgnoreCase("Status"))
				{
					status = value;
				}
				else if (keyword.equalsIgnoreCase("Voice"))
				{
					voiceeid = value;
				}
				else if (keyword.equalsIgnoreCase("Language"))
				{
					language = value;
				}
				else if (keyword.equalsIgnoreCase("Country"))
				{
					country = value;
				}
			}
			
			if (nickname != null)
			{
				
				// set timer to update buddy status if presence is not 'unavailable'
				if (!"unavailable".equals(presence)) {
					// obtain a new refresh message
					android.os.Message msg = mUpdateHandler.obtainMessage(REFRESH_BUDDY_DATA, source.toString());
					
					// schedule the update in 61 minutes
					mUpdateHandler.sendMessageDelayed(msg, 61 * 60 * 1000);
					
					Log.d(TAG, "scheduled update for " + source.toString());
				}
			}
		}
		
		private void eventNewMessage(SingletonEndpoint source, Date created, String payload, Long flags)
		{
			if (source == null)
			{
				Log.e(TAG, "message source is null!");
			}
			
			
			// create a status bar notification
			Log.i(TAG, "New message received!");
		}
	};
	
	/**
	 * Class for clients to access.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with
	 * IPC.
	 */
	public class LocalBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate()
	{
		// call onCreate of the super-class
		super.onCreate();

		
		// create a new background looper
		mUpdateThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mUpdateThread.start();
		Looper looper = mUpdateThread.getLooper();
		mUpdateHandler = new UpdateHandler(looper);
		
		// create registration
		Registration registration = new Registration("chat");
		registration.add(PRESENCE_GROUP_EID);
		
		try {
			initialize(registration);
		} catch (ServiceNotAvailableException e) {
		} catch (SecurityException ex) {
		}
		
		// register to presence changes
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
		
	}
	
	private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			if ("presencetag".equals(key)) {
				String announced = prefs.getString("announced-presence", "unavailable");
				String desired = prefs.getString(key, "unavailable");
				
			}
		}
	};
	
	
	@Override
	public void onDestroy()
	{
		// unregister preference listener
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.unregisterOnSharedPreferenceChangeListener(mPrefListener);

		try {
			// stop looper
			mUpdateThread.quit();
			mUpdateThread.join();
		} catch (InterruptedException e) {
			Log.e(TAG, "Wait for looper thread was interrupted.", e);
		}
		
		
		// destroy the session if the unregister option is enabled
		if (_unregister_on_destroy && _session != null) _session.destroy();
		

		super.onDestroy();
		
		Log.i(TAG, "service destroyed.");
	}
	
	

	@Override
	protected void onHandleIntent(Intent intent) {
		// stop processing if the session is not assigned
		if (_session == null) return;
		
		String action = intent.getAction();
		
		// create a task to process concurrently
		if (ACTION_PRESENCE_ALARM.equals(action))
		{
		}
		else if (MARK_DELIVERED_INTENT.equals(action))
		{
			
		}
		else if (ACTION_SEND_MESSAGE.equals(action))
		{
			//Long buddyId = intent.getLongExtra(ChatService.EXTRA_USER_ID, -1);
			//String text = intent.getStringExtra(ChatService.EXTRA_TEXT_BODY);
			
			// abort if there is no buddyId
			//if (buddyId < 0) return;
			
			actionSendMessage("dtn://androd-7e424bc4.dtn", "HOLaa");
		}
		else if (ACTION_NEW_MESSAGE.equals(action)) {
      //aqui un nuevo mensaje!!!
			//showNotification(intent);
		}
	}

	private void actionSendMessage(String idUser, String text) {
		SingletonEndpoint endpoint = new SingletonEndpoint(idUser);
		try {
			_session.send(endpoint, 3600, text.getBytes());
			Log.e(TAG, "Session SEND");
		} catch (SessionDestroyedException e) {
			e.printStackTrace();
			Log.e(TAG, " Session destroyer al enviar");
		}
	}

	

	@Override
	protected void onSessionConnected(Session session) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		/**
		 * Upgrade from "checkBroadcastPresence" option
		 */
		if (prefs.contains("checkBroadcastPresence")) {
			if (!prefs.getBoolean("checkBroadcastPresence", false)) {
				prefs.edit().putString("presencetag", "unavailable").remove("checkBroadcastPresence").commit();
			} else {
				prefs.edit().remove("checkBroadcastPresence").commit();
			}
		}
		
		/**
		 * Activate presence by default
		 */
		if (!prefs.contains("presencetag")) prefs.edit().putString("presencetag", "auto").commit();
		mPrefListener.onSharedPreferenceChanged(prefs, "presencetag");
		
		// register own data handler for incoming bundles
		session.setDataHandler(_data_handler);

		// store session locally
		_session = session;
		
		// signal updated user info
		Intent i = new Intent(ACTION_USERINFO_UPDATED);
		sendBroadcast(i);
	}

	@Override
	protected void onSessionDisconnected() {
		_session = null;
	}
}
