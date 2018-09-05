package elcarmen.project.community.Business;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunitiesFragment extends Fragment {

    private ArrayList<Community> communities = new ArrayList<Community>();

    public CommunitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communities, container, false);
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
                view = inflater.inflate(R.layout.communities_list_item, null);
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

            if(User_Singleton.getInstance().isAdmin(communities.get(i).getId())){
                view.setBackgroundColor(getResources().getColor(R.color.colorAccentTransparent));
            }

            if(!communities.get(i).isSubcommunity()){
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.setMarginStart(11);
            }

            return view;
        }
    }

}
