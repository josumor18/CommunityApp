package elcarmen.project.community.Business;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
    ImageView imgUserAddComment;
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

    private ArrayList<Comment> listComments = new ArrayList<Comment>();

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
        Bitmap photoUserComment = user.getPhoto_rounded();

        lvComments = findViewById(R.id.lv_comments);
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
        imgUserAddComment = findViewById(R.id.img_addComment);


        txtAddCommentUser.setText(userComment);
        txtTitleNew.setText(titleNews);
        txtDateNew.setText(dateNews);
        txtDescriptionNew.setText(descriptionNews);

        if(photoNews != null)
            imgNew.setImageBitmap(photoNews);
        else
            imgNew.setVisibility(View.GONE);

        if(photoUserComment == null){
            photoUserComment = BitmapFactory.decodeResource( this.getApplicationContext().getResources(),
                    R.drawable.user_rounded_photo);
        }
        else
            imgUserAddComment.setImageBitmap(photoUserComment);


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
                new AlertDialog.Builder(NewsMoreActivity.this)
                        .setIcon(R.drawable.ic_delete_forever_black_24dp)
                        .setTitle("Está seguro?")
                        .setMessage("Desea eliminar difusion?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ExecuteDeleteNews executeDeleteNews = new ExecuteDeleteNews(idActual);
                                executeDeleteNews.execute();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });


        isAdmin = user.isAdmin(CommunityActivity.idCommunity);

       if(!isAdmin)
            btnDeleteNew.setVisibility(View.GONE);

       ExecuteGetComments executeGetComments = new ExecuteGetComments();
       executeGetComments.execute();


    }

    public void addComment(View view){
        if (!TextUtils.isEmpty(edtTextComment.getText())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date1 = new Date();

            date = dateFormat.format(date1);

            ExecuteAddComment executeAddComment = new ExecuteAddComment(user.getId(),
                    idActual,edtTextComment.getText().toString());

            executeAddComment.execute();


        }
        else
            edtTextComment.setError("Campo Requerido");


    }

    private void cargarComments(JSONObject jsonResult) {

        try {
            listComments.clear();

            JSONArray jsonCommentsList = jsonResult.getJSONArray("comentarios");  //Importante
            for (int i = 0; i < jsonCommentsList.length(); i++) {
                JSONObject jsonComment = (JSONObject) jsonCommentsList.get(i);

                /*HttpGetBitmap request = new HttpGetBitmap();
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
                }*/


                Comment commentObject = new Comment(jsonComment.getInt("id"),
                        jsonComment.getInt("id_news"), jsonComment.getInt("id_user"),
                        jsonComment.getString("description"));

                listComments.add(commentObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvComments.setAdapter(new CommentAdapter());


    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class CommentAdapter extends BaseAdapter {

        public CommentAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return listComments.size();
        }

        @Override
        public Object getItem(int i) {
            return listComments.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.comments_list_item, null);
            }


            TextView txtUsername = view.findViewById(R.id.txtUserComment);

            ImageView imgImageComment = view.findViewById(R.id.img_Comment);

            Button btnReport = view.findViewById(R.id.btn_Report);

            TextView txtDescription = view.findViewById(R.id.txtCommentDescription);

            final int idComment = listComments.get(i).getId();

            final String description = listComments.get(i).getDescription();

            final int idUser = listComments.get(i).getId_user();

            String userName = "";

            Bitmap photoUser = null;

            for(int j = 0;j<CommunityActivity.listUsers.size();j++){
                User userActual = CommunityActivity.listUsers.get(j);
                if(Integer.toString(idUser).equals(userActual.getId())){
                    userName = userActual.getName() + " dijo:";
                    photoUser = userActual.getPhoto_rounded();

                }
            }

            final String usernameComment = userName;





            btnReport.setOnClickListener(new View.OnClickListener() {
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
            });






            txtDescription.setText(description);
            txtUsername.setText(userName);



            if(photoUser == null)
                imgImageComment.setVisibility(View.GONE);

            imgImageComment.setImageBitmap(photoUser);


            return view;
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

        public ExecuteAddComment(int idUser,int idNews, String description) {
            this.idNews = idNews;
            this.idUser = idUser;
            this.description = description;
            //this.date = date;
        }



        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            String[] keys = {"id_news","id_user", "description"};
            String[] values = {Integer.toString(idNews),Integer.toString(idUser),description};
            isAdded = api.post_put_base(keys,values,21,"POST",0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isAdded) {

                Toast.makeText(NewsMoreActivity.this, "Comentario exitoso", Toast.LENGTH_SHORT).show();

                ExecuteGetComments executeGetComments = new ExecuteGetComments();
                executeGetComments.execute();


            } else
                Toast.makeText(NewsMoreActivity.this, "Comentario fallido", Toast.LENGTH_SHORT).show();

        }
    }

    public class ExecuteGetComments extends AsyncTask<String, Void, String> {
        boolean isOk = false;




        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id"};
            String[] values = {Integer.toString(idActual)};
            isOk = api.get_delete_base(keys, values, 22, "GET", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarComments(API_Access.getInstance().getJsonObjectResponse());
                Toast.makeText(NewsMoreActivity.this, "Comentarios obtenidos", Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al obtener los comentarios";

                Toast.makeText(NewsMoreActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class ExecuteDeleteComments extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int id;



        ExecuteDeleteComments(int id){
            this.id = id;
        }


        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id"};
            String[] values = {Integer.toString(id)};
            isOk = api.get_delete_base(keys, values, 23, "DELETE", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetComments executeGetComments = new ExecuteGetComments();
                executeGetComments.execute();
                Toast.makeText(NewsMoreActivity.this, "Comentario eliminada", Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error al eliminar";

                Toast.makeText(NewsMoreActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
