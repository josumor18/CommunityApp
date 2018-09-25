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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.R;

public class NewsActivity extends AppCompatActivity {

    EditText edtContent;
    EditText edtName;
    Button btnAdd;
    ImageButton btnImage;
    //ImageView img_post;

    Date date1 = new Date();
    String date = "";

    User_Singleton user;
    Intent intent;
    private static int IMG_RESULT = 1;
    String picturePath;
    Bitmap bitmap;

    InputStream in = null;
    String photo = "";
    String photo_thumbnail = "";

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = User_Singleton.getInstance();


        edtContent = findViewById(R.id.edt_contentNews);
        btnAdd = findViewById(R.id.btn_addNew);
        edtName = findViewById(R.id.edt_nameNews);
        btnImage = findViewById(R.id.imgBut_news);

        btnImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, IMG_RESULT);*/
                intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_RESULT);


            }
        });
    }



    public void addClicked(View view){

        if (!TextUtils.isEmpty(edtContent.getText()) && ! TextUtils.isEmpty(edtName.getText())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date1 = new Date();

            date = dateFormat.format(date1);

            ExecuteUploaded executeUploaded = new ExecuteUploaded();
            executeUploaded.execute();


        }
        else {
            edtContent.setError("Campo Requerido");
            edtName.setError("Campo Requerido");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ExecutePost extends AsyncTask<String,Void,String> {
        private String title;
        private String idCommunity;
        private String description;
        private String photo; //Url image
        private String date;
        private boolean approved;
        private boolean isPosted = false;

        public ExecutePost(String idCommunity,String title, String description,String urlImage,String date, boolean isApproved) {
            this.idCommunity = idCommunity;
            this.title = title;
            this.description = description;
            this.photo = urlImage;
            this.date = date;
            this.approved = isApproved;
        }



        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            String[] keys = {"idCommunity","title", "description", "date","photo","approved"};
            String[] values = {idCommunity,title,description,date,photo,Boolean.toString(approved)};
            isPosted = api.post_put_base(keys,values,9,"POST",0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isPosted) {

                Toast.makeText(NewsActivity.this, "Publicacion exitosa", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getApplicationContext(), CommunityActivity.class);
                intent.putExtra("idCommunity",CommunityActivity.idCommunity);
                intent.putExtra("nameCommunity",CommunityActivity.nameCommunity);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                finish();
            } else
                Toast.makeText(NewsActivity.this, "Publicacion fallida", Toast.LENGTH_SHORT).show();

        }
    }
    public class ExecuteUploaded extends AsyncTask<String, Void, String> {
        boolean isOk;
        String rand ="";
        Cloudinary cloudinary = new Cloudinary("cloudinary://523642389612375:w_BVcUQ7VFZ8IQj-iE1-zbqv5iU@ddgkzz2gk");

        @Override
        protected String doInBackground(String... strings) {


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

            photo_thumbnail = (cloudinary.url().transformation(new Transformation()
                    .radius(360).crop("scale").chain()
                    .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
            // Normal:
            photo = (cloudinary.url().transformation(new Transformation()
                    .radius(0).crop("scale").chain()
                    .angle(0)).imageTag(rand + ".png")).split("\'")[1];


            ExecutePost executePost = new ExecutePost(Integer.toString(CommunityActivity.idCommunity),edtName.getText().toString(),
                    edtContent.getText().toString(),photo, date ,user.isAdmin(CommunityActivity.idCommunity));

            executePost.execute();
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

                    btnImage.setImageBitmap(bitmap);


                    //picturePath = getFileName(URI);
                    picturePath = getPath(NewsActivity.this,URI);
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
