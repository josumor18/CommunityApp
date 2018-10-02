package elcarmen.project.community.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import elcarmen.project.community.Business.Event;

public class DB_Access extends AppCompatActivity{

    private Context context;
    private SQLiteDatabase db;
    private static final DB_Access ourInstance = new DB_Access();

    public static DB_Access getInstance() {
        return ourInstance;
    }

    public DB_Access() {
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void initDB(){
        try{
            db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

            //db.execSQL("DROP TABLE events");

            db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER(100) NOT NULL PRIMARY KEY," +
                    "id_community INTEGER(100)," +
                    "community VARCHAR," +
                    "title VARCHAR," +
                    "description VARCHAR," +
                    "dateEvent VARCHAR," +
                    "startTime VARCHAR," +
                    "endTime VARCHAR," +
                    /*"dateEvent DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "startTime DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "endTime DATETIME DEFAULT CURRENT_TIMESTAMP," +*/
                    "photo VARCHAR," +
                    "approved BOOLEAN NOT NULL CHECK (approved IN (0,1)))");

            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean insert_event(Event event, String name_community){
        try{
            db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

            ContentValues contentValues = new ContentValues();

            contentValues.put("id", event.getId());
            contentValues.put("id_community", event.getId_community());
            contentValues.put("community", name_community);
            contentValues.put("title", event.getTitle());
            contentValues.put("description", event.getDescription());
            contentValues.put("dateEvent", event.getFechaParam());
            contentValues.put("startTime", event.getStart_to_String());
            contentValues.put("endTime", event.getEnd_to_String());
            contentValues.put("photo", event.getPhoto());
            contentValues.put("approved", event.isApproved());

            long res = db.insert("events", null, contentValues);


            /*String query = "SELECT * FROM events";
            Cursor result = db.rawQuery(query, null);
            result.getCount();*/

            db.close();
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public void clearEvents(){
        db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

        db.delete("events", null, null);

        db.close();
    }

    public void deleteEvent(int id){
        db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

        /*String query = "SELECT title FROM events where id=" + Integer.toString(id);
        Cursor result = db.rawQuery(query, null);
        result.getCount();*/

        db.delete("events", "id=?", new String[] {Integer.toString(id)});

        db.close();
    }

    public void updateEvent(int id, boolean approved){
        db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("approved", approved);
        db.update("events", contentValues, "id="+id, null);
        db.close();
    }

    public ArrayList<Event> getNextEvents(){
        ArrayList<Event> events = new ArrayList<Event>();

        db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

        String query = "SELECT * FROM events";
        Cursor result = db.rawQuery(query, null);
        result.getCount();



        int id = result.getColumnIndex("id");
        int id_community = result.getColumnIndex("id_community");
        int name_community = result.getColumnIndex("community");
        int title = result.getColumnIndex("title");
        int description = result.getColumnIndex("description");
        int dateEvent = result.getColumnIndex("dateEvent");
        int startTime = result.getColumnIndex("startTime");
        int endTime = result.getColumnIndex("endTime");
        int photo = result.getColumnIndex("photo");

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();

        Date date_limit = new Date();
        c.add(Calendar.MINUTE, 5);
        date_limit = c.getTime();

        for(result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            int event_id = result.getInt(id);
            int event_id_community = result.getInt(id_community);
            String event_name_community = result.getString(name_community);
            String event_title = result.getString(title);
            String event_description = result.getString(description);
            String event_dateEvent = result.getString(dateEvent);
            String event_startTime = result.getString(startTime);
            String event_endTime = result.getString(endTime);
            String event_photo = result.getString(photo);

            Event event = new Event();
            event.setId(event_id);
            event.setId_community(event_id_community);
            event.setName_community(event_name_community);
            event.setTitle(event_title);
            event.setDescription(event_description);
            event.setDatesEvent(event_dateEvent, event_startTime, event_endTime);
            event.setPhoto(event_photo);
            event.setApproved(true);

            Date eventDate = event.getDateEvent();
            if(eventDate.after(date) && eventDate.before(date_limit)){
                events.add(event);
            }
        }


        db.close();

        return events;
    }
}
