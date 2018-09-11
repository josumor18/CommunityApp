package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class CreateCommunityActivity extends AppCompatActivity {

    Menu menuCreateCommunity;

    boolean isSubcommunity = false;

    ListView lvRulesListCreate;
    EditText edtxtCommName, edtxtCommDescription, edtxtReglaConvivencia;

    ArrayList<String> listaReglas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        isSubcommunity = getIntent().getBooleanExtra("isSubcommunity",false);

        lvRulesListCreate = findViewById(R.id.lvRulesListCreate);
        edtxtCommName = findViewById(R.id.edtxtCommName);
        edtxtCommName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(edtxtCommName.getText().toString().isEmpty()){
                    menuCreateCommunity.getItem(0).setEnabled(false);
                }else{
                    menuCreateCommunity.getItem(0).setEnabled(true);
                }
                return false;
            }
        });
        edtxtCommDescription  = findViewById(R.id.edtxtCommDescription);
        edtxtReglaConvivencia = findViewById(R.id.edtxtReglaConvivencia);

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

                String[] valores = new String[6];

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
                valores[4] = "";
                valores[5] = "";

                ExecuteCreateCommunity executeCreateCommunity = new ExecuteCreateCommunity(valores);
                executeCreateCommunity.execute();
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
                    user.setAuth_token(response.getString("auth_token"));
                    user.addCommunity_admin(response.getInt("id_community"));

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
}
