package elcarmen.project.community.Business;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.DB_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class ReminderCreator extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Context context;

    public ReminderCreator() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            Runnable runnable;
            final Handler handler  = new Handler();


            runnable = new Runnable() {
                public void run() {
                    Toast.makeText(context, "Mensaje",  Toast.LENGTH_SHORT).show();

                    ArrayList<Event> events = DB_Access.getInstance().getNextEvents();

                    for(Event e : events){

                        HttpGetBitmap request = new HttpGetBitmap();
                        Bitmap eventImage = null;
                        try {
                            eventImage = request.execute(e.getPhoto()).get();
                        } catch (InterruptedException er) {
                            er.printStackTrace();
                        } catch (ExecutionException er) {
                            er.printStackTrace();
                        }

                        String notif_body = "Evento: " + e.getTitle() + "\nFecha: " + e.getDate() + "\nHora de inicio: " + e.getStart_to_String();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.logo_community)
                                .setLargeIcon(eventImage)
                                .setContentTitle(e.getName_community())
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(notif_body))
                                .setContentText(notif_body);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(e.getId(), builder.build());
                    }
                    handler.postDelayed(this, 20000);
                }
            };

            handler.postDelayed(runnable, 1000);



            /*try {
                Thread.sleep(5000);
                Toast.makeText(context, "Mensaje",  Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_media_next)
                        .setContentTitle("Notificación creada")
                        .setContentText("Hola Mundo!");
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1234, builder.build());
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }*/
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        context = getApplicationContext();

        HandlerThread thread = new HandlerThread("ServiceStartArguments");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(context, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(context, "service done", Toast.LENGTH_SHORT).show();
    }
}
