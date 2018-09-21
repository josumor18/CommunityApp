package elcarmen.project.community.Business;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.R;

public class CreateEventActivity extends AppCompatActivity {

    Menu menuCreateEvent;
    ImageView imgEventUploaded;
    Button btnUploadEventImage;
    EditText txtNewEventDate, txtNewEventDescription, txtNewEventTitle;
    CalendarView calendarView;
    NumberPicker nmbPckStartHour, nmbPckEndHour, nmbPckStartMinute, nmbPckEndMinute;

    private static int IMG_RESULT = 1;
    String picturePath;

    InputStream in = null;
    Bitmap bitmap;

    String photo = "";
    String photo_thumbnail = "";

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnUploadEventImage = findViewById(R.id.btnUploadEventImage);
        btnUploadEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_RESULT);
            }
        });
        imgEventUploaded = findViewById(R.id.imgEventUploaded);
        txtNewEventTitle = findViewById(R.id.txtNewEventTitle);
        txtNewEventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                habilitarBoton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtNewEventDescription = findViewById(R.id.txtNewEventDescription);
        txtNewEventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                habilitarBoton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtNewEventDate = findViewById(R.id.txtNewEventDate);
        txtNewEventDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                habilitarBoton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        calendarView = findViewById(R.id.calendarView);
        calendarView.setVisibility(View.GONE);

        txtNewEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarView.getVisibility() == View.GONE){
                    calendarView.setVisibility(View.VISIBLE);
                }else{
                    calendarView.setVisibility(View.GONE);
                }

            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                txtNewEventDate.setText(fecha);
                view.setVisibility(View.GONE);
            }
        });

        final String[] hourValues = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        final String[] minuteValues = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};

        nmbPckStartHour = findViewById(R.id.nmbPckStartHour);
        nmbPckStartHour.setDisplayedValues(hourValues);
        nmbPckStartHour.setMinValue(0);
        nmbPckStartHour.setMaxValue(23);
        nmbPckStartHour.setWrapSelectorWheel(true);

        nmbPckStartMinute = findViewById(R.id.nmbPckStartMinute);
        nmbPckStartMinute.setDisplayedValues(minuteValues);
        nmbPckStartMinute.setMinValue(0);
        nmbPckStartMinute.setMaxValue(59);

        nmbPckEndHour = findViewById(R.id.nmbPckEndHour);
        nmbPckEndHour.setDisplayedValues(hourValues);
        nmbPckEndHour.setMinValue(0);
        nmbPckEndHour.setMaxValue(23);

        nmbPckEndMinute = findViewById(R.id.nmbPckEndMinute);
        nmbPckEndMinute.setDisplayedValues(minuteValues);
        nmbPckEndMinute.setMinValue(0);
        nmbPckEndMinute.setMaxValue(59);

    }

    private void habilitarBoton(){
        if(txtNewEventTitle.getText().toString().isEmpty() || txtNewEventDescription.getText().toString().isEmpty() || txtNewEventDate.getText().toString().isEmpty()){
            menuCreateEvent.getItem(0).setEnabled(false);
        }else{
            menuCreateEvent.getItem(0).setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event_option, menu);

        menuCreateEvent = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createEventItem:

                ExecuteUploaded executeUploaded = new ExecuteUploaded();
                executeUploaded.execute();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteCreateEvent extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String title, description, dateEvent, start, end;
        boolean approved;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            title = txtNewEventTitle.getText().toString();
            description = txtNewEventDescription.getText().toString();
            String[] dateSplited = txtNewEventDate.getText().toString().split("/");
            dateEvent = dateSplited[2] + "-" + dateSplited[1] + "-" + dateSplited[0];

            int intHour = nmbPckStartHour.getValue();
            int intMinute = nmbPckStartMinute.getValue();
            String stringHour = Integer.toString(intHour);
            String stringMinute = Integer.toString(intMinute);
            if(intHour < 10){
                stringHour = "0" + stringHour;
            }
            if(intMinute < 10){
                stringMinute = "0" + stringMinute;
            }
            start = stringHour + ":" + stringMinute;

            intHour = nmbPckEndHour.getValue();
            intMinute = nmbPckEndMinute.getValue();
            stringHour = Integer.toString(intHour);
            stringMinute = Integer.toString(intMinute);
            if(intHour < 10){
                stringHour = "0" + stringHour;
            }
            if(intMinute < 10){
                stringMinute = "0" + stringMinute;
            }
            end = stringHour + ":" + stringMinute;

            approved = User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();

            String[] keys = {"id", "auth_token", "id_community", "title", "description", "dateEvent", "start", "end", "photo", "approved"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(CommunityActivity.idCommunity), title, description, dateEvent, start, end, photo, Boolean.toString(approved)};
            isOk = api.post_put_base(keys, values, 25, "POST",0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String mensaje = "";
            if(isOk){
                try {
                    JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                    User_Singleton user = User_Singleton.getInstance();
                    user.setAuth_token(response.getString("auth_token"));

                    //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(intent);
                    finish();

                    mensaje = "Evento creado";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //cargarCommunities(API_Access.getInstance().getJsonObjectResponse());
            }else{
                mensaje = "Error al crear comunidad";
            }

            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteUploaded extends AsyncTask<String, Void, String> {
        boolean isOk;
        String rand ="";
        Cloudinary cloudinary = new Cloudinary("cloudinary://523642389612375:w_BVcUQ7VFZ8IQj-iE1-zbqv5iU@ddgkzz2gk");

        @Override
        protected String doInBackground(String... strings) {
            isOk = false;

            try {
                String path = picturePath; //Aquí sería la ruta donde se haya seleccionado la imagen
                File file = new File(picturePath);
                File f = file.getAbsoluteFile();

                ///////////////////////////////////////////////////////////
                // Generar el random para identificador de la foto
                StringBuilder sb = new StringBuilder(10);
                for( int i = 0; i < 10; i++ )
                    sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
                rand = sb.toString();
                ///////////////////////////////////////////////////////////

                cloudinary.uploader().upload(in, ObjectUtils.asMap("public_id", rand));//.getAbsoluteFile(), ObjectUtils.asMap("public_id", rand));//emptyMap());

                photo_thumbnail = (cloudinary.url().transformation(new Transformation()
                        .radius(360).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
                // Normal:
                photo = (cloudinary.url().transformation(new Transformation()
                        .radius(0).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1];


                isOk = true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                photo_thumbnail = (cloudinary.url().transformation(new Transformation()
                        .radius(360).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
                // Normal:
                photo = (cloudinary.url().transformation(new Transformation()
                        .radius(0).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1];
            }else{
                photo = "";
                photo_thumbnail = "";
            }

            ExecuteCreateEvent executeCreateEvent = new ExecuteCreateEvent();
            executeCreateEvent.execute();
        }
    }


    //////////////METODOS PARA OBTENER IMAGEN//////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                    && null != data) {

                Uri URI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URI);
                    // Log.d(TAG, String.valueOf(bitmap));

                    try {
                        in = getContentResolver().openInputStream(URI);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    imgEventUploaded.setImageBitmap(bitmap);


                    //picturePath = getFileName(URI);
                    picturePath = getPath(CreateEventActivity.this,URI);
                    picturePath = getRealPathFromURI(URI);
                    Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
