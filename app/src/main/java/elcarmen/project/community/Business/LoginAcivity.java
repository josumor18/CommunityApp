package elcarmen.project.community.Business;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import elcarmen.project.community.R;

public class LoginAcivity extends AppCompatActivity {

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_Login);
    }

    public void click_btnLogin(View view){
        Toast.makeText(getApplicationContext(), "Sesión iniciada", Toast.LENGTH_LONG).show();

        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);
        finish();
    }
}
