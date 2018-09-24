package elcarmen.project.community.Business;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class LoginAcivity extends AppCompatActivity {

    EditText edtxtEmail, edtxtPassword;
    Button btnLogin;
    CheckBox chbxSesionActiva;

    RelativeLayout rlLogin, rlLoginPB;

    private static final String USER_PREFERENCES = "user.preferences.elcarmen.community";
    private static final String PREFERENCE_EMAIL = "string.email.sesion";
    private static final String PREFERENCE_AUTH_TOKEN = "string.token.sesion";
    private static final String PREFERENCE_SESION_ACTIVA = "boolean.sesion.isActiva";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rlLogin = findViewById(R.id.rlLogin);
        rlLoginPB = findViewById(R.id.rlLoginPB);

        edtxtEmail = findViewById(R.id.edtxtEmail);
        edtxtPassword = findViewById(R.id.edtxtPassword);
        btnLogin = findViewById(R.id.btn_Login);
        chbxSesionActiva = findViewById(R.id.chbxSesionActiva);

        if(getEstadoSesion()){
            String[] userData = getUsuarioSesion();

            ExecuteLogin executeLogin = new ExecuteLogin(userData[0], userData[1], 1);
            executeLogin.execute();

            rlLogin.setVisibility(View.INVISIBLE);
            rlLoginPB.setVisibility(View.VISIBLE);
        }
    }

    public void loginClicked(View view){
        String email = "";
        String password = "";

        email = edtxtEmail.getText().toString();
        password = edtxtPassword.getText().toString();

        if(!(email.isEmpty() && password.isEmpty())){
            ExecuteLogin executeLogin = new ExecuteLogin(email, password, 0);
            executeLogin.execute();

            rlLogin.setVisibility(View.INVISIBLE);
            rlLoginPB.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getApplicationContext(), "Datos incompletos", Toast.LENGTH_SHORT).show();
        }


    }

    public void iniciarSesion(JSONObject response){
        User_Singleton user = User_Singleton.getInstance();

        try {
            JSONArray coms_admins = response.getJSONArray("list_com_admin");
            response = response.getJSONObject("data");
            user.setId(response.getInt("id"));
            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));
            user.setCel(response.getString("cel"));
            user.setTel(response.getString("tel"));
            user.setAddress(response.getString("address"));
            user.setUrl_photo(response.getString("photo"));
            user.setUrl_photo_rounded(response.getString("photo_thumbnail"));
            user.setPrivateProfile(response.getBoolean("isPrivate"));
            user.setAuth_token(response.getString("auth_token"));
            user.setCommunities_admin(coms_admins);
            user.setApplicationContext(getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap coverImage = null;
        try {
            coverImage = request.execute(user.getUrl_photo()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(coverImage == null){
            coverImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                    R.drawable.user_box_photo);
        }
        user.setPhoto(coverImage);

        HttpGetBitmap request_r = new HttpGetBitmap();
        Bitmap coverImage_r = null;
        try {
            coverImage_r = request_r.execute(user.getUrl_photo_rounded()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(coverImage_r == null){
            coverImage_r = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                    R.drawable.user_rounded_photo);
        }
        user.setPhoto_rounded(coverImage_r);
        //Si son correctas...hacer:
        if(chbxSesionActiva.isChecked()){
            guardarUsuarioSesion(user.getEmail(), user.getAuth_token());
        }

        Toast.makeText(getApplicationContext(), "Sesión iniciada", Toast.LENGTH_LONG).show();

        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);
        finish();

        //rlLogin.setVisibility(View.VISIBLE);
        //rlLoginPB.setVisibility(View.INVISIBLE);
    }

    //------------------------------------------------------------------------------------------------------//
    //----------------------------- Obtiene/Guarda las preferencias de sesion ------------------------------//
    //------------------------------------------------------------------------------------------------------//
    public void guardarUsuarioSesion(String email, String auth_token){
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        preferences.edit().putString(PREFERENCE_EMAIL, email).apply();
        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, auth_token).apply();
        preferences.edit().putBoolean(PREFERENCE_SESION_ACTIVA, chbxSesionActiva.isChecked()).apply();
    }

    public static void actualizarAuth_Token(String auth_token, Context c){
        SharedPreferences preferences = c.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, auth_token).apply();
    }

    public static void cerrarSesion(Context c){
        User_Singleton user = User_Singleton.getInstance();
        user.setEmail("");
        user.setAuth_token("");
        user.setApplicationContext(null);

        SharedPreferences preferences = c.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        preferences.edit().putString(PREFERENCE_EMAIL, "").apply();
        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, "").apply();
        preferences.edit().putBoolean(PREFERENCE_SESION_ACTIVA, false).apply();
    }

    public String[] getUsuarioSesion(){
        String[] userData = new String[2];
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        //Esto es para probar unicamente...después habría que ver si lo que se obtiene son todos los datos del usuario o que (segun lo que se haya guardado)...
        userData[0] = preferences.getString(PREFERENCE_EMAIL, "");
        userData[1] = preferences.getString(PREFERENCE_AUTH_TOKEN, "");
        return userData;
    }

    public boolean getEstadoSesion(){
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_SESION_ACTIVA, false);
    }
    // ========================================================================================== //


    // ========================================================================================== //
    public class ExecuteLogin extends AsyncTask<String,Void,String> {
        int tipoAutenticacion = 0;// 0-Formulario, 1-Authentication Token(sesion abierta)
        private String[] keys = new String[2];
        private String[] values = new String[2];
        private boolean isLogged = false;


        //Login con los campos de email y contraseña o authentication token
        public ExecuteLogin(String email, String pass_token, int tipo){
            tipoAutenticacion = tipo;
            keys[0] = "email";
            if(tipoAutenticacion == 0){
                keys[1] = "password";
            }else{
                keys[1] = "auth_token";
            }

            values[0] = email;
            values[1] = pass_token;
        }



        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();

            isLogged = api.post_put_base(keys, values, 1, "POST", 1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isLogged) {
                iniciarSesion(API_Access.getInstance().getJsonObjectResponse());
            } else{
                Toast.makeText(LoginAcivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();

                rlLogin.setVisibility(View.VISIBLE);
                rlLoginPB.setVisibility(View.INVISIBLE);
            }

        }
    }
}
