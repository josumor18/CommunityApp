package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();


    User_Singleton user;
    boolean isAdmin;
    private ArrayList<News> listNews = new ArrayList<News>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        lvFavorites = findViewById(R.id.lvFavorites);
        user = User_Singleton.getInstance();

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if(isAdmin) {
            FavoritesActivity.ExecuteGetNews executeGetNews = new FavoritesActivity.ExecuteGetNews();
            executeGetNews.execute();
        }
        else{
            FavoritesActivity.ExecuteGetNews executeGetNews = new FavoritesActivity.ExecuteGetNews(true);
            executeGetNews.execute();
        }

    }



    public void pruebaClicked(View view){
        Toast.makeText(FavoritesActivity.this, "hola", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
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

            final int idActual = listNews.get(i).getId();


            //Si ya esta aprobada
            if(listNews.get(i).isApproved())
                txtAprobar.setVisibility(View.GONE);


            txtTitle.setText(listNews.get(i).getTitle());


            if(listNews.get(i).getPhoto() == null)
                imgImageNew.setVisibility(View.GONE);

            imgImageNew.setImageBitmap(listNews.get(i).getPhoto());

            txtDate.setText(listNews.get(i).getDate().toString());

            return view;
        }
    }




    private void cargarNews(JSONObject jsonResult) {

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
                        jsonNew.getString("photo"),newImage,jsonNew.getBoolean("approved"));

                listNews.add(newObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvFavorites.setAdapter(new FavoritesActivity.NewsAdapter());


    }



    public class ExecuteGetNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        boolean isApproved = false;

        ExecuteGetNews(){

        }

        ExecuteGetNews(boolean status){
            this.isApproved = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();

            if(isAdmin) {
                String[] keys = {"id"};
                String[] values = {Integer.toString(CommunityActivity.idCommunity)};
                isOk = api.get_delete_base(keys, values, 10, "GET", 1);
            }
            else{
                String[] keys = {"id","isApproved"};
                String[] values = {Integer.toString(CommunityActivity.idCommunity),Boolean.toString(isApproved)};
                isOk = api.get_delete_base(keys, values, 11, "GET", 1);
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarNews(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las difusiones";

                Toast.makeText(FavoritesActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

