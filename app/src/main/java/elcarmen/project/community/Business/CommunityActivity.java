package elcarmen.project.community.Business;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import elcarmen.project.community.R;

public class CommunityActivity extends AppCompatActivity {

    public static int idCommunity;
    public static String nameCommunity = "";

    TabLayout tabLayoutCommunity;
    private ViewPager containerCommunity;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int[] tabSelectedIcons = {R.drawable.ic_feed_selected, R.drawable.ic_events_selected, R.drawable.ic_chat_selected, R.drawable.ic_members_selected, R.drawable.ic_edit_community_selected};
    private int[] tabUnselectedIcons = {R.drawable.ic_feed_unselected, R.drawable.ic_events_unselected, R.drawable.ic_chat_unselected, R.drawable.ic_members_unselected, R.drawable.ic_edit_community_unselected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        idCommunity = getIntent().getIntExtra("idCommunit",0);
        nameCommunity = getIntent().getStringExtra("nameCommunity");

        getSupportActionBar().setSubtitle(nameCommunity);
        tabLayoutCommunity = findViewById(R.id.appbartabsCommunity);
        tabLayoutCommunity.setTabMode(TabLayout.MODE_FIXED);

        cargarTabLayout();

        tabLayoutCommunity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                containerCommunity.setCurrentItem(tab.getPosition());
                cambiarIconoSeleccionado(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                cambiarIconoDeseleccionado(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        containerCommunity = findViewById(R.id.containerCommunity);
        containerCommunity.setAdapter(mSectionsPagerAdapter);
        containerCommunity.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutCommunity));
    }

    /*
     * Metodos para el TabLayout:
     * cargarTabLayout()
     * cambiarFragment(int tab)
     * cambiarIconoSeleccionado(int position)
     */
    private void cargarTabLayout(){
        tabLayoutCommunity.addTab(tabLayoutCommunity.newTab().setIcon(tabSelectedIcons[0]));
        tabLayoutCommunity.addTab(tabLayoutCommunity.newTab().setIcon(tabUnselectedIcons[1]));
        tabLayoutCommunity.addTab(tabLayoutCommunity.newTab().setIcon(tabUnselectedIcons[2]));
        tabLayoutCommunity.addTab(tabLayoutCommunity.newTab().setIcon(tabUnselectedIcons[3]));
        tabLayoutCommunity.addTab(tabLayoutCommunity.newTab().setIcon(tabUnselectedIcons[4]));
    }

    private void cambiarIconoSeleccionado(int position){
        tabLayoutCommunity.getTabAt(position).setIcon(tabSelectedIcons[position]);
    }

    private void cambiarIconoDeseleccionado(int position){
        tabLayoutCommunity.getTabAt(position).setIcon(tabUnselectedIcons[position]);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;

            switch (sectionNumber){
                case 1:
                    fragment = new CommunityFeedFragment();
                    break;
                case 2:
                    fragment = new CommunityEventsFragment();
                    break;
                case 3:
                    fragment = new CommunityChatListFragment();
                    break;
                case 4:
                    fragment = new CommunityMembersFragment();
                    break;
                case 5:
                    fragment = new CommunityProfileFragment();
                    break;
            }
            return fragment;
        }

        /*@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }*/
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return CommunityActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }
    }
}
