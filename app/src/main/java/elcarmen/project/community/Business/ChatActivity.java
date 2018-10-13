package elcarmen.project.community.Business;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    //ListView lvChatMessages;
    RecyclerView lvChatMessages;
    TextView txtMessage;

    private boolean shutdown = false;
    Runnable runnable;
    final Handler handler  = new Handler();

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
        lvChatMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));

        txtMessage = findViewById(R.id.txtMessage);

        runnable = new Runnable() {
            public void run() {
                if(!shutdown){
                    int last_id = -1;
                    if(!messages.isEmpty()){
                        //last_id = messages.get(messages.size() - 1).getId();
                        for(int ind = messages.size() - 1; ind > -1; ind--){
                            if(messages.get(ind).isSent()){
                                last_id = messages.get(ind).getId();
                                ind = -1;
                            }
                        }
                    }
                    ExecuteGetMessages executeGetMessages = new ExecuteGetMessages(true, last_id);
                    executeGetMessages.execute();
                    //Toast.makeText(getApplicationContext(), "Getting messages", Toast.LENGTH_SHORT).show();
                }

                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //shutdown = true;
                handler.removeCallbacks(runnable);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void send_message(View view){
        String texto = txtMessage.getText().toString();
        if(!texto.isEmpty()){
            int last_id = -1;
            if(!messages.isEmpty()){
                last_id = messages.get(messages.size() - 1).getId();
            }

            // setear el atributo "sent" de Message en false
            Message newMessage = new Message(-1, id_chat, User_Singleton.getInstance().getId(), texto, false, "2018-10-05T05:32:19.709Z");
            newMessage.setSent(false);

            messages.add(newMessage);

            //lvChatMessages.setAdapter(new MessagesAdapter());
            MessagesAdapter messagesAdapter = new MessagesAdapter(messages);
            lvChatMessages.setAdapter(messagesAdapter);
            lvChatMessages.scrollToPosition(messages.size()-1);

            ExecuteGetMessages executeGetMessages = new ExecuteGetMessages(false, last_id);
            executeGetMessages.execute(texto);

        }
    }

    private void cargarMessages(JSONObject jsonResult) {

        ArrayList<Message> newMessages = new ArrayList<Message>();
        int jsonCount_my_messages = 0;
        try {
            //messages.clear();
            //String token = jsonResult.getString("auth_token");
            //User_Singleton.getInstance().setAuth_token(token);
            //LoginAcivity.actualizarAuth_Token(token, getApplicationContext());

            JSONArray jsonMessagesList = jsonResult.getJSONArray("messages_list");

            for (int i = 0; i < jsonMessagesList.length(); i++) {
                JSONObject jsonMessage = (JSONObject) jsonMessagesList.get(i);

                Message message = new Message(jsonMessage.getInt("id"), jsonMessage.getInt("id_chat"),
                        jsonMessage.getInt("id_user"), jsonMessage.getString("message"),
                        jsonMessage.getBoolean("seen"), jsonMessage.getString("created_at"));

                //messages.add(message);
                newMessages.add(message);
            }

            jsonCount_my_messages = jsonResult.getInt("count_my_messages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int count_my_messages = 0;
        for(Message mes: messages){
            if(mes.getId_user() == User_Singleton.getInstance().getId()){
                count_my_messages++;
            }
        }

        if(!newMessages.isEmpty()){
            for(int i = 0; i < messages.size(); i++){
                if((!messages.get(i).isSent()) && (count_my_messages > jsonCount_my_messages ||
                        (count_my_messages == jsonCount_my_messages && count_my_messages == 1))){
                    messages.remove(i);
                }
            }
            for(Message m: newMessages){
                messages.add(m);
            }

            MessagesAdapter messagesAdapter = new MessagesAdapter(messages);
            lvChatMessages.removeAllViews();
            lvChatMessages.setAdapter(messagesAdapter);
            lvChatMessages.scrollToPosition(messages.size()-1);
        }
        //lvChatMessages.setAdapter(new MessagesAdapter());

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolderDatos>{

        ArrayList<Message> messageArrayList;

        public MessagesAdapter(ArrayList<Message> messageArrayList) {
            this.messageArrayList = messageArrayList;
        }

        @NonNull
        @Override
        public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item, null, false);
            return new ViewHolderDatos(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
            holder.asignarDatos(messageArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return messageArrayList.size();
        }

        public class ViewHolderDatos extends RecyclerView.ViewHolder {
            TextView txtContactName_Message, txtContactMessage, txtMessageContactDateTime, txtUserMessage, txtMessageUserDateTime;
            ImageView imgContactMessage, imgClock;


            public ViewHolderDatos(View itemView) {
                super(itemView);
                txtContactName_Message = itemView.findViewById(R.id.txtContactName_Message);
                txtContactMessage = itemView.findViewById(R.id.txtContactMessage);
                txtMessageContactDateTime = itemView.findViewById(R.id.txtMessageContactDateTime);
                txtUserMessage = itemView.findViewById(R.id.txtUserMessage);
                txtMessageUserDateTime = itemView.findViewById(R.id.txtMessageUserDateTime);
                txtMessageUserDateTime = itemView.findViewById(R.id.txtMessageUserDateTime);
                imgContactMessage = itemView.findViewById(R.id.imgContactMessage);
                imgClock = itemView.findViewById(R.id.imgClock);
            }

            public void asignarDatos(Message message) {
                ArrayList<User> users = CommunityActivity.listUsers;
                if(is_group){
                    for(User u: users){
                        if(Integer.parseInt(u.getId()) == message.getId_user()){
                            user_name = u.getName();
                        }
                    }
                }

                if(message.getId_user() == User_Singleton.getInstance().getId()){
                    txtUserMessage.setText(message.getMessage());
                    if(message.isSent()){
                        txtUserMessage.setBackground(getResources().getDrawable(R.drawable.rounded_user_message));
                        txtMessageUserDateTime.setText(message.getCreated_at_to_String());
                        imgClock.setVisibility(View.GONE);
                    }else{
                        txtMessageUserDateTime.setVisibility(View.GONE);
                    }
                    int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
                    txtUserMessage.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

                    imgContactMessage.setVisibility(View.GONE);
                    txtContactName_Message.setVisibility(View.GONE);
                    txtContactMessage.setVisibility(View.GONE);
                    txtMessageContactDateTime.setVisibility(View.GONE);
                }else {
                    String link_photo = "";
                    for (User u : users) {
                        if (Integer.parseInt(u.getId()) == message.getId_user()) {
                            link_photo = u.getUrl_photo_rounded();
                        }
                    }

                    HttpGetBitmap request = new HttpGetBitmap();
                    Bitmap userImage = null;
                    if (!link_photo.isEmpty()) {
                        try {
                            userImage = request.execute(link_photo).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    if (userImage == null) {
                        userImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.user_rounded_photo);
                    }
                    imgContactMessage.setImageBitmap(userImage);

                    txtContactName_Message.setText(user_name);
                    txtContactMessage.setText(message.getMessage());
                    txtMessageContactDateTime.setText(message.getCreated_at_to_String());

                    txtUserMessage.setVisibility(View.GONE);
                    txtMessageUserDateTime.setVisibility(View.GONE);
                    imgClock.setVisibility(View.GONE);
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetMessages extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        boolean forGet = false;
        int last_id = -1;

        public ExecuteGetMessages(boolean forGet, int last_id) {
            this.forGet = forGet;
            this.last_id = last_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            if(forGet){
                String[] keys = {"id", "auth_token", "id_chat", "last_id"};
                String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(id_chat), Integer.toString(last_id)};
                isOk = api.get_delete_base(keys, values, 37, "GET",1);
            }else{
                String[] keys = {"id", "auth_token", "id_chat", "message"};
                String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(id_chat), strings[0]};
                isOk = api.post_put_base(keys, values, 36, "POST",1);
            }



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                if(forGet){
                    cargarMessages(API_Access.getInstance().getJsonObjectResponse());
                }else{
                    txtMessage.setText("");
                    //ExecuteGetMessages executeGetMessages = new ExecuteGetMessages(true, last_id);
                    //executeGetMessages.execute();
                }
            }else{
                String mensaje = "Error al obtener los chats";

                //Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
