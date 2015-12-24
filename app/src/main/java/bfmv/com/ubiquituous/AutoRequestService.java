package bfmv.com.ubiquituous;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import org.androidannotations.annotations.EService;

import bfmv.com.ubiquituous.model.Galon;
import bfmv.com.ubiquituous.rest.GalonService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by MartinOenang on 12/14/2015.
 */
@EService
public class AutoRequestService extends Service{
    SharedPreferences prefs;
    String address;
    String sellerNumber;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void getGalonStatus() {
        prefs = getSharedPreferences("bfmv.com.ubiquitous.sharedprefs", Context.MODE_PRIVATE);
        address = prefs.getString("address", null);
        sellerNumber = prefs.getString("sellerNumber", null);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://ristek.cs.ui.ac.id/galminder/api/").addConverterFactory(GsonConverterFactory.create()).build();
        GalonService galonService = retrofit.create(GalonService.class);
        Call<Galon> galonStatusCall = galonService.getGalonStatus("1");
        Log.d("MO", "galon");
        galonStatusCall.enqueue(new Callback<Galon>() {
            @Override
            public void onResponse(Response<Galon> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        Galon galon = response.body();
                        Log.d("MO", galon.getVolume());
                        if (galon.getInterpretation().equalsIgnoreCase("biasa") || galon.getInterpretation().equalsIgnoreCase("penuh")) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("hasSent", false);
                            editor.commit();
//                            SmsManager sm = SmsManager.getDefault();
//                            Log.d("MO", sellerNumber);
//                            Log.d("MO", address);
//                            sm.sendTextMessage(sellerNumber, null, "Pak! Butuh Cepet! Galon ke " +address, null, null);
//                            Log.d("MO", "send message");
                        } else {
                            if (galon.getInterpretation().equalsIgnoreCase("kosong")) {
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AutoRequestService.this);
                                mBuilder.setSmallIcon(R.drawable.ic_menu_slideshow);
                                mBuilder.setContentTitle("GalMinder");
                                mBuilder.setContentText("DANGER!!! Galon Anda sudah habis!");
                                Intent intent = new Intent(AutoRequestService.this, MainActivity_.class);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(AutoRequestService.this, 0, intent, 0);
                                mBuilder.setContentIntent(resultPendingIntent);
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(7928, mBuilder.build());
                                if (prefs.getBoolean("hasSent",false)) {
                                    SmsManager sm = SmsManager.getDefault();
                                    sm.sendTextMessage(sellerNumber, null, "Pak! Butuh Cepet! Galon ke " +address, null, null);
                                    Log.d("MO", "send message");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("hasSent", true);
                                    editor.commit();
                                }
                            } else if (galon.getInterpretation().equalsIgnoreCase("hampir kosong")) {
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AutoRequestService.this);
                                mBuilder.setSmallIcon(R.drawable.ic_menu_slideshow);
                                mBuilder.setContentTitle("GalMinder");
                                mBuilder.setContentText("DANGER! Galon Anda hampir habis!");
                                Intent intent = new Intent(AutoRequestService.this, MainActivity_.class);
                                PendingIntent resultPendingIntent = PendingIntent.getActivity(AutoRequestService.this, 0, intent, 0);
                                mBuilder.setContentIntent(resultPendingIntent);
                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(7928, mBuilder.build());
                                if (prefs.getBoolean("hasSent",false)) {
                                    SmsManager sm = SmsManager.getDefault();
                                    sm.sendTextMessage(sellerNumber, null, "Galon saya mau habis, tolong kirim ke " + address, null, null);
                                    Log.d("MO", "send message");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("hasSent", true);
                                    editor.commit();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getGalonStatus();
        return START_STICKY;
    }
}
