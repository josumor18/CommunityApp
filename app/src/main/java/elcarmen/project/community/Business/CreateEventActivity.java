package elcarmen.project.community.Business;

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

import elcarmen.project.community.R;

public class CreateEventActivity extends AppCompatActivity {

    Menu menuCreateEvent;
    ImageView imgEventUploaded;
    EditText txtNewEventDate, txtNewEventDescription, txtNewEventTitle;
    CalendarView calendarView;
    NumberPicker nmbPckStartHour, nmbPckEndHour, nmbPckStartMinute, nmbPckEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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
                String fecha = dayOfMonth + "/" + month + "/" + year;
                txtNewEventDate.setText(fecha);
                view.setVisibility(View.GONE);
            }
        });

        nmbPckStartHour = findViewById(R.id.nmbPckStartHour);
        nmbPckStartHour.setMinValue(00);
        nmbPckStartHour.setMaxValue(23);

        nmbPckStartMinute = findViewById(R.id.nmbPckStartMinute);
        nmbPckStartMinute.setMinValue(00);
        nmbPckStartMinute.setMaxValue(59);

        nmbPckEndHour = findViewById(R.id.nmbPckEndHour);
        nmbPckEndHour.setMinValue(00);
        nmbPckEndHour.setMaxValue(23);

        nmbPckEndMinute = findViewById(R.id.nmbPckEndMinute);
        nmbPckEndMinute.setMinValue(00);
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
        return super.onOptionsItemSelected(item);
    }

    public void cargarImagen(View view){

    }
}
