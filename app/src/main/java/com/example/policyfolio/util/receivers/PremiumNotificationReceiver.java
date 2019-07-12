package com.example.policyfolio.util.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.policyfolio.R;
import com.example.policyfolio.data.Repository;
import com.example.policyfolio.util.Constants;

public class PremiumNotificationReceiver extends BroadcastReceiver {

    private Repository repository;
    @Override
    public void onReceive(Context context, Intent intent) {
        repository = Repository.getInstance(context);

        long id = intent.getLongExtra(Constants.Notification.ID,-1);
        String policyNumber = intent.getStringExtra(Constants.Notification.POLICY_NUMBER);
        int type = intent.getIntExtra(Constants.Notification.TYPE,-1);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Constants.Notification.DUES_CHANNEL_ID,"DUES", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.Notification.DUES_CHANNEL_ID);
        Log.e("NOTIFICATION",id +" "+policyNumber);
        repository.deleteNotifications(id);

        //TODO Notification Modify Content
        switch (type){
            case Constants.Notification.Type.DAY:
                builder.setContentTitle("Premium Day Pending");
                builder.setContentText(policyNumber+"'s due is pending.");
                break;
            case Constants.Notification.Type.MONTH:
                builder.setContentTitle("Premium Month Pending");
                builder.setContentText(policyNumber+"'s due is pending.");
                break;
            case Constants.Notification.Type.TWO_MONTHS:
                builder.setContentTitle("Premium Two Months Pending");
                builder.setContentText(policyNumber+"'s due is pending.");
                break;
            case Constants.Notification.Type.TWO_WEEKS:
                builder.setContentTitle("Premium Two Weeks Pending");
                builder.setContentText(policyNumber+"'s due is pending.");
                break;
            case Constants.Notification.Type.WEEK:
                builder.setContentTitle("Premium Week Pending");
                builder.setContentText(policyNumber+"'s due is pending.");
                break;
        }
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setAutoCancel(true);

        Notification notification=builder.build();
        manager.notify((int) id,notification);
    }
}
