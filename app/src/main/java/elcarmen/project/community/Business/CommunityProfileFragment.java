package elcarmen.project.community.Business;


import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityProfileFragment extends Fragment {


    public CommunityProfileFragment() {
        // Required empty public constructor
    }

    String[] valores = new String[7];

    EditText edtName, edtDescription, edtRule;
    ImageView imgCom;
    Button btnAddRule, btnUpImage , btnEditCom;
    boolean isSubcommunity = false;

    int idCommu;

    ListView lvRulesList;
    ArrayList<String> listaReglas = new ArrayList<String>();

    User_Singleton user;
    boolean isAdmin;

    private static int IMG_RESULT = 1;
    String picturePath;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    String photo = "";
    String photo_thumbnail = "";
    InputStream in = null;
    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community_profile, container, false);

        edtName = v.findViewById(R.id.edtxtCommNameProfile);edtDescription = v.findViewById(R.id.edtxtCommDescriptionProfile);
        edtRule = v.findViewById(R.id.edtxtReglaConvivProfile);
        imgCom = v.findViewById(R.id.imgComImageProfile);
        lvRulesList = v.findViewById(R.id.lvRulesListCreateProfile);

        btnAddRule = v.findViewById(R.id.btnAddRuleProfile); btnUpImage = v.findViewById(R.id.btnUploadComImageProfile);
        btnEditCom = v.findViewById(R.id.btn_EditCommunity);

        user = User_Singleton.getInstance();

        //String photo = "";

        for(Community e: (CommunitiesFragment.communities)){
            if (e.getId() == CommunityActivity.idCommunity){
                photo = e.getUrl_photo();
                photo_thumbnail = e.getUrl_photo_rounded();
                edtDescription.setText(e.getDescription());
                for(String ru:e.getRules()){
                    listaReglas.add(ru);
                }

            }
        }

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap commImage = null;
        try {
            commImage = request.execute(photo).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (commImage == null) {
            commImage = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.img_comm_default);
        }

        imgCom.setImageBitmap(commImage);
        edtName.setText(CommunityActivity.nameCommunity);

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if(!isAdmin){
            disableEditText(edtName);
            disableEditText(edtDescription);
            edtRule.setVisibility(View.GONE);
            btnUpImage.setVisibility(View.GONE);
            btnAddRule.setVisibility(View.GONE);
            btnEditCom.setVisibility(View.GONE);


        }
        btnUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_RESULT);
            }
        });

        btnAddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newRule = edtRule.getText().toString();
                if(newRule.isEmpty()){
                    Toast.makeText(getContext(), "Ingrese una regla", Toast.LENGTH_SHORT).show();
                }else {
                    listaReglas.add(newRule);
                    lvRulesList.setAdapter(new RulesListAdapter());
                    edtRule.setText("");
                }
            }
        });

        btnEditCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valores[0] = Integer.toString(CommunityActivity.idCommunity);
                valores[1] = edtName.getText().toString();
                valores[2] = edtDescription.getText().toString();
                ArrayList<String> rulesAux = new ArrayList<String>();
                String rulesArrayToString = "[";
                for(int i = 0; i < listaReglas.size(); i++){
                    rulesAux.add(listaReglas.get(i));
                    rulesArrayToString += listaReglas.get(i) + ",";
                }
                rulesAux.add("");
                rulesArrayToString += "]";
                valores[3] = rulesAux.toString();
                valores[3] = rulesArrayToString;

                if(isSubcommunity){
                    valores[4] = "true";
                }else{
                    valores[4] = "false";
                }

                valores[5] = photo;
                valores[6] = photo_thumbnail;

                /*ExecuteUploaded executeUploaded = new ExecuteUploaded();
                executeUploaded.execute();*/

                ExecuteEditCommunity executeEditCommunity = new ExecuteEditCommunity(valores);
                executeEditCommunity.execute();
            }
        });

        lvRulesList.setAdapter(new RulesListAdapter());

        return v;
    }



    private void disableEditText(EditText editText) {
        //editText.setFocusable(false);
        //editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);

    }


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
                    lvRulesList.setAdapter(new RulesListAdapter());
                    Toast.makeText(getContext(), "Regla eliminada", Toast.LENGTH_SHORT).show();
                }
            });

            if(!isAdmin)
                btnDeleteRuleCC.setVisibility(View.GONE);

            return view;
        }
    }



    public class ExecuteEditCommunity extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String idCommunity, name, description, rules, isSubcommunity, photo, photo_thumbnail;

        public ExecuteEditCommunity(String[] values) {
            this.idCommunity = values[0];
            this.name = values[1];
            this.description = values[2];
            this.rules = values[3];
            this.isSubcommunity= values[4];
            this.photo = values[5];
            this.photo_thumbnail = values[6];
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

            String[] keys = {"id", "auth_token", "idCommunity","name", "description", "rules", "isSubcommunity", "photo", "photo_thumbnail"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), idCommunity, name, description, rules, isSubcommunity, photo, photo_thumbnail};
            isOk = api.post_put_base(keys, values, 42, "PUT",1);

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
                    String token = response.getString("auth_token");
                    user.setAuth_token(token);
                    //user.addCommunity_admin(response.getInt("id_community"));
                    LoginAcivity.actualizarAuth_Token(token, getContext());

                    /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();*/

                    mensaje = "Cambios guardados";
                    //getActivity().getActionBar().setSubtitle(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //cargarCommunities(API_Access.getInstance().getJsonObjectResponse());
            }else{
                mensaje = "Error al editar comunidad";
            }

            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    public class ExecuteUploaded extends AsyncTask<String, Void, String> {
        boolean isOk;
        String rand ="";
        Cloudinary cloudinary = new Cloudinary("cloudinary://523642389612375:w_BVcUQ7VFZ8IQj-iE1-zbqv5iU@ddgkzz2gk");

        @Override
        protected String doInBackground(String... strings) {


            try {
                String path = picturePath; //Aquí sería la ruta donde se haya seleccionado la imagen
                File file = new File(picturePath);
                File f = file.getAbsoluteFile();

                ///////////////////////////////////////////////////////////
                // Generar el random para identificador de la foto
                StringBuilder sb = new StringBuilder(10);
                for( int i = 0; i < 10; i++ )
                    sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
                rand = sb.toString();
                ///////////////////////////////////////////////////////////

                cloudinary.uploader().upload(in, ObjectUtils.asMap("public_id", rand));//.getAbsoluteFile(), ObjectUtils.asMap("public_id", rand));//emptyMap());

                photo_thumbnail = (cloudinary.url().transformation(new Transformation()
                        .radius(360).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
                // Normal:
                photo = (cloudinary.url().transformation(new Transformation()
                        .radius(0).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1];



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            photo_thumbnail = (cloudinary.url().transformation(new Transformation()
                    .radius(360).crop("scale").chain()
                    .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
            // Normal:
            photo = (cloudinary.url().transformation(new Transformation()
                    .radius(0).crop("scale").chain()
                    .angle(0)).imageTag(rand + ".png")).split("\'")[1];

            valores[5] = photo;
            valores[6] = photo_thumbnail;

            ExecuteEditCommunity executeCreateCommunity = new ExecuteEditCommunity(valores);
            executeCreateCommunity.execute();
        }
    }





}
