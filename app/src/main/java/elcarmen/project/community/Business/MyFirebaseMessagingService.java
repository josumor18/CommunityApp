package elcarmen.project.community.Business;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.DB_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = "Mensajes Firebase: ";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //String rT = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            showNotification(remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void showNotification(Map<String, String> messages){
        int tipo = Integer.parseInt(messages.get("tipo"));

        if(tipo == 1){
            Event event = new Event();
            event.setId(Integer.parseInt(messages.get("id")));
            event.setId_community(Integer.parseInt(messages.get("id_community")));
            event.setName_community(messages.get("community_name"));
            event.setTitle(messages.get("title"));
            event.setDescription(messages.get("description"));
            event.setDatesEvent(messages.get("dateEvent"), messages.get("startTime"), messages.get("endTime"));
            event.setPhoto(messages.get("photo"));
            event.setApproved(Boolean.parseBoolean(messages.get("approved")));

            if(event.isApproved()){
                DB_Access db_access = DB_Access.getInstance();
                db_access.setContext(getApplicationContext());
                db_access.insert_event(event, event.getName_community());
            }


            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap eventImage = null;
            try {
                eventImage = request.execute(event.getPhoto()).get();
            } catch (InterruptedException er) {
                er.printStackTrace();
            } catch (ExecutionException er) {
                er.printStackTrace();
            }

            Intent intent = new Intent(getApplicationContext(), EventInfoActivity.class);
            intent.putExtra("community_name", event.getName_community());
            intent.putExtra("photo", event.getPhoto());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("date", event.getDate());
            boolean terminado = false;
            Date date = new Date();
            if (date.after(event.getDateEventEnd())){
                terminado = true;
            }
            intent.putExtra("terminado", terminado);
            //intent.putExtra("terminado", false);
            intent.putExtra("hours", event.getHours());
            intent.setAction(Long.toString(System.currentTimeMillis()));

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            String notif_body = "Evento: " + event.getTitle() + "\nFecha: " + event.getDate() +
                    "\nHora de inicio: " + event.getStart_to_String();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.logo_community)
                    .setLargeIcon(eventImage)
                    .setContentTitle(event.getName_community() + ": Nuevo evento")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notif_body))
                    .setContentText(notif_body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[] { 1000, 100, 200, 100, 100, 200 });

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1000 + event.getId(), builder.build());
        }else{            
            String url_photo = messages.get("photo");
            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap newsImage = null;
            try {
                newsImage = request.execute(url_photo).get();
            } catch (InterruptedException er) {
                er.printStackTrace();
            } catch (ExecutionException er) {
                er.printStackTrace();
            }
            News news = new News(Integer.parseInt(messages.get("id")), messages.get("title"),
                    messages.get("description"), messages.get("date"), messages.get("photo"),
                    newsImage, Boolean.parseBoolean(messages.get("approved")));

            Intent intent = new Intent(getApplicationContext(), NewsMoreActivity.class);
            intent.putExtra("idActual", news.getId());
            intent.putExtra("Title", news.getTitle());
            intent.putExtra("DateN", messages.get("date"));
            intent.putExtra("Description", news.getDescription());
            intent.putExtra("isApproved", news.isApproved());
            intent.putExtra("idCommunity", news.getIdCommunity());
            intent.putExtra("Photo", news.getUrl_photo());
            intent.setAction(Long.toString(System.currentTimeMillis()));
            NewsMoreActivity.fromNotif = true;

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            String notif_body = "Difusión: " + news.getTitle();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.logo_community)
                    .setLargeIcon(newsImage)
                    .setContentTitle(messages.get("community_name") + ": Nueva difusión")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notif_body))
                    .setContentText(notif_body)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVibrate(new long[] { 1000, 100, 200, 100, 100, 200 });

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(2000 + news.getId(), builder.build());
        }

    }
}
