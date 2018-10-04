package elcarmen.project.community.Business;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        ExecuteGetChats executeGetChats = new ExecuteGetChats();
        executeGetChats.execute();

        return v;
    }

    private void cargarChats(JSONObject jsonResult) {

        try {
            chats.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            LoginAcivity.actualizarAuth_Token(token, getActivity());

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
                    ArrayList<User> users = CommunityActivity.listUsers;

                    for(User u: users){
                        if(Integer.parseInt(u.getId()) == id_user){
                            userName = u.getName();
                        }
                    }
                }


                Chat chat = new Chat(jsonChat.getInt("id"), jsonChat.getInt("id_community"),
                        CommunityActivity.nameCommunity, is_group,
                        id_user, userName);

                Message last_msg = null;
                if(jsonLastMessage != null){
                    last_msg = new Message(jsonLastMessage.getInt("id"),
                            jsonLastMessage.getInt("id_chat"), jsonLastMessage.getInt("id_user"),
                            jsonLastMessage.getString("message"), jsonLastMessage.getBoolean("seen"),
                            jsonLastMessage.getString("created_at"));
                }

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
                for (User u : CommunityActivity.listUsers) {
                    if (Integer.parseInt(u.getId()) == chats.get(i).getId_user()){
                        link_photo = u.getUrl_photo_rounded();
                        continue;
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

            if(message != null){
                txtLastMessage.setText(message.getMessage());
                if (!message.isSeen()) {
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
