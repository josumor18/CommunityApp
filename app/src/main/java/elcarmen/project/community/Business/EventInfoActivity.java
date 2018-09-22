package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class EventInfoActivity extends AppCompatActivity {

    ImageView imgEventInfoPhoto;
    TextView txtEventInfoTitle, txtEventInfoTerminado, txtEventInfoDescription, txtEventInfoFecha, txtEventInfoHInicio, txtEventInfoHFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(CommunityActivity.nameCommunity);

        imgEventInfoPhoto = findViewById(R.id.imgEventInfoPhoto);
        txtEventInfoTitle = findViewById(R.id.txtEventInfoTitle);
        txtEventInfoTerminado = findViewById(R.id.txtEventInfoTerminado);
        txtEventInfoDescription = findViewById(R.id.txtEventInfoDescription);
        txtEventInfoFecha = findViewById(R.id.txtEventInfoFecha);
        txtEventInfoHInicio = findViewById(R.id.txtEventInfoHInicio);
        txtEventInfoHFin = findViewById(R.id.txtEventInfoHFin);

        Intent intent = getIntent();
        txtEventInfoTitle.setText(intent.getStringExtra("title"));
        txtEventInfoDescription.setText(intent.getStringExtra("description"));
        txtEventInfoFecha.setText(intent.getStringExtra("date"));

        String[] hours = intent.getStringExtra("hours").split(" - ");
        txtEventInfoHInicio.setText(hours[0]);
        txtEventInfoHFin.setText(hours[1]);

        String photo = intent.getStringExtra("photo");

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap userImage = null;
        try {
            userImage = request.execute(photo).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (userImage == null) {
            userImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                    R.drawable.img_event_default);
        }
        imgEventInfoPhoto.setImageBitmap(userImage);

        if(!intent.getBooleanExtra("terminado", false)){
            txtEventInfoTerminado.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
