package elcarmen.project.community.Business;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityEventsFragment extends Fragment {


    public CommunityEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_events, container, false);
    }

}
