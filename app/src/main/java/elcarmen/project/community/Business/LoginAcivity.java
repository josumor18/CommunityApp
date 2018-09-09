package elcarmen.project.community.Business;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class LoginAcivity extends AppCompatActivity {

    Button btnLogin;

    RelativeLayout rlLogin, rlLoginPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rlLogin = findViewById(R.id.rlLogin);
        rlLoginPB = findViewById(R.id.rlLoginPB);

        btnLogin = findViewById(R.id.btn_Login);
    }

    public void loginClicked(View view){
        ExecuteLogin executeLogin = new ExecuteLogin("prueba@email.com", "1234", 0);
        executeLogin.execute();

        rlLogin.setVisibility(View.INVISIBLE);
        rlLoginPB.setVisibility(View.VISIBLE);
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
        /*if(chckSesionActiva.isChecked()){
            guardarUsuarioSesion(user.getEmail(), user.getAuth_token());
        }
        */
        Toast.makeText(getApplicationContext(), "Sesión iniciada", Toast.LENGTH_LONG).show();

        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);
        finish();

        //rlLogin.setVisibility(View.VISIBLE);
        //rlLoginPB.setVisibility(View.INVISIBLE);
    }


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
