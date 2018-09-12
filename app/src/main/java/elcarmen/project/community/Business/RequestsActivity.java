package elcarmen.project.community.Business;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class RequestsActivity extends AppCompatActivity {

    ArrayList<User> users_requests = new ArrayList<User>();

    ListView lvUsersRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvUsersRequests = findViewById(R.id.lvUsersRequests);

        ExecuteGetRequests executeGetRequests = new ExecuteGetRequests();
        executeGetRequests.execute();
    }

    public void cargarSolicitudes(JSONObject jsonResult){
        try {
            users_requests.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            //LoginActivity.actualizarAuth_Token(token, getActivity());
            //JSONArray jsonListUserPosts = jsonResult.getJSONArray("posts");
            JSONArray jsonUsersList = jsonResult.getJSONArray("users");
            for (int i = 0; i < jsonUsersList.length(); i++) {
                JSONObject jsonUser = jsonUsersList.getJSONObject(i);

                User user = new User(jsonUser.getString("id"), jsonUser.getString("name"), "", "", "", "", jsonUser.getString("photo"), jsonUser.getString("photo_thumbnail"), null, null, true);

                users_requests.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvUsersRequests.setAdapter(new RequestsAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class RequestsAdapter extends BaseAdapter {

        public RequestsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return users_requests.size();
        }

        @Override
        public Object getItem(int i) {
            return users_requests.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.requests_list_item, null);
            }

            ImageView imgUserReqItem = view.findViewById(R.id.img_UserReqItem);
            TextView txtUsernameReqItem = view.findViewById(R.id.txtUsernameReqItem);
            Button btnAceptRequest = view.findViewById(R.id.btnAcceptRequest);
            Button btnRefuseRequest = view.findViewById(R.id.btnRefuseRequest);

            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(users_requests.get(i).getUrl_photo()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                        R.drawable.user_box_photo);
            }
            imgUserReqItem.setImageBitmap(userImage);

            txtUsernameReqItem.setText(users_requests.get(i).getName());

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetRequests extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            String[] keys = {"id", "auth_token", "id_community"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(CommunityActivity.idCommunity)};
            isOk = api.get_delete_base(keys, values, 4, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarSolicitudes(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las solicitudes";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}