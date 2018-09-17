package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class NewsMoreActivity extends AppCompatActivity {


    User_Singleton user;
    ListView lvComments;
    ListView lvNews;

    boolean isAdmin;

    //private ArrayList<Comment> listComments = new ArrayList<Comment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_more);


        user = User_Singleton.getInstance();

        lvComments = findViewById(R.id.lvNews);
        lvNews = findViewById(R.id.lvNews);

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
       /* if(isAdmin) {
            ExecuteGetNews executeGetNews = new ExecuteGetNews();
            executeGetNews.execute();
        }
        else{
            ExecuteGetNews executeGetNews = new ExecuteGetNews(true);
            executeGetNews.execute();
        }*/

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
                //ExecuteGetNews executeGetNews = new ExecuteGetNews();
                //executeGetNews.execute();
            }else{
                String mensaje = "Error al aprobar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
