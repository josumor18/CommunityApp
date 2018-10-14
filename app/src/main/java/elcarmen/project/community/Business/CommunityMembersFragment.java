package elcarmen.project.community.Business;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityMembersFragment extends Fragment {

    RelativeLayout rlVerSolicitudes;
    TextView txtCantSolicitudes;

    public CommunityMembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community_members, container, false);

        rlVerSolicitudes = v.findViewById(R.id.rlVerSolicitudes);
        txtCantSolicitudes = v.findViewById(R.id.txtCantSolicitudes);
        txtCantSolicitudes.setVisibility(View.INVISIBLE);

        rlVerSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCantSolicitudes.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(getActivity(), RequestsActivity.class);
                startActivity(intent);
            }
        });

        if(!User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity)){
            rlVerSolicitudes.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            if(User_Singleton.getInstance().isAdmin(CommunityActivity.idCommunity)){
                ExecuteGetRequestCount executeGetRequestCount = new ExecuteGetRequestCount();
                executeGetRequestCount.execute();
            }else{
                if(rlVerSolicitudes != null){
                    rlVerSolicitudes.setVisibility(View.GONE);
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetRequestCount extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            User_Singleton user = User_Singleton.getInstance();
            String[] keys = {"id", "auth_token", "id_community"};
            String[] values = {Integer.toString(user.getId()), user.getAuth_token(), Integer.toString(CommunityActivity.idCommunity)};
            isOk = api.get_delete_base(keys, values, 12, "GET",1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                try {
                    JSONObject response = API_Access.getInstance().getJsonObjectResponse();

                    int cantidad = response.getInt("cantidad");
                    if(cantidad > 0){
                        txtCantSolicitudes.setText(Integer.toString(cantidad));
                        txtCantSolicitudes.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
