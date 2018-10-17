package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class ProfileUserActivity extends AppCompatActivity {

    ImageView imgProfileUser;
    TextView txtName, txtCel, txtTel, txtAddress;
    String idUser;
    Button btnEject;
    boolean isAdmin;
    User_Singleton user;
    int typeUser=0; //1 El mismo //2 Admin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        Intent intent = getIntent();
        user = User_Singleton.getInstance();

        imgProfileUser = findViewById(R.id.imgView_userProfile);
        txtName = findViewById(R.id.txt_nameUserProfile);
        txtCel = findViewById(R.id.txt_tel1UserProfile);
        txtTel = findViewById(R.id.txt_tel2UserProfile);
        txtAddress = findViewById(R.id.txt_addressUserProfile);
        btnEject = findViewById(R.id.btn_ejectUser);

        txtName.setText(intent.getStringExtra("name"));
        txtCel.setText(intent.getStringExtra("cel"));
        txtTel.setText(intent.getStringExtra("tel"));
        txtAddress.setText(intent.getStringExtra("address"));

        idUser = intent.getStringExtra("idUser");
        String photo = intent.getStringExtra("photo");

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if (!isAdmin && !(Integer.toString(user.getId()).equals(idUser)))
            btnEject.setVisibility(View.GONE);
        else if(Integer.toString(user.getId()).equals(idUser)) {
            typeUser = 1;  //Mismo usuario
            btnEject.setText("Abandonar comunidad");
        }
        else if(isAdmin)
            typeUser = 2;


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
                    R.drawable.user_box_photo);
        }
        imgProfileUser.setImageBitmap(userImage);
    }

    public void ejectUser(View view){
        ExecuteEjectUser executeEjectUser = new ExecuteEjectUser(idUser,CommunityActivity.idCommunity);
        executeEjectUser.execute();
    }

    public class ExecuteEjectUser extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int id_community;
        String id_user;



        ExecuteEjectUser(String id_user, int id_community){
            this.id_user = id_user;
            this.id_community = id_community;
        }


        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();


            String[] keys = {"id_user","id_community","auth_token"};
            String[] values = {id_user,Integer.toString(id_community),user.getAuth_token()};
            isOk = api.get_delete_base(keys, values, 40, "DELETE", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                /*JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                User_Singleton user = User_Singleton.getInstance();
                try {
                    String token = response.getString("auth_token");
                    user.setAuth_token(token);

                    LoginAcivity.actualizarAuth_Token(token, getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                for(User u : CommunityMembersFragment.listMembers){
                    if (u.getId().equals(id_user)){
                        CommunityMembersFragment.listMembers.remove(u);
                    }
                }
                Toast.makeText(getApplicationContext(), "Usuario expulsado", Toast.LENGTH_SHORT).show();
                if(typeUser==1){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if(typeUser == 2){
                    finish();
                }



            }else{
                String mensaje = "Error al expulsar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
