package com.trustedoffer.messapp.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trustedoffer.messapp.Activity.MainActivity;
import com.trustedoffer.messapp.R;

import java.util.Random;

public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //Setting Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);
        //Getting Notification Title
        String title = remoteMessage.getNotification().getTitle();
        //Getting Notification Messgae
        String message = remoteMessage.getNotification().getBody();
        //Setting Notification Sound
        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Building Notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "PushNotification")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PushNotification")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_profile)
                .setAutoCancel(true)
                .setContentText(message);

        NotificationManagerCompat compat = NotificationManagerCompat.from(this);
        compat.notify(01, builder.build());
    }
}
