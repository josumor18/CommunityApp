package elcarmen.project.community.Business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.SecureRandom;

import elcarmen.project.community.R;

public class FavoritesActivity  extends AppCompatActivity {
    Button btnPrueba;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnPrueba = findViewById(R.id.btnPrueba);
    }



    public void pruebaClicked(View view){
        Toast.makeText(FavoritesActivity.this, "hola", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
