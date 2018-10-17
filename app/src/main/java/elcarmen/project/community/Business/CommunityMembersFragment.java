package elcarmen.project.community.Business;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityMembersFragment extends Fragment {

    RelativeLayout rlVerSolicitudes;
    TextView txtCantSolicitudes;
    ListView lvUsers;

    private ArrayList<User> listMembers = new ArrayList<User>();

    User_Singleton user;
    boolean isAdmin;

    public CommunityMembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community_members, container, false);

        user = User_Singleton.getInstance();

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);

        for(User u:CommunityActivity.listUsers){
            if(!u.isPrivateProfile())
                listMembers.add(u);
        }

        lvUsers = v.findViewById(R.id.lvUsers);
        rlVerSolicitudes = v.findViewById(R.id.rlVerSolicitudes);
        txtCantSolicitudes = v.findViewById(R.id.txtCantSolicitudes);
        txtCantSolicitudes.setVisibility(View.INVISIBLE);

        rlVerSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCantSolicitudes.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getActivity(), RequestsActivity.class);
                startActivity(intent);
            }
        });

        if(!User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity)){
            rlVerSolicitudes.setVisibility(View.GONE);
        }
      
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ProfileUserActivity.class);
                User selected = listMembers.get(position);

                intent.putExtra("idUser",selected.getId());
                intent.putExtra("photo", selected.getUrl_photo());
                intent.putExtra("name", selected.getName());
                intent.putExtra("cel", selected.getCel());
                intent.putExtra("tel", selected.getTel());
                intent.putExtra("address", selected.getAddress());
                startActivity(intent);
            }
        });

        //lvUsers.setAdapter(new UserAdapter());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            if(User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity)){
                ExecuteGetRequestCount executeGetRequestCount = new ExecuteGetRequestCount();
                executeGetRequestCount.execute();
            }else{
                if(rlVerSolicitudes != null){
                    rlVerSolicitudes.setVisibility(View.GONE);
                }
            }

            if(lvUsers != null){
                lvUsers.setAdapter(new UserAdapter());
            }
        }
    }

    public class UserAdapter extends BaseAdapter {

        public UserAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return listMembers.size();
        }

        @Override
        public Object getItem(int i) {
            return listMembers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.profiles_list_item, null);
            }


            TextView txtUserProfile = view.findViewById(R.id.txtUserProfile);

            ImageView imgUserProfile = view.findViewById(R.id.img_userProfile);

            final String idUser = listMembers.get(i).getId();

            String userName = listMembers.get(i).getName();

            Bitmap photoUser = listMembers.get(i).getPhoto_rounded();

            Boolean isPrivate = listMembers.get(i).isPrivateProfile();




            /*btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAdmin || idUser == user.getId()){
                        new AlertDialog.Builder(NewsMoreActivity.this)
                                .setIcon(R.drawable.ic_delete_forever_black_24dp)
                                .setTitle("Está seguro?")
                                .setMessage("Desea eliminar comentario?")
                                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ExecuteDeleteComments executeDeleteComment = new ExecuteDeleteComments(idComment);
                                        executeDeleteComment.execute();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();

                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), ReportsActivity.class);
                        intent.putExtra("idComment", idComment);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("username",usernameComment);
                        startActivity(intent);
                    }

                }
            });*/



            txtUserProfile.setText(userName);



            if(photoUser == null)
                BitmapFactory.decodeResource( getContext().getResources(),
                        R.drawable.user_rounded_photo);

            else
                imgUserProfile.setImageBitmap(photoUser);


            return view;
        }
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetRequestCount extends AsyncTask<String, Void, String> {
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
            isOk = api.get_delete_base(keys, values, 12, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                try {
                    JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                    int cantidad = response.getInt("cantidad");
                    if(cantidad > 0){
                        txtCantSolicitudes.setText(Integer.toString(cantidad));
                        txtCantSolicitudes.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
