package com.pogamadores.candies.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.pogamadores.candies.R;
import com.pogamadores.candies.application.CandiesApplication;
import com.pogamadores.candies.broadcast.PaymentOrderReceiver;

import org.altbeacon.beacon.Beacon;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Util
{
    public static final int NOTIFICATION_ID = 123456;
    public static final double PRODUCT_DEFAULT_VALUE = 10.5f;

    public static void dispatchNotification(Context context, String uuid, String major, String minor, int productImage)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), productImage);
        dispatchNotification(context, uuid, major, minor, bitmap);
        bitmap.recycle();
    }

    public static void dispatchNotification(Context context, String uuid, String major, String minor, Bitmap productImage)
    {
        Bundle infoBundle = new Bundle();
        infoBundle.putString(IntentParameters.UUID,uuid);
        infoBundle.putString(IntentParameters.MAJOR,major);
        infoBundle.putString(IntentParameters.MINOR,minor);

        Intent purchaseIntent = new Intent(context, PaymentOrderReceiver.class);
        purchaseIntent.putExtras(infoBundle);

        PendingIntent purchasePendingIntent = PendingIntent.getBroadcast(
                context,
                RequestCode.PURCHASE,
                purchaseIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLocalOnly(true)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(context.getString(R.string.notification_generic_title))
                .setContentText(context.getString(R.string.notification_generic_message))
                .addAction(R.drawable.ic_launcher, context.getString(R.string.action_purchase), purchasePendingIntent);

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_ALL;

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(NOTIFICATION_ID,notification);
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void sendMessage(GoogleApiClient client, String path, Beacon targetBeacon) {
        Uri.Builder builder = new Uri.Builder()
                .scheme("candie")
                .authority("beacon")
                .appendQueryParameter("uuid",targetBeacon.getId1().toString())
                .appendQueryParameter("major",targetBeacon.getId2().toString())
                .appendQueryParameter("minor",targetBeacon.getId3().toString());

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        putDataMapRequest.getDataMap().putLong("DataStamp", System.currentTimeMillis());
        putDataMapRequest.getDataMap().putString("content",builder.toString());

        Wearable.DataApi.putDataItem(client, putDataMapRequest.asPutDataRequest());
    }

    public static String getPhoneIMEI(Context context)     {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    /**
     * Sends message to machine via MQTT Protocol
     * @return results of operation. True if message has been sent or false in failure.
     */
    public static boolean sendMessageToMachine(String message)     {

        CandiesApplication app = CandiesApplication.get();

        MqttClient mqttClient = app.getMqttClient();

        boolean retorno = false;

        try {
            mqttClient.connect();
            if (mqttClient.isConnected()) {
                mqttClient.publish("jeffprestes/candies/world", new MqttMessage(message.getBytes()));
                mqttClient.disconnect();
                retorno = true;
            }

        } catch (Exception e) {
            //TODO: Colocar mensagens no arquivo Strings
            Toast.makeText(CandiesApplication.get().getApplicationContext(), "Nao foi possivel fazer a comunicacao com a maquina. Erro: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e("JEFFDEBUG", "Erro ao enviar mensagem a maquina: " + e.getLocalizedMessage(), e);

        } finally {
            return retorno;
        }
    }
}
