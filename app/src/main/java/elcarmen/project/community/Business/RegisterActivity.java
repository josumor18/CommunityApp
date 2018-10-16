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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPass, edtConfPass, edtCel, edtTel, edtAddress;
    ImageButton imageButton;

    ArrayList<EditText> campos_obligatorios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edt_nameUserRegister); edtEmail = findViewById(R.id.edt_emailUserRegister);
        edtPass = findViewById(R.id.edt_passUserRegister); edtConfPass = findViewById(R.id.edt_confpassUserRegister);
        edtCel = findViewById(R.id.edt_tel1UserRegister); edtTel = findViewById(R.id.edt_tel2UserRegister);
        edtAddress = findViewById(R.id.edt_addressUserRegister);



        campos_obligatorios = new ArrayList<>();
        campos_obligatorios.add(edtName);campos_obligatorios.add(edtEmail);campos_obligatorios.add(edtPass);
        campos_obligatorios.add(edtConfPass);


    }

    public void registerClicked (View view){

        //Validaciones de campos
        String pass1 = edtPass.getText().toString();
        String pass2 = edtConfPass.getText().toString();

        for(EditText edit: campos_obligatorios){
            if(TextUtils.isEmpty(edit.getText())){
                edit.setError("Campo obligatorio");
            }
        }

        if(pass1.length()<4){
            //Toast.makeText(RegistrarActivity.this, "La contraseña debe contener minimo 6 caracteres", Toast.LENGTH_LONG).show();
            edtPass.setError("Min. 4 caracteres");
            edtPass.setText("");
            edtConfPass.setText("");
        }
        else if(!pass1.equals(pass2)){

            //txtInvalidRegis.setVisibility(1);
            //Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            edtConfPass.setError("Contraseñas no coinciden");
            edtPass.setText("");
            edtConfPass.setText("");

        }
        else{


            ExecuteRegister register = new ExecuteRegister(edtName.getText().toString(),edtEmail.getText().toString()
                    ,edtPass.getText().toString(), edtCel.getText().toString()
                    ,edtTel.getText().toString(), edtAddress.getText().toString());
            register.execute();

        }

    }

    public class ExecuteRegister extends AsyncTask<String,Void,String> {
        private String name;
        private String email;
        private String cel;
        private String tel;
        private String address;
        private String password;
        private boolean isOk;

        public ExecuteRegister(String name, String email,String pass, String cel, String tel, String address){
            this.cel = cel;
            this.tel = tel;
            this.address = address;
            this.name = name;
            this.email = email;
            this.password = pass;
        }

        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            String[] keys = {"name", "email", "password","cel","tel","address"};
            String[] values = {name,email,password,cel,tel,address};
            isOk = api.post_put_base(keys, values, 41, "POST",0);



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOk) {
                Toast.makeText(RegisterActivity.this, "Registro exitoso" , Toast.LENGTH_SHORT).show();
                finish();

                    /*ExecuteUploaded executeUploaded = new ExecuteUploaded();
                    executeUploaded.execute();*/


            }
            else {
                /*String mensaje = "Fail";
                try {
                    mensaje = (API_Access.getInstance().getJsonObjectResponse()).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                Toast.makeText(RegisterActivity.this, "Registro fallido" , Toast.LENGTH_SHORT).show();
            }

        }




    }


}
