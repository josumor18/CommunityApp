package elcarmen.project.community.Business;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import elcarmen.project.community.R;

public class EventsActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        CommunityEventsFragment communityEventsFragment = new CommunityEventsFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.eventFragmentContainer, communityEventsFragment);
        fragmentTransaction.commit();
    }
}
