package elcarmen.project.community.Business;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
public class SearchFragment extends Fragment {

    EditText edtxtSearch;
    Button btnSearch;
    ListView lvCommunitiesSearch;
    ProgressBar pbSearch;

    ArrayList<Community> communities = new ArrayList<Community>();
    ArrayList<Boolean> requests = new ArrayList<Boolean>();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        lvCommunitiesSearch = v.findViewById(R.id.lvCommunitiesSearch);
        pbSearch = v.findViewById(R.id.pbSearch);

        edtxtSearch = v.findViewById(R.id.edtxtSearch);
        btnSearch = v.findViewById(R.id.btnSearchCommunity);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbSearch.setVisibility(View.VISIBLE);
                String busqueda = edtxtSearch.getText().toString();
                ExecuteSearchCommunities executeSearch = new ExecuteSearchCommunities();
                executeSearch.execute(busqueda);
            }
        });

        return v;
    }

    private void cargarCommunities(JSONObject jsonResult) {

        try {
            communities.clear();
            requests.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            //LoginActivity.actualizarAuth_Token(token, getActivity());
            JSONArray jsonCommunitiesList = jsonResult.getJSONArray("resultados");
            JSONArray jsonRequestsList = jsonResult.getJSONArray("solicitudes");

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
                requests.add(jsonRequestsList.getBoolean(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvCommunitiesSearch.setAdapter(new CommunitiesAdapter());

        //rlStart.setVisibility(View.VISIBLE);
        //rlLoader.setVisibility(View.INVISIBLE);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class CommunitiesAdapter extends BaseAdapter {

        public CommunitiesAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return communities.size();
        }

        @Override
        public Object getItem(int i) {
            return communities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.found_communities_list_item, null);
            }

            ImageView imgCommunity = view.findViewById(R.id.img_CommListItem);
            TextView txtCommName = view.findViewById(R.id.txtCommListItem);
            Button btnUnirseCancelarSolicitud = view.findViewById(R.id.btnUnirseCancelarSolicitud);

            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(communities.get(i).getUrl_photo()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
                        R.drawable.img_comm_default);
            }
            imgCommunity.setImageBitmap(userImage);

            txtCommName.setText(communities.get(i).getName());

            if(requests.get(i)){
                btnUnirseCancelarSolicitud.setBackground(getResources().getDrawable(R.drawable.rounded_borders_cancel_button));
                btnUnirseCancelarSolicitud.setTextColor(getResources().getColor(R.color.colorGrayButtonText));
                btnUnirseCancelarSolicitud.setText("Cancelar");
            }

            return view;
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteSearchCommunities extends AsyncTask<String, Void, String> {
        boolean isOk = false;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();

            String[] keys = {"id", "auth_token", "busqueda"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), strings[0]};
            isOk = api.get_base(keys, values, 3, 1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarCommunities(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las comunidades";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }

            pbSearch.setVisibility(View.INVISIBLE);
        }
    }

}
