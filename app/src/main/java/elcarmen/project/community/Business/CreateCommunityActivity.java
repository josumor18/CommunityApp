package elcarmen.project.community.Business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class CreateCommunityActivity extends AppCompatActivity {

    Menu menuCreateCommunity;

    ListView lvRulesListCreate;
    EditText edtxtCommName, edtxtCommDescription, edtxtReglaConvivencia;

    ArrayList<String> listaReglas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

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
            btnDeleteRuleCC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Regla eliminada", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }
}
