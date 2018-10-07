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

import org.cloudinary.json.JSONString;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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


    public static ArrayList<Notification> notifications = new ArrayList<Notification>();

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

    @Override
    public void onResume() {
        super.onResume();
        lvNotifications.setAdapter(new NotificationsAdapter());
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

            if(!notifications.get(i).isSeen()) {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            }
            else {
                view.setBackgroundColor(getResources().getColor(R.color.colorGrayTab));
            }

            ImageView imgNotif = view.findViewById(R.id.imgNotif);
            TextView txtDescriptionNotif = view.findViewById(R.id.txtDescriptionNotif);
            TextView txtTitleNotif = view.findViewById(R.id.txtTitleNotif);
            Button btnDeleteNotif = view.findViewById(R.id.btnDeleteNotif);


            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap notifImage = null;
            String photo = notifications.get(i).getPhoto();
            try {
                notifImage = request.execute(photo).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(notifImage == null){
                notifImage = BitmapFactory.decodeResource( getActivity().getApplicationContext()
                                                .getResources(),R.drawable.ic_report_black_24dp);
            }
            imgNotif.setImageBitmap(notifImage);

            String descriptionNotif = "";
            if(notifications.get(i).isNews()){
                descriptionNotif = "Se ha publicado una nueva difusión";
            }
            else if(notifications.get(i).isEvents()){
                descriptionNotif = "Se ha publicado un nuevo evento";
            }
            else if(notifications.get(i).isReports()){
                descriptionNotif = "Se ha realizado un nuevo reporte en:";
            }

            btnDeleteNotif.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idNotification = notifications.get(i).getId();
                    NotificationsFragment.ExecuteDeleteNotification executeDeleteNotification = new
                            NotificationsFragment.ExecuteDeleteNotification(idNotification);
                    executeDeleteNotification.execute();
                }
            });


            final View finalView = view;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!notifications.get(i).isSeen()) {
                        finalView.setBackgroundColor(getResources().getColor(R.color.colorGrayTab));
                        NotificationsFragment.ExecutePUTSeenNotif executePUTSeenNotif = new
                                NotificationsFragment.ExecutePUTSeenNotif(notifications.get(i).getId());
                        executePUTSeenNotif.execute();
                        notifications.get(i).setSeen(true);
                    }


                    if(notifications.get(i).isNews()){
                        int idSearchNews = notifications.get(i).getIdContent();
                        NotificationsFragment.ExecuteGetListUsersCommunity_by_idNews
                                executeGetListUsersCommunity_by_idNews = new NotificationsFragment.
                                ExecuteGetListUsersCommunity_by_idNews(idSearchNews);
                        executeGetListUsersCommunity_by_idNews.execute();
                    }

                    else if(notifications.get(i).isEvents()){
                        int idSearchEvent = notifications.get(i).getIdContent();
                        NotificationsFragment.ExecuteGetDataEvent executeGetDataEvent = new
                                NotificationsFragment.ExecuteGetDataEvent(idSearchEvent);
                        executeGetDataEvent.execute();

                    }

                    else if(notifications.get(i).isReports()){
                        int idSearchReport = notifications.get(i).getIdContent();
                        NotificationsFragment.ExecuteGetReportAndComment executeGetReportAndComment=
                                new NotificationsFragment.ExecuteGetReportAndComment(idSearchReport);
                        executeGetReportAndComment.execute();
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
                    intent.putExtra("idCommunity", jsonNews.getInt("idCommunity"));
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
    public class ExecuteGetDataEvent extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idEvent;

        public ExecuteGetDataEvent(int idEvent) {
            this.idEvent = idEvent;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idEvent", "auth_token"};
            String[] values = {Integer.toString(User_Singleton.getInstance().getId()),
                    Integer.toString(idEvent),  User_Singleton.getInstance().getAuth_token()};
            isOk = api.get_delete_base(keys, values, 38, "GET", 1);

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

                JSONObject  jsonEvent = null;
                String NameComm =  "";
                try {
                    jsonEvent = (JSONObject) response.getJSONObject("event");
                    NameComm = response.getString("nameCommunity");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(),EventInfoActivity.class);
                intent.putExtra("community_name", NameComm);
                try {
                    Event event = new Event(jsonEvent.getInt("id"),
                                            jsonEvent.getInt("id_community"),
                                            jsonEvent.getString("title"),
                                            jsonEvent.getString("description"),
                                            jsonEvent.getString("dateEvent"),
                                            jsonEvent.getString("start"),
                                            jsonEvent.getString("end"),
                                            jsonEvent.getString("photo"),
                                            jsonEvent.getBoolean("approved"));

                    intent.putExtra("photo", event.getPhoto());
                    intent.putExtra("title", event.getTitle());
                    intent.putExtra("description", event.getDescription());
                    intent.putExtra("date", event.getDate());
                    boolean terminado = false;
                    Date date = new Date();
                    if (date.after(event.getDateEventEnd())){
                        terminado = true;
                    }
                    intent.putExtra("terminado", terminado);
                    intent.putExtra("hours", event.getHours());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String mensaje = "Cargando contenido del evento";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }else{
                String mensaje = "Error al obtener los datos del evento";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=============================================================================================
    public class ExecuteGetReportAndComment extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idReport;

        public ExecuteGetReportAndComment(int idReport) {
            this.idReport = idReport;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idReport", "auth_token"};
            String[] values = {Integer.toString(User_Singleton.getInstance().getId()),
                    Integer.toString(idReport),  User_Singleton.getInstance().getAuth_token()};
            isOk = api.get_delete_base(keys, values, 39, "GET", 1);

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                try {
                    JSONObject jsonReport  = (JSONObject) response.getJSONObject("report");
                    JSONObject jsonComment = (JSONObject) response.getJSONObject("comment");
                    String titleNews     = response.getString("titleNews");
                    String nameCommunity = response.getString("nameCommunity");
                    String nameUser      = response.getString("nameUser");
                    final int idComment = jsonComment.getInt("id");
                    final int idReport  = jsonReport.getInt("id");
                    Comment comment = new Comment(idComment,
                                                  jsonComment.getInt("id_news"),
                                                  jsonComment.getInt("id_user"),
                                                  jsonComment.getString("description"));

                    String txtAlert = "Se ha registrado un reporte al usuario '" + nameUser +
                            "´en la difusión '" + titleNews + "' " + " \n\nComentario:\n " +
                            comment.getDescription() + "\n\nMotivo del reporte: " +
                            jsonReport.getString("reason");

                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_report_black_24dp)
                            .setTitle("Reporte en comunidad: " + nameCommunity)
                            .setMessage(txtAlert)
                            .setPositiveButton("Eliminar comentario", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NotificationsFragment.ExecuteDeleteComment executeDeleteComment
                                         = new NotificationsFragment.ExecuteDeleteComment
                                                                            (idComment, idReport);
                                    executeDeleteComment.execute();
                                }
                            })
                            .setNegativeButton("Ignorar", null)
                            .show();

                    //set user auth_token
                    try {
                        String token = response.getString("auth_token");
                        User_Singleton.getInstance().setAuth_token(token);
                        LoginAcivity.actualizarAuth_Token(token, getActivity());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    String mensaje = "Error al obtener los datos del reporte";
                    Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                String mensaje = "Cargando contenido del reporte";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al obtener los datos del reporte";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=============================================================================================
    public class ExecuteDeleteComment extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idComment;
        int idReport;

        public ExecuteDeleteComment(int idComment, int idReport) {
            this.idComment = idComment;
            this.idReport = idReport;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            String[] keys = {"id"};
            String[] values = {Integer.toString(idComment)};
            isOk = api.get_delete_base(keys, values, 23, "DELETE",1);


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                JSONObject response = API_Access.getInstance().getJsonObjectResponse();
                int indexDel = 0;
                ExecuteGetNotifications executeGetNotifications = new
                        ExecuteGetNotifications();
                executeGetNotifications.execute();;

                Toast.makeText(getActivity(), "Comentario eliminado", Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al eliminar comentario";
                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //=============================================================================================
    public class ExecutePUTSeenNotif extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idNotification;


        public ExecutePUTSeenNotif(int idNotification) {
            this.idNotification = idNotification;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idNotification", "auth_token"};
            String[] values = {Integer.toString(User_Singleton.getInstance().getId()),
                    Integer.toString(idNotification),  User_Singleton.getInstance().getAuth_token()};
            isOk = api.post_put_base(keys, values, 31, "PUT", 1);

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

            }else{
                String mensaje = "Error al colocar en visto la notificacion";
                Toast.makeText( getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


    //=============================================================================================
    public class ExecuteDeleteNotification extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idNotification;

        public ExecuteDeleteNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            String[] keys = {"idNotification"};
            String[] values = {Integer.toString(idNotification)};
            isOk = api.get_delete_base(keys, values, 33, "DELETE",1);


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                int indexDelete = 0;
                for(Notification n : notifications){
                    if(n.getId() == idNotification){
                        notifications.remove(indexDelete);
                        break;
                    }
                    indexDelete++;
                }

                lvNotifications.setAdapter(new NotificationsAdapter());
                Toast.makeText(getActivity(), "Notificacion eliminada",Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al eliminar notificacion";
                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
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
