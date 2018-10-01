package elcarmen.project.community.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompatSideChannelService;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {


    ListView lvNotifications;
    RelativeLayout rlNotifications;


    private ArrayList<Notification> notifications = new ArrayList<Notification>();

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);



        rlNotifications = v.findViewById(R.id.rlNitifications);
        lvNotifications = v.findViewById(R.id.lvNotifications);
        lvNotifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*  REDIRECCIONAR A DETALLES SEGUN TIPO DE NOTIFICACION
                Intent intent = new Intent(getActivity(), CommunityActivity.class);
                int idCommunity_enviar = communities.get(position).getId();
                String nameCommunity_enviar = communities.get(position).getName();
                intent.putExtra("idCommunity",idCommunity_enviar);
                intent.putExtra("nameCommunity", nameCommunity_enviar);
                startActivity(intent);
                */
            }
        });


        NotificationsFragment.ExecuteGetNotifications executeGetNotifications = new NotificationsFragment.ExecuteGetNotifications();
        executeGetNotifications.execute();

        return v;
    }


    private void loadNotifications(JSONObject jsonResult) {

        try {
            notifications.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            LoginAcivity.actualizarAuth_Token(token, getActivity());

            /*
            JSONArray jsonCommunitiesList = jsonResult.getJSONArray("communities");
            for (int i = 0; i < jsonCommunitiesList.length(); i++) {
                JSONObject jsonCommunity = (JSONObject) jsonCommunitiesList.get(i);

                JSONArray jsonRules = jsonCommunity.getJSONArray("rules");
                ArrayList<String> rules = new ArrayList<String>();
                for(int ind = 0; ind < jsonRules.length(); ind++){
                    rules.add(jsonRules.getString(ind));
                }

                JSONArray jsonSubs = jsonCommunity.getJSONArray("sub_communities");
                ArrayList<Integer> subs = new ArrayList<Integer>();
                for(int ind = 0; ind < jsonSubs.length(); ind++){
                    subs.add(jsonSubs.getInt(ind));
                }

                Community community = new Community(jsonCommunity.getInt("id"), jsonCommunity.getString("name"), jsonCommunity.getString("description"), rules, jsonCommunity.getBoolean("isSubcommunity"), jsonCommunity.getString("photo"), jsonCommunity.getString("photo_thumbnail"), subs);

                communities.add(community);
            }
            */
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvNotifications.setAdapter(new NotificationsFragment.NotificationsAdapter());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class NotificationsAdapter extends BaseAdapter {

        public NotificationsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Object getItem(int i) {
            return notifications.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.notifications_list_item, null);
            }

            ImageView imgNotif = view.findViewById(R.id.imgNotif);
            TextView txtDescriptionNotif = view.findViewById(R.id.txtDescriptionNotif);
            TextView txtTitleNotif = view.findViewById(R.id.txtTitleNotif);
            Button btnMoreNotif = view.findViewById(R.id.btnMoreNotif);
            Button btnDeleteNotif = view.findViewById(R.id.btnDeleteNotif);


            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(notifications.get(i).getPhoto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
                        R.drawable.ic_report_black_24dp);
            }
            imgNotif.setImageBitmap(userImage);

            String descriptionNotif = "";
            if(notifications.get(i).isNews()){
                descriptionNotif = "Se ha publicado una nueva difusión";
            }
            else if(notifications.get(i).isEvents()){
                descriptionNotif = "Se ha publicado un nuevo evento";
            }
            else if(notifications.get(i).isReports()){
                descriptionNotif = "Se ha realizado un nuevo reporte";
            }


            txtDescriptionNotif.setText(descriptionNotif);
            txtTitleNotif.setText(notifications.get(i).getTitleContent());

            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetNotifications extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            String[] keys = {"idUser", "auth_token"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token()};
            isOk = api.get_delete_base(keys, values, 32, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                loadNotifications(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las notificaciones";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
