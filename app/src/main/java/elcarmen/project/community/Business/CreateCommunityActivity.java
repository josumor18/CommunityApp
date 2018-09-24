package elcarmen.project.community.Business;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

public class CreateCommunityActivity extends AppCompatActivity {

    String[] valores = new String[6];
    private static int IMG_RESULT = 1;
    String picturePath;

    Menu menuCreateCommunity;
    Button btnUploadComImage;

    boolean isSubcommunity = false;

    ListView lvRulesListCreate;
    EditText edtxtCommName, edtxtCommDescription, edtxtReglaConvivencia;

    ArrayList<String> listaReglas = new ArrayList<String>();

    InputStream in = null;
    Bitmap bitmap;
    ImageView imgComImage;

    String photo = "";
    String photo_thumbnail = "";

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        isSubcommunity = getIntent().getBooleanExtra("isSubcommunity",false);

        lvRulesListCreate = findViewById(R.id.lvRulesListCreate);
        edtxtCommName = findViewById(R.id.edtxtCommName);
        edtxtCommName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0){
                    menuCreateCommunity.getItem(0).setEnabled(false);
                }else{
                    menuCreateCommunity.getItem(0).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtxtCommDescription  = findViewById(R.id.edtxtCommDescription);
        edtxtReglaConvivencia = findViewById(R.id.edtxtReglaConvivencia);

        imgComImage = findViewById(R.id.imgComImage);

        btnUploadComImage = findViewById(R.id.btnUploadComImage);
        btnUploadComImage.setOnClickListener(new View.OnClickListener() {
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

    }

    public void addRule_Clicked(View view){
        String newRule = edtxtReglaConvivencia.getText().toString();
        if(newRule.isEmpty()){
            Toast.makeText(getApplicationContext(), "Ingrese una regla", Toast.LENGTH_SHORT).show();
        }else {
            listaReglas.add(newRule);
            lvRulesListCreate.setAdapter(new RulesListAdapter());
            edtxtReglaConvivencia.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_community_option, menu);

        menuCreateCommunity = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.createCommunityItem:



                valores[0] = edtxtCommName.getText().toString();
                valores[1] = edtxtCommDescription.getText().toString();
                ArrayList<String> rulesAux = new ArrayList<String>();
                String rulesArrayToString = "[";
                for(int i = 0; i < listaReglas.size(); i++){
                    rulesAux.add(listaReglas.get(i));
                    rulesArrayToString += listaReglas.get(i) + ",";
                }
                rulesAux.add("");
                rulesArrayToString += "]";
                valores[2] = rulesAux.toString();
                valores[2] = rulesArrayToString;

                if(isSubcommunity){
                    valores[3] = "true";
                }else{
                    valores[3] = "false";
                }

                ExecuteUploaded executeUploaded = new ExecuteUploaded();
                executeUploaded.execute();


                //Toast.makeText(getApplicationContext(), "Crear comunidad", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class RulesListAdapter extends BaseAdapter {

        public RulesListAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return listaReglas.size();
        }

        @Override
        public Object getItem(int i) {
            return listaReglas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.rules_list_create_item, null);
            }

            TextView txtRuleNumberCC = view.findViewById(R.id.txtRuleNumberCC);
            TextView txtRuleTextCC = view.findViewById(R.id.txtRuleTextCC);
            Button btnDeleteRuleCC = view.findViewById(R.id.btnDeleteRuleCC);

            txtRuleNumberCC.setText(Integer.toString(i+1) + ".");
            txtRuleTextCC.setText(listaReglas.get(i));

            final int position = i;
            btnDeleteRuleCC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listaReglas.remove(position);
                    lvRulesListCreate.setAdapter(new RulesListAdapter());
                    Toast.makeText(getApplicationContext(), "Regla eliminada", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteCreateCommunity extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String name, description, rules, isSubcommunity, photo, photo_thumbnail;

        public ExecuteCreateCommunity(String[] values) {
            this.name = values[0];
            this.description = values[1];
            this.rules = values[2];
            this.isSubcommunity= values[3];
            this.photo = values[4];
            this.photo_thumbnail = values[5];
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();

            String[] keys = {"id", "auth_token", "name", "description", "rules", "isSubcommunity", "photo", "photo_thumbnail"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), name, description, rules, isSubcommunity, photo, photo_thumbnail};
            isOk = api.post_put_base(keys, values, 8, "POST",0);

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
                    String token = response.getString("auth_token");
                    user.setAuth_token(token);
                    user.addCommunity_admin(response.getInt("id_community"));
                    LoginAcivity.actualizarAuth_Token(token, getApplicationContext());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    mensaje = "Comunidad creada";
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

            valores[4] = photo;
            valores[5] = photo_thumbnail;

            ExecuteCreateCommunity executeCreateCommunity = new ExecuteCreateCommunity(valores);
            executeCreateCommunity.execute();
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

                    imgComImage.setImageBitmap(bitmap);


                    //picturePath = getFileName(URI);
                    picturePath = getPath(CreateCommunityActivity.this,URI);
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
