package elcarmen.project.community.Business;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import elcarmen.project.community.R;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    private ViewPager container;

    FragmentTransaction fragmentTransaction;

    private int[] tabSelectedIcons = {R.drawable.ic_group_selected, R.drawable.ic_search_selected, R.drawable.ic_notifications_selected};
    private int[] tabUnselectedIcons = {R.drawable.ic_group_unselected, R.drawable.ic_search_unselected, R.drawable.ic_notifications_unselected};

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.appbartabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        cargarTabLayout();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                container.setCurrentItem(tab.getPosition());
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

        container = findViewById(R.id.container);
        container.setAdapter(mSectionsPagerAdapter);
        container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = null;
        switch(item.getItemId()){
            case R.id.main_item:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.agenda_item:
                //intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
                break;
            case R.id.destacadas_item:
                intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                startActivity(intent);
                break;
            case R.id.perfil_item:
                intent = new Intent(getApplicationContext(), EditUserActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_item:
                //TODO borrar auth token de las preferencias
                intent = new Intent(getApplicationContext(), LoginAcivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Metodos para el TabLayout:
    * cargarTabLayout()
    * cambiarFragment(int tab)
    * cambiarIconoSeleccionado(int position)
    */
    private void cargarTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setIcon(tabSelectedIcons[0]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabUnselectedIcons[1]));
        tabLayout.addTab(tabLayout.newTab().setIcon(tabUnselectedIcons[2]));
    }

    private void cambiarIconoSeleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabSelectedIcons[position]);
    }

    private void cambiarIconoDeseleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabUnselectedIcons[position]);
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
                    fragment = new CommunitiesFragment();
                    break;
                case 2:
                    fragment = new SearchFragment();
                    break;
                case 3:
                    fragment = new NotificationsFragment();
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

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}