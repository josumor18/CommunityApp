package elcarmen.project.community.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityFeedFragment extends Fragment {

    ListView lvNews;

    FloatingActionButton ftbtnCreateNews;



    User_Singleton user;

    boolean isAdmin;

    public static ArrayList<News> listNews = new ArrayList<News>();

    public CommunityFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_community_feed, container, false);
        user = User_Singleton.getInstance();
        ftbtnCreateNews = v.findViewById(R.id.ftbtnCreateNews);
        ftbtnCreateNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                //intent.putExtra("isSubcommunity", false);
                startActivity(intent);
            }
        });




        lvNews = v.findViewById(R.id.lvNews);

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if(isAdmin) {
            ExecuteGetNews executeGetNews = new ExecuteGetNews();
            executeGetNews.execute();
        }
        else{
            ExecuteGetNews executeGetNews = new ExecuteGetNews(true);
            executeGetNews.execute();
        }


        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        CommunityActivity.isInFeedFragment = isVisibleToUser;
    }

    @Override
    public void onResume() {
        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if(isAdmin) {
            ExecuteGetNews executeGetNews = new ExecuteGetNews();
            executeGetNews.execute();
        }
        else{
            ExecuteGetNews executeGetNews = new ExecuteGetNews(true);
            executeGetNews.execute();
        }
        super.onResume();
    }

    private void cargarNews(JSONObject jsonResult) {

        try {
            listNews.clear();

            JSONArray jsonNewsList = jsonResult.getJSONArray("news");  //Importante
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
                    newImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
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

        lvNews.setAdapter(new NewsAdapter());


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
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
            //TextView txtContent = view.findViewById(R.id.txtContent);
            ImageView imgImageNew = view.findViewById(R.id.img_New);
            TextView txtDate = view.findViewById(R.id.txtFechaHora);
            TextView txtAprobar = view.findViewById(R.id.txt_aprobar);
            Button btnNewsMore = view.findViewById(R.id.btn_NewMore);
            Button btnDeleteNews = view.findViewById(R.id.btn_EliminarNew);

            final int idActual = listNews.get(i).getId();

            final String titleN = listNews.get(i).getTitle();

            final String dateN = listNews.get(i).getDate().toString();

            final String description = listNews.get(i).getDescription();

            final Bitmap photoNews = listNews.get(i).getPhoto();

            final boolean isApprovedNews = listNews.get(i).isApproved();

            txtAprobar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        ExecuteApproveNews executeApproveNews = new ExecuteApproveNews(idActual);
                        executeApproveNews.execute();

                }
            });


            btnNewsMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photoNews.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();*/

                    Intent intent = new Intent(getActivity(),NewsMoreActivity.class);
                    intent.putExtra("idActual", idActual);
                    intent.putExtra("Title",titleN);
                    //intent.putExtra("Photo", byteArray);
                    intent.putExtra("DateN",dateN);
                    intent.putExtra("Description",description);
                    intent.putExtra("isApproved",isApprovedNews);
                    startActivity(intent);

                }
            });

            btnDeleteNews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExecuteDeleteNews executeDeleteNews = new ExecuteDeleteNews(idActual);
                    executeDeleteNews.execute();
                }
            });

           if(!isAdmin)
                btnDeleteNews.setVisibility(View.GONE);

            //Si ya esta aprobada
            if(isApprovedNews)
                txtAprobar.setVisibility(View.GONE);




            txtTitle.setText(titleN);
            //txtContent.setText(listNews.get(i).getDescription());

            /*HttpGetBitmap request2 = new HttpGetBitmap();
            Bitmap postImage = null;
            try {
                postImage = request2.execute(posts.get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/

            if(listNews.get(i).getPhoto() == null)
                imgImageNew.setVisibility(View.GONE);

            imgImageNew.setImageBitmap(photoNews);

            txtDate.setText(dateN);

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
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

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
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

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteApproveNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int id;



        ExecuteApproveNews(int id){
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id"};
            String[] values = {Integer.toString(id)};
            isOk = api.post_put_base(keys, values, 15, "PUT", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetNews executeGetNews = new ExecuteGetNews();
                executeGetNews.execute();
            }else{
                String mensaje = "Error al aprobar";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteDeleteNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int id;



        ExecuteDeleteNews(int id){
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id"};
            String[] values = {Integer.toString(id)};
            isOk = api.get_delete_base(keys, values, 16, "DELETE", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetNews executeGetNews = new ExecuteGetNews();
                executeGetNews.execute();
                Toast.makeText(getActivity(), "Difusion eliminada", Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error al eliminar";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
