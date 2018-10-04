package elcarmen.project.community.Business;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class FavoritesActivity  extends AppCompatActivity {


    ListView lvFavorites;
    TextView avError;

    User_Singleton user;
    public static ArrayList<News> listNews = new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_favorites);
        lvFavorites = findViewById(R.id.lvFavorites);
        avError = findViewById(R.id.tvError);
        user = User_Singleton.getInstance();
        avError.setVisibility(View.INVISIBLE);
        FavoritesActivity.ExecuteGetFavNews executeFavGetNews = new FavoritesActivity.ExecuteGetFavNews();
        executeFavGetNews.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lvFavorites.setAdapter(new FavoritesActivity.NewsAdapter());
    }



    public class NewsAdapter extends BaseAdapter {

        public NewsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return listNews.size();
        }

        @Override
        public Object getItem(int i) {
            return listNews.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.news_list_item, null);
            }


            TextView txtTitle = view.findViewById(R.id.txtTitleNew);
            ImageView imgImageNew = view.findViewById(R.id.img_New);
            TextView txtDate = view.findViewById(R.id.txtFechaHora);
            TextView txtAprobar = view.findViewById(R.id.txt_aprobar);
            Button btnNewsMore = view.findViewById(R.id.btn_NewMore);
            Button btnEliminarNew = view.findViewById(R.id.btn_EliminarNew);
            Button btn_Favorite = view.findViewById(R.id.btn_Favorite);

            btn_Favorite.setBackground(getResources().getDrawable(R.drawable.ic_star_black_24dp));
            txtAprobar.setVisibility(View.INVISIBLE);
            btnEliminarNew.setVisibility(View.INVISIBLE);



            final int actualNewsID = listNews.get(i).getId();

            final String titleN = listNews.get(i).getTitle();

            final String dateN = listNews.get(i).getDate().toString();

            final String description = listNews.get(i).getDescription();


            final boolean isApprovedNews = listNews.get(i).isApproved();

            txtTitle.setText(listNews.get(i).getTitle());


            if(listNews.get(i).getPhoto() == null)
                imgImageNew.setVisibility(View.GONE);

            imgImageNew.setImageBitmap(listNews.get(i).getPhoto());

            txtDate.setText(listNews.get(i).getDate().toString());


            btn_Favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecuteDelFavorites executeDelFavorites = new ExecuteDelFavorites(actualNewsID);
                    executeDelFavorites.execute();

                }
            });


            final int position = i;
            btnNewsMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecuteGetListUsersCommunity_by_idNews executeGetListUsersCommunity_by_idNews =
                            new ExecuteGetListUsersCommunity_by_idNews(actualNewsID, listNews.get(position));
                    executeGetListUsersCommunity_by_idNews.execute();
                }
            });


            return view;
        }
    }






    //=============================================================================================
    public class ExecuteDelFavorites extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int favNewsID;

        public ExecuteDelFavorites(int favNewsID) {
            this.favNewsID = favNewsID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            String auth_token = user.getAuth_token();
            int idUser = user.getId();
            String[] keys = {"idNews", "idUser", "auth_token"};
            String[] values = {Integer.toString(favNewsID), Integer.toString(idUser), auth_token};
            isOk = api.get_delete_base(keys, values, 28, "DELETE",1);


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
                    user.setAuth_token(token);

                    LoginAcivity.actualizarAuth_Token(token, getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                    String mensaje = "Error al cargar nuevo token de autenticacion";
                    Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();;
                }
                Toast.makeText(FavoritesActivity.this, "Favorito eliminado", Toast.LENGTH_SHORT).show();
                FavoritesActivity.ExecuteGetFavNews executeFavGetNews = new FavoritesActivity.ExecuteGetFavNews();
                executeFavGetNews.execute();
            }else{
                String mensaje = "Error al eliminar difusión destacada";
                Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();;
            }
        }
    }

    //=============================================================================================
    public class ExecuteGetFavNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        ExecuteGetFavNews(){

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id", "auth_token"};
            String[] values = {Integer.toString(user.getId()),  user.getAuth_token()};
            isOk = api.get_delete_base(keys, values, 20, "GET", 1);

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
                    user.setAuth_token(token);

                    LoginAcivity.actualizarAuth_Token(token, getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadNews(response);
                if (listNews.isEmpty()){
                    avError.setVisibility(View.VISIBLE);
                }
                else{
                    avError.setVisibility(View.INVISIBLE);
                }

            }else{
                String mensaje = "Error al obtener las difusiones";
                Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadNews(JSONObject jsonResult) {

        try {
            listNews.clear();

            JSONArray jsonNewsList = jsonResult.getJSONArray("news");
            for (int i = 0; i < jsonNewsList.length(); i++) {
                JSONObject jsonNew = (JSONObject) jsonNewsList.get(i);

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap newImage = null;
                try {
                    newImage = request.execute(jsonNew.getString("photo")).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if(newImage == null){
                    newImage = BitmapFactory.decodeResource( FavoritesActivity.this.getApplicationContext().getResources(),
                            R.drawable.ic_add_a_photo_black_24dp);
                }


                News newObject = new News(jsonNew.getInt("id"),
                        jsonNew.getString("title"), jsonNew.getString("description"), jsonNew.getString("date"),
                        jsonNew.getString("photo"),newImage,jsonNew.getBoolean("approved"),
                        jsonNew.getInt("idCommunity"));

                listNews.add(newObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvFavorites.setAdapter(new FavoritesActivity.NewsAdapter());


    }

    //=============================================================================================
    public class ExecuteGetListUsersCommunity_by_idNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int idNews;
        News news;

        public ExecuteGetListUsersCommunity_by_idNews(int idNews, News news) {
            this.idNews = idNews;
            this.news = news;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"idUser", "idNews", "auth_token"};
            String[] values = {Integer.toString(user.getId()), Integer.toString(idNews),  user.getAuth_token()};
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
                    user.setAuth_token(token);
                    LoginAcivity.actualizarAuth_Token(token, getApplicationContext());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cargarUsersFavorite(API_Access.getInstance().getJsonObjectResponse());
                String mensaje = "Cargando contenido de la difusión destacada";
                Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(FavoritesActivity.this,NewsMoreActivity.class);
                intent.putExtra("idActual", news.getId());
                intent.putExtra("Title",news.getTitle());
                intent.putExtra("DateN",news.getDate());
                intent.putExtra("Description",news.getDescription());
                intent.putExtra("isApproved",news.isApproved());
                intent.putExtra("idCommunity",news.getIdCommunity());
                NewsMoreActivity.fromFavorites = true;
                startActivity(intent);
            }else{
                String mensaje = "Error al obtener las difusiones";
                Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarUsersFavorite(JSONObject jsonResult) {

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
                    photo = BitmapFactory.decodeResource( this.getApplicationContext().getResources(),
                            R.drawable.user_box_photo);
                }
                if(photo_rounded == null){
                    photo_rounded = BitmapFactory.decodeResource( this.getApplicationContext().getResources(),
                            R.drawable.user_rounded_photo);
                }

                User userObject = new User(Integer.toString(id),name,email,tel,cel,address,url_photo,url_photo_rounded,photo,photo_rounded,privateProfile);

                CommunityActivity.listUsers.add(userObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

