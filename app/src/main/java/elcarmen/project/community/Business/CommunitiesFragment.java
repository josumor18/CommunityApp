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
import android.widget.AdapterView;
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
public class CommunitiesFragment extends Fragment {

    ListView lvCommunities;
    RelativeLayout rlCommunities, rlCommunitiesPB;
    FloatingActionButton ftbtnCreateCommunity;

    private ArrayList<Community> communities = new ArrayList<Community>();

    public CommunitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_communities, container, false);

        ftbtnCreateCommunity = v.findViewById(R.id.ftbtnCreateCommunity);
        ftbtnCreateCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateCommunityActivity.class);
                startActivity(intent);
            }
        });

        rlCommunities = v.findViewById(R.id.rlCommunities);
        rlCommunitiesPB = v.findViewById(R.id.rlCommunitiesPB);
        lvCommunities = v.findViewById(R.id.lvCommunities);
        lvCommunities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CommunityActivity.class);
                int idCommunity_enviar = communities.get(position).getId();
                String nameCommunity_enviar = communities.get(position).getName();
                intent.putExtra("idCommunity",idCommunity_enviar);
                intent.putExtra("nameCommunity", nameCommunity_enviar);
                startActivity(intent);
            }
        });

        rlCommunities.setVisibility(View.INVISIBLE);
        rlCommunitiesPB.setVisibility(View.VISIBLE);

        ExecuteGetCommunities executeGetCommunities = new ExecuteGetCommunities();
        executeGetCommunities.execute();

        return v;
    }


    private void cargarCommunities(JSONObject jsonResult) {

        try {
            communities.clear();
            String token = jsonResult.getString("auth_token");
            User_Singleton.getInstance().setAuth_token(token);
            //LoginActivity.actualizarAuth_Token(token, getActivity());
            //JSONArray jsonListUserPosts = jsonResult.getJSONArray("posts");
            JSONArray jsonCommunitiesList = jsonResult.getJSONArray("communities");
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvCommunities.setAdapter(new CommunitiesAdapter());

        rlCommunities.setVisibility(View.VISIBLE);
        rlCommunitiesPB.setVisibility(View.INVISIBLE);
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
                if(!communities.get(i).isSubcommunity()){
                    view = inflater.inflate(R.layout.communities_list_item, null);
                }else{
                    view = inflater.inflate(R.layout.communities_list_sub_item, null);
                }

            }

            ImageView imgCommunity = view.findViewById(R.id.img_CommListItem);
            TextView txtCommName = view.findViewById(R.id.txtCommListItem);

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

            if(!User_Singleton.getInstance().isAdmin(communities.get(i).getId())){
                ImageView imgAdminIcon = view.findViewById(R.id.imgAdminIcon);
                imgAdminIcon.setVisibility(View.INVISIBLE);
            }

            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetCommunities extends AsyncTask<String, Void, String> {
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
            String[] keys = {"id", "auth_token"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token()};
            isOk = api.get_delete_base(keys, values, 2, "GET",1);

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
        }
    }

}
