package elcarmen.project.community.Business;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.R;

public class ReportsActivity extends AppCompatActivity {

    int idUser;
    int idComment;
    String userComment;
    String optionSelected = "";
    RadioButton rbOne;
    RadioButton rbTwo;
    RadioButton rbThree;

    TextView txtUserComment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        idUser = getIntent().getIntExtra("idUser",0);
        idComment = getIntent().getIntExtra("idComment",0);
        userComment = getIntent().getStringExtra("username");

        txtUserComment = findViewById(R.id.txtUserReport);
        String texto = "Reportar comentario de " + userComment;
        txtUserComment.setText(texto);

        rbOne = findViewById(R.id.rb_oneReport);
        rbTwo = findViewById(R.id.rb_twoReport);
        rbThree = findViewById(R.id.rb_threeReport);



    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.rb_oneReport:
                if (checked) {
                    optionSelected = rbOne.getText().toString();
                    break;
                }
            case R.id.rb_twoReport:
                if (checked) {
                    optionSelected = rbTwo.getText().toString();
                    break;
                }
            case R.id.rb_threeReport:
                if(checked) {
                    optionSelected = rbThree.getText().toString();
                    break;
                }
        }
    }

    public void onClickReport (View view){
        if(optionSelected.equals("")){
            Toast.makeText(getApplicationContext(), "Seleccione una opcion", Toast.LENGTH_SHORT).show();
        }
        else{
            ExecuteReport executeReport = new ExecuteReport(idUser,idComment,optionSelected);
            executeReport.execute();
        }

    }

    public class ExecuteReport extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String idUser;
        String idComment;
        String reason;



        ExecuteReport(int idUser, int idCommunity, String option){
            this.idUser = Integer.toString(idUser);
            this.idComment = Integer.toString(idCommunity);
            this.reason = option;
        }


        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();

            //Falta ponerlo en API_Access
            String[] keys = {"idUser","idComment","reason"};
            String[] values = {idUser,idComment,reason};
            isOk = api.post_put_base(keys, values, 30, "POST", 0);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                String mensaje = "Reporte exitoso";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                finish();
            }else{
                String mensaje = "Error al reportar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
