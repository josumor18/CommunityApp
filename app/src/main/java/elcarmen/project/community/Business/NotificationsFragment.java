package elcarmen.project.community.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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



        rlNotifications = v.findViewById(R.id.rlNotifications);
        lvNotifications = v.findViewById(R.id.lvNotifications);



        ExecuteGetNotifications executeGetNotifications = new
                                                    ExecuteGetNotifications();
        executeGetNotifications.execute();

        return v;
    }


    private void loadNotifications(JSONObject jsonResult) {

        try {
            notifications.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            LoginAcivity.actualizarAuth_Token(token, getActivity());


            JSONArray jsonNotificationsList = jsonResult.getJSONArray("notifications");
            for (int i = 0; i < jsonNotificationsList.length(); i++) {
                JSONObject jsonNotification = (JSONObject) jsonNotificationsList.get(i);

                Notification notification = new Notification(jsonNotification.getInt("id"),
                        jsonNotification.getInt("idUser"),
                        jsonNotification.getInt("idContent"),
                        jsonNotification.getBoolean("isNews"),
                        jsonNotification.getBoolean("isReports"),
                        jsonNotification.getBoolean("isEvents"),
                        jsonNotification.getString("titleContent"),
                        jsonNotification.getBoolean("seen"),
                        jsonNotification.getString("photo"));

                notifications.add(notification);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvNotifications.setAdapter(new NotificationsAdapter());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.notifications_list_item, null);
            }
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

            ImageView imgNotif = view.findViewById(R.id.imgNotif);
            TextView txtDescriptionNotif = view.findViewById(R.id.txtDescriptionNotif);
            TextView txtTitleNotif = view.findViewById(R.id.txtTitleNotif);
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
                userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext()
                                                .getResources(),R.drawable.ic_report_black_24dp);
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

            btnDeleteNotif.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "HOLA222", Toast.LENGTH_SHORT).show();
                }
            });


            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(notifications.get(i).isNews()){
                        int idSearchNews = notifications.get(i).getIdContent();
                        NotificationsFragment.ExecuteGetListUsersCommunity_by_idNews
                                executeGetListUsersCommunity_by_idNews = new NotificationsFragment.
                                ExecuteGetListUsersCommunity_by_idNews(idSearchNews);
                        executeGetListUsersCommunity_by_idNews.execute();
                    }

                    else if(notifications.get(i).isEvents()){
                        Toast.makeText(getActivity(), "aun no cargo eventos", Toast.LENGTH_SHORT).show();
                    }

                    else if(notifications.get(i).isReports()){
                        Toast.makeText(getActivity(), "aun no cargo reportes", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            txtDescriptionNotif.setText(descriptionNotif);
            txtTitleNotif.setText(notifications.get(i).getTitleContent());

            return view;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
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
            isOk = api.get_delete_base(keys, values, 32,"GET",1);

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


    //=============================================================================================
    public class ExecuteGetListUsersCommunity_by_idNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idNews;

        public ExecuteGetListUsersCommunity_by_idNews(int idNews) {
            this.idNews = idNews;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idNews", "auth_token"};
            String[] values = {Integer.toString(User_Singleton.getInstance().getId()),
                           Integer.toString(idNews),  User_Singleton.getInstance().getAuth_token()};
            isOk = api.get_delete_base(keys, values, 29, "GET", 1);

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                //set user auth_token
                try {
                    String token = response.getString("auth_token");
                    User_Singleton.getInstance().setAuth_token(token);
                    LoginAcivity.actualizarAuth_Token(token, getActivity());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadUsersNews(API_Access.getInstance().getJsonObjectResponse());
                NotificationsFragment.ExecuteGetNewsbyId executeGetNewsbyId = new
                        NotificationsFragment.ExecuteGetNewsbyId(idNews);
                executeGetNewsbyId.execute();
                String mensaje = "Cargando contenido de la difusión";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error al obtener los datos de la difusión";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUsersNews(JSONObject jsonResult) {

        try {
            CommunityActivity.listUsers.clear();

            JSONArray jsonUsersList = jsonResult.getJSONArray("usuarios");  //Importante
            for (int i = 0; i < jsonUsersList.length(); i++) {
                JSONObject jsonUser = (JSONObject) jsonUsersList.get(i);

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap newImage = null;
                try {
                    newImage = request.execute(jsonUser.getString("photo")).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                int id = jsonUser.getInt("id");
                String name = jsonUser.getString("name");
                String email = jsonUser.getString("email");
                String tel = jsonUser.getString("tel");
                String cel = jsonUser.getString("cel");
                String address = jsonUser.getString("address");
                String url_photo = jsonUser.getString("photo");
                String url_photo_rounded = jsonUser.getString("photo_thumbnail");
                Bitmap photo = convertirBitmap(url_photo);
                Bitmap photo_rounded = convertirBitmap(url_photo_rounded);
                boolean privateProfile = jsonUser.getBoolean("isPrivate");

                if(photo == null){
                    photo = BitmapFactory.decodeResource( getActivity().getResources(),
                            R.drawable.user_box_photo);
                }
                if(photo_rounded == null){
                    photo_rounded = BitmapFactory.decodeResource( getActivity().getResources(),
                            R.drawable.user_rounded_photo);
                }

                User userObject = new User(Integer.toString(id),name,email,tel,cel,address,
                                    url_photo,url_photo_rounded,photo,photo_rounded,privateProfile);

                CommunityActivity.listUsers.add(userObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //=============================================================================================
    public class ExecuteGetNewsbyId extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idNews;

        public ExecuteGetNewsbyId(int idNews) {
            this.idNews = idNews;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idNews", "auth_token"};
            String[] values = {Integer.toString(User_Singleton.getInstance().getId()),
                    Integer.toString(idNews),  User_Singleton.getInstance().getAuth_token()};
            isOk = api.get_delete_base(keys, values, 34, "GET", 1);

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                //set user auth_token
                try {
                    String token = response.getString("auth_token");
                    User_Singleton.getInstance().setAuth_token(token);
                    LoginAcivity.actualizarAuth_Token(token, getActivity());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject  jsonNews = null;
                try {
                    jsonNews = (JSONObject)  API_Access.getInstance().getJsonObjectResponse()
                                                                            .getJSONObject("news");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(),NewsMoreActivity.class);

                try {
                    intent.putExtra("idActual", jsonNews.getInt("id"));
                    intent.putExtra("Title", jsonNews.getString("title"));
                    intent.putExtra("DateN", jsonNews.getString("date"));
                    intent.putExtra("Description", jsonNews.getString("description"));
                    intent.putExtra("isApproved", jsonNews.getBoolean("approved"));
                    NewsMoreActivity.fromFavorites = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                String mensaje = "Cargando contenido de la difusión";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error al obtener los datos de la difusión";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


    //=============================================================================================

    private Bitmap convertirBitmap(String url){
        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap newImage = null;
        try {
            newImage = request.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return newImage;
    }


}
