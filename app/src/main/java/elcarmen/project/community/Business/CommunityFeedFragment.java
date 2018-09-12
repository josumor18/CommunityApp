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
public class CommunityFeedFragment extends Fragment {

    ListView lvNews;

    FloatingActionButton ftbtnCreateNews;

    private ArrayList<News> listNews = new ArrayList<News>();

    public CommunityFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_community_feed, container, false);
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

        ExecuteGetNews executeGetNews = new ExecuteGetNews();
        executeGetNews.execute();


        return v;
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
            TextView txtContent = view.findViewById(R.id.txtContent);
            ImageView imgImageNew = view.findViewById(R.id.img_New);
            TextView txtDate = view.findViewById(R.id.txtFechaHora);



            txtTitle.setText(listNews.get(i).getTitle());
            txtContent.setText(listNews.get(i).getDescription());

            /*HttpGetBitmap request2 = new HttpGetBitmap();
            Bitmap postImage = null;
            try {
                postImage = request2.execute(posts.get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
            imgImageNew.setImageBitmap(listNews.get(i).getPhoto());

            txtDate.setText(listNews.get(i).getDate().toString());

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetNews extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            String[] keys = {"id"};
            String[] values = {Integer.toString(CommunityActivity.idCommunity)};
            //isOk = api.get_delete_base(keys, values, 10, "GET",1);

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

}
