package com.socialdisasters.IBRDTN;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;


public class BindingIBRDTN extends ReactContextBaseJavaModule {

    private static final String TAG =  "BindingDTN";
    //private ChatService service;
    private DTNService service;

    private static final String DURATION_SHORT_KEY = "SHORT";

    public BindingIBRDTN(ReactApplicationContext reactContext) { 
        super(reactContext);
        Log.d(TAG, "Oncrete");

        IntentFilter filter = new IntentFilter(DTNService.ACTION_RECV_MESSAGE);
        getReactApplicationContext().registerReceiver(mReceiver, filter);
    }

    @Override
    public String getName() {
        return "BindingIBRDTN";
    }
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        return constants;
    }

    // para probar si funciona el modulo
    @ReactMethod
    public void show(String message, int duration) {
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }


    @ReactMethod
    public void init() {
        service = new DTNService();
        //service = new ChatService();
    }

    @ReactMethod
    public void send(String dir, String message) {
        Log.d(TAG, "SEND: " + dir);

        final Intent intent = new Intent(getReactApplicationContext(), DTNService.class);
        intent.setAction(DTNService.ACTION_SEND_MESSAGE);
        intent.putExtra(DTNService.EXTRA_USER_ID, dir);
        intent.putExtra(DTNService.EXTRA_TEXT_USER, message);
        getReactApplicationContext().startService(intent);
    }


    private void newMessage(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (DTNService.ACTION_RECV_MESSAGE.equals(action)) {
                Log.d(TAG, "ACTION_RECV_MESSAGE: ");
                String id = intent.getStringExtra(DTNService.EXTRA_SOURCE);
                String message = new String(intent.getByteArrayExtra(DTNService.EXTRA_PAYLOAD));
                WritableMap params = Arguments.createMap();
                params.putString("ID", id);
                params.putString("MESSAGE", message);
                newMessage(getReactApplicationContext(), "newMessage", params);
            }
        }
    };
}

