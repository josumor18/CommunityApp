package elcarmen.project.community.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class NewsMoreActivity extends AppCompatActivity {


    User_Singleton user;
    ListView lvComments;
    ListView lvNews;
    String date = " ";

    ImageView imgNew;
    TextView txtApproveNew;
    TextView txtTitleNew;
    TextView txtDescriptionNew;
    TextView txtDateNew;
    TextView txtAddCommentUser;
    Button btnDeleteNew;
    Button btnAddComment;
    EditText edtTextComment;

    int idActual;
    String titleNews;
    Bitmap photoNews = null;
    String descriptionNews;
    String dateNews;
    Boolean isApprovedNews;
    String userComment;

    boolean isAdmin;

    //private ArrayList<Comment> listComments = new ArrayList<Comment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_more);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = User_Singleton.getInstance();

        idActual = getIntent().getIntExtra("idActual",0);
        titleNews = getIntent().getStringExtra("Title");
        descriptionNews = getIntent().getStringExtra("Description");
        dateNews = getIntent().getStringExtra("DateN");
        isApprovedNews = getIntent().getBooleanExtra("isApproved",false);
        //byte[] byteArray = getIntent().getByteArrayExtra("Photo");
        //photoNews = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        for(int i =0;i<CommunityFeedFragment.listNews.size();i++){
            if(CommunityFeedFragment.listNews.get(i).getId() == idActual)
                photoNews = CommunityFeedFragment.listNews.get(i).getPhoto();
        }

        userComment = user.getName() + " dijo:";

        lvComments = findViewById(R.id.lvNews);
        lvNews = findViewById(R.id.lvNews);

        txtApproveNew = findViewById(R.id.txt_aprobar);
        imgNew = findViewById(R.id.img_New);
        txtTitleNew = findViewById(R.id.txtTitleNew);
        txtDateNew = findViewById(R.id.txtFechaHora);
        txtDescriptionNew = findViewById(R.id.txtDescription);
        txtAddCommentUser = findViewById(R.id.txtAddComment);
        btnDeleteNew = findViewById(R.id.btn_EliminarNew);
        btnAddComment = findViewById(R.id.btn_AddComment);
        edtTextComment = findViewById(R.id.edtAddComment);


        txtAddCommentUser.setText(userComment);
        txtTitleNew.setText(titleNews);
        txtDateNew.setText(dateNews);
        txtDescriptionNew.setText(descriptionNews);

        if(photoNews != null)
            imgNew.setImageBitmap(photoNews);
        else
            imgNew.setVisibility(View.GONE);




        if(isApprovedNews)
            txtApproveNew.setVisibility(View.GONE);


        txtApproveNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteApproveNews executeApproveNews = new ExecuteApproveNews(idActual);
                executeApproveNews.execute();

            }
        });

        btnDeleteNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteDeleteNews executeDeleteNews = new ExecuteDeleteNews(idActual);
                executeDeleteNews.execute();
            }
        });


        isAdmin = user.isAdmin(CommunityActivity.idCommunity);

       if(!isAdmin)
            btnDeleteNew.setVisibility(View.GONE);

       /* if(isAdmin) {
            ExecuteGetNews executeGetNews = new ExecuteGetNews();
            executeGetNews.execute();
        }
        else{
            ExecuteGetNews executeGetNews = new ExecuteGetNews(true);
            executeGetNews.execute();
        }*/

    }

    public void addComment(View view){
        if (!TextUtils.isEmpty(edtTextComment.getText())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date1 = new Date();

            date = dateFormat.format(date1);

            ExecuteAddComment executeAddComment = new ExecuteAddComment(user.getId(),
                    idActual,edtTextComment.getText().toString(),date);

            executeAddComment.execute();


        }
        else
            edtTextComment.setError("Campo Requerido");


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
                //callFeedActivity();
                Toast.makeText(getApplicationContext(), "Eliminada", Toast.LENGTH_SHORT).show();
                finish();

            }else{
                String mensaje = "Error al eliminar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
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
                txtApproveNew.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Aprobada", Toast.LENGTH_SHORT).show();
                //ExecuteGetNews executeGetNews = new ExecuteGetNews();
                //executeGetNews.execute();
            }else{
                String mensaje = "Error al aprobar";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ExecuteAddComment extends AsyncTask<String,Void,String> {
        private int idUser;
        private int idNews;
        private String description;
        private String date;
        private boolean isAdded = false;

        public ExecuteAddComment(int idUser,int idNews, String description, String date) {
            this.idNews = idNews;
            this.idUser = idUser;
            this.description = description;
            this.date = date;
        }



        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            String[] keys = {"id_news","id_user", "description", "date"};
            String[] values = {Integer.toString(idNews),Integer.toString(idUser),description,date};
            isAdded = api.post_put_base(keys,values,21,"POST",0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isAdded) {

                Toast.makeText(NewsMoreActivity.this, "Comentario exitosa", Toast.LENGTH_SHORT).show();

                //ExecuteGetComments


            } else
                Toast.makeText(NewsMoreActivity.this, "Comentario fallido", Toast.LENGTH_SHORT).show();

        }
    }
}
