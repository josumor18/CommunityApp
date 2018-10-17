package elcarmen.project.community.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityChatListFragment extends Fragment {

    Runnable runnable;
    final Handler handler  = new Handler();

    public static boolean returned = false;

    ListView lvChatsList;

    ArrayList<Chat> chats = new ArrayList<Chat>();

    public CommunityChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community_chat_list, container, false);
        lvChatsList = v.findViewById(R.id.lvChatsList);
        lvChatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);

                Chat selected = chats.get(position);
                intent.putExtra("id_chat", selected.getId());
                intent.putExtra("user_name", selected.getName_user());
                intent.putExtra("community_name", selected.getCommunity_name());
                intent.putExtra("is_group", selected.isIs_group());
                startActivity(intent);

                handler.removeCallbacks(runnable);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(returned){
            returned = false;
            executeRunnable();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            executeRunnable();
        }else{
            handler.removeCallbacks(runnable);
        }
    }

    private void executeRunnable(){
        runnable = new Runnable() {
            public void run() {
                ExecuteGetChats executeGetChats = new ExecuteGetChats();
                executeGetChats.execute();
                //Toast.makeText(getActivity(), "Getting chats", Toast.LENGTH_SHORT).show();

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void cargarChats(JSONObject jsonResult) {

        try {
            chats.clear();
            //String token = jsonResult.getString("auth_token");
            //User_Singleton.getInstance().setAuth_token(token);
            //LoginAcivity.actualizarAuth_Token(token, getActivity());

            JSONArray jsonChatsList = jsonResult.getJSONArray("chats");
            JSONArray jsonLastMessagesList = jsonResult.getJSONArray("last_msg");
            for (int i = 0; i < jsonChatsList.length(); i++) {
                JSONObject jsonChat = (JSONObject) jsonChatsList.get(i);
                JSONObject jsonLastMessage = null;
                if(!jsonLastMessagesList.get(i).equals(null)){
                    jsonLastMessage = (JSONObject) jsonLastMessagesList.get(i);
                }

                boolean is_group = jsonChat.getBoolean("is_group");
                int id_user = -1;
                String userName = "";

                if(!is_group){
                    id_user = jsonChat.getInt("id_user");

                    if(!(User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity))){//jsonChatsList.length() == 2){
                        userName = "Administración";
                    }else{
                        ArrayList<User> users = CommunityActivity.listUsers;

                        for(User u: users){
                            if(Integer.parseInt(u.getId()) == id_user){
                                userName = u.getName();
                            }
                        }
                    }
                }


                Message last_msg = null;
                if(jsonLastMessage != null){
                    last_msg = new Message(jsonLastMessage.getInt("id"),
                            jsonLastMessage.getInt("id_chat"), jsonLastMessage.getInt("id_user"),
                            jsonLastMessage.getString("message"), jsonLastMessage.getBoolean("seen"),
                            jsonLastMessage.getString("created_at"));
                }

                Chat chat = new Chat(jsonChat.getInt("id"), jsonChat.getInt("id_community"),
                        CommunityActivity.nameCommunity, is_group,
                        id_user, userName, last_msg);

                chat.setLast_message(last_msg);

                chats.add(chat);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvChatsList.setAdapter(new ChatsAdapter());

    }




    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ChatsAdapter extends BaseAdapter {

        public ChatsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return chats.size();
        }

        @Override
        public Object getItem(int i) {
            return chats.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.chat_list_item, null);
            }

            ImageView imgChatUser = view.findViewById(R.id.imgChatUser);
            TextView txtUsernameChat = view.findViewById(R.id.txtUsernameChat);
            TextView txtLastMessage = view.findViewById(R.id.txtLastMessage);

            String link_photo = "";
            if (!chats.get(i).isIs_group()){
                if(chats.size() > 2){
                    for (User u : CommunityActivity.listUsers) {
                        if (Integer.parseInt(u.getId()) == chats.get(i).getId_user()){
                            link_photo = u.getUrl_photo_rounded();
                            continue;
                        }
                    }
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
                userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
                        R.drawable.user_rounded_photo);

                if(chats.get(i).isIs_group()){
                    userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
                            R.drawable.twotone_public_black_24);
                }
            }
            imgChatUser.setImageBitmap(userImage);

            String chatName = "";

            if(chats.get(i).isIs_group()){
                chatName = chats.get(i).getCommunity_name();
            }else{
                chatName = chats.get(i).getName_user();
            }
            txtUsernameChat.setText(chatName);

            Message message = chats.get(i).getLast_message();

            String messageText = "";
            if(message != null){
                messageText = message.getMessage();
                String userName = "";
                if(chats.get(i).isIs_group()){
                    if(message.getId_user() == User_Singleton.getInstance().getId()){
                        userName = "Tú: ";
                    }else{
                        for (User u : CommunityActivity.listUsers) {
                            if (Integer.parseInt(u.getId()) ==  message.getId_user()){
                                userName = u.getName() + ": ";
                                continue;
                            }
                        }
                    }
                }
                messageText = userName + messageText;

                txtLastMessage.setText(messageText);
                if (!message.isSeen() && (message.getId_user() != User_Singleton.getInstance().getId())) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
                }
            }else{
                txtLastMessage.setText("");
            }

            return view;
        }
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetChats extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        public ExecuteGetChats() {
        }

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
            isOk = api.get_delete_base(keys, values, 35, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarChats(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener los chats";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
