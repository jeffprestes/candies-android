package com.pogamadores.candies.service;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.pogamadores.candies.util.Util;

import java.util.concurrent.TimeUnit;

public class MessageService extends WearableListenerService {

    private static final String TAG = MessageService.class.getSimpleName();
    private GoogleApiClient mGoogleClient;

    public MessageService() {}

    private void setUpGoogleClientIfNeeded() {
        if(mGoogleClient == null) {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();

            ConnectionResult connectionResult =
                    googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "Failed to connect to GoogleApiClient.");
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                return;
            }
            mGoogleClient = googleApiClient;
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //super.onDataChanged(dataEvents);
        setUpGoogleClientIfNeeded();

        for(DataEvent event : dataEvents) {

            if(event.getType() == DataEvent.TYPE_CHANGED) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Log.d(TAG, dataMapItem.getDataMap().getString("content"));
                if (event.getDataItem().getUri().getPath().equalsIgnoreCase("/new/candies/beacon")) {
                    Uri message = Uri.parse(dataMapItem.getDataMap().getString("content"));
                    Util.dispatchNotification(getApplicationContext(), message);
                }
            }
        }

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        setUpGoogleClientIfNeeded();
    }

    @Override
    public void onPeerConnected(Node peer) {
        //super.onPeerConnected(peer);
        setUpGoogleClientIfNeeded();
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        //super.onPeerDisconnected(peer);
        setUpGoogleClientIfNeeded();
    }
}
