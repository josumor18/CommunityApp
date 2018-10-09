package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class ChatActivity extends AppCompatActivity {

    private int id_chat;
    private boolean is_group;
    private String user_name;
    ListView lvChatMessages;

    ArrayList<Message> messages = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        id_chat = intent.getIntExtra("id_chat", -1);
        is_group = intent.getBooleanExtra("is_group", false);

        user_name = intent.getStringExtra("user_name");
        if(!is_group){
            getSupportActionBar().setTitle(user_name);
        }else{
            getSupportActionBar().setTitle("Chat comunal");
            user_name = "";
        }
        getSupportActionBar().setSubtitle(intent.getStringExtra("community_name"));

        lvChatMessages = findViewById(R.id.lvChatMessages);
        lvChatMessages.setDivider(null);

        ExecuteGetMessages executeGetMessages = new ExecuteGetMessages();
        executeGetMessages.execute();
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

    public void send_message(View view){

        // setear el atributo "sent" de Message en false
    }

    private void cargarMessages(JSONObject jsonResult) {

        try {
            messages.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            LoginAcivity.actualizarAuth_Token(token, getApplicationContext());

            JSONArray jsonMessagesList = jsonResult.getJSONArray("messages_list");

            for (int i = 0; i < jsonMessagesList.length(); i++) {
                JSONObject jsonMessage = (JSONObject) jsonMessagesList.get(i);

                Message message = new Message(jsonMessage.getInt("id"), jsonMessage.getInt("id_chat"),
                        jsonMessage.getInt("id_user"), jsonMessage.getString("message"),
                        jsonMessage.getBoolean("seen"), jsonMessage.getString("created_at"));

                messages.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvChatMessages.setAdapter(new MessagesAdapter());

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class MessagesAdapter extends BaseAdapter {

        public MessagesAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.chat_item, null);
            }

            ImageView imgContactMessage = view.findViewById(R.id.imgContactMessage);
            TextView txtContactName_Message = view.findViewById(R.id.txtContactName_Message);
            TextView txtContactMessage = view.findViewById(R.id.txtContactMessage);
            TextView txtMessageContactDateTime = view.findViewById(R.id.txtMessageContactDateTime);
            TextView txtUserMessage = view.findViewById(R.id.txtUserMessage);
            TextView txtMessageUserDateTime = view.findViewById(R.id.txtMessageUserDateTime);
            ImageView imgClock = view.findViewById(R.id.imgClock);

            ArrayList<User> users = CommunityActivity.listUsers;
            if(is_group){
                for(User u: users){
                    if(Integer.parseInt(u.getId()) == messages.get(i).getId_user()){
                        user_name = u.getName();
                    }
                }
            }

            if(messages.get(i).getId_user() == User_Singleton.getInstance().getId()){
                txtUserMessage.setText(messages.get(i).getMessage());
                if(messages.get(i).isSent()){
                    txtUserMessage.setBackground(getResources().getDrawable(R.drawable.rounded_user_message));
                    txtMessageUserDateTime.setText(messages.get(i).getCreated_at_to_String());
                    imgClock.setVisibility(View.GONE);
                }else{
                    txtMessageUserDateTime.setVisibility(View.GONE);

                }

                imgContactMessage.setVisibility(View.GONE);
                txtContactName_Message.setVisibility(View.GONE);
                txtContactMessage.setVisibility(View.GONE);
                txtMessageContactDateTime.setVisibility(View.GONE);
            }else{
                String link_photo = "";
                for(User u: users){
                    if(Integer.parseInt(u.getId()) == messages.get(i).getId_user()){
                        link_photo = u.getUrl_photo_rounded();
                    }
                }

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap userImage = null;
                if(!link_photo.isEmpty()){
                    try {
                        userImage = request.execute(link_photo).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                if(userImage == null){
                    userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                            R.drawable.user_rounded_photo);
                }
                imgContactMessage.setImageBitmap(userImage);

                txtContactName_Message.setText(user_name);
                txtContactMessage.setText(messages.get(i).getMessage());
                txtMessageContactDateTime.setText(messages.get(i).getCreated_at_to_String());

                txtUserMessage.setVisibility(View.GONE);
                txtMessageUserDateTime.setVisibility(View.GONE);
                imgClock.setVisibility(View.GONE);
            }

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetMessages extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        public ExecuteGetMessages() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            String[] keys = {"id", "auth_token", "id_chat", "last_id"};
            int last_id = -1;
            if(messages.size() > 0){
                last_id = messages.get(messages.size()-1).getId();
            }
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(id_chat), Integer.toString(last_id)};
            isOk = api.get_delete_base(keys, values, 37, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarMessages(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener los chats";

                //Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
