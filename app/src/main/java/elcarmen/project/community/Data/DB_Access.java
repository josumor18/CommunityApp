package elcarmen.project.community.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

            db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER(100) NOT NULL PRIMARY KEY," +
                    "id_community INTEGER(100)," +
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

    public boolean insert_event(Event event){
        try{
            db = context.openOrCreateDatabase("Community_DB", MODE_PRIVATE, null);

            ContentValues contentValues = new ContentValues();

            contentValues.put("id", event.getId());
            contentValues.put("id_community", event.getId_community());
            contentValues.put("title", event.getTitle());
            contentValues.put("description", event.getDescription());
            contentValues.put("dateEvent", event.getFecha());
            contentValues.put("startTime", event.getStart_to_String());
            contentValues.put("endTime", event.getEnd_to_String());
            contentValues.put("photo", event.getPhoto());
            contentValues.put("approved", event.isApproved());

            db.insert("events", null, contentValues);

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
}
