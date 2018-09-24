package elcarmen.project.community.Business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.API_Access;
import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

public class CommunityActivity extends AppCompatActivity {

    public static int idCommunity;
    public static String nameCommunity = "";
    public static boolean isInFeedFragment = false;

    TabLayout tabLayoutCommunity;
    private ViewPager containerCommunity;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    public static ArrayList<User> listUsers = new ArrayList<User>();

    private int[] tabSelectedIcons = {R.drawable.ic_feed_selected, R.drawable.ic_events_selected, R.drawable.ic_chat_selected, R.drawable.ic_members_selected, R.drawable.ic_edit_community_selected};
    private int[] tabUnselectedIcons = {R.drawable.ic_feed_unselected, R.drawable.ic_events_unselected, R.drawable.ic_chat_unselected, R.drawable.ic_members_unselected, R.drawable.ic_edit_community_unselected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        idCommunity = getIntent().getIntExtra("idCommunity",0);
        nameCommunity = getIntent().getStringExtra("nameCommunity");

        ExecuteGetUsers executeGetUsers = new ExecuteGetUsers();
        executeGetUsers.execute();

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
                case 0:
                    fragment = new CommunityFeedFragment();
                    break;
                case 1:
                    fragment = new CommunityEventsFragment();
                    break;
                case 2:
                    fragment = new CommunityChatListFragment();
                    break;
                case 3:
                    fragment = new CommunityMembersFragment();
                    break;
                case 4:
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

            return CommunityActivity.PlaceholderFragment.newInstance(position);// + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }
    }

    private void cargarUsers(JSONObject jsonResult) {

        try {
            listUsers.clear();

            JSONArray jsonUsersList = jsonResult.getJSONArray("usuarios");  //Importante
            for (int i = 0; i < jsonUsersList.length(); i++) {
                JSONObject jsonUser = (JSONObject) jsonUsersList.get(i);

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap newImage = null;
                try {
                    newImage = request.execute(jsonUser.getString("photo")).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                 int id = jsonUser.getInt("id");
                 String name = jsonUser.getString("name");
                 String email = jsonUser.getString("email");
                 String tel = jsonUser.getString("tel");
                 String cel = jsonUser.getString("cel");
                 String address = jsonUser.getString("address");
                 String url_photo = jsonUser.getString("photo");
                 String url_photo_rounded = jsonUser.getString("photo_thumbnail");
                 Bitmap photo = convertirBitmap(url_photo);
                 Bitmap photo_rounded = convertirBitmap(url_photo_rounded);
                 boolean privateProfile = jsonUser.getBoolean("isPrivate");

                if(photo == null){
                    photo = BitmapFactory.decodeResource( this.getApplicationContext().getResources(),
                            R.drawable.user_box_photo);
                }
                if(photo_rounded == null){
                    photo_rounded = BitmapFactory.decodeResource( this.getApplicationContext().getResources(),
                            R.drawable.user_rounded_photo);
                }

                User userObject = new User(Integer.toString(id),name,email,tel,cel,address,url_photo,url_photo_rounded,photo,photo_rounded,privateProfile);

                listUsers.add(userObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private Bitmap convertirBitmap(String url){
        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap newImage = null;
        try {
            newImage = request.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return newImage;
    }

    public class ExecuteGetUsers extends AsyncTask<String, Void, String> {
        boolean isOk = false;




        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();


            String[] keys = {"id_community"};
            String[] values = {Integer.toString(idCommunity)};
            isOk = api.get_delete_base(keys, values, 24, "GET", 1);



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarUsers(API_Access.getInstance().getJsonObjectResponse());
                Toast.makeText(CommunityActivity.this, "Usuarios obtenidos", Toast.LENGTH_SHORT).show();
            }else{
                String mensaje = "Error al obtener los usuarios";

                Toast.makeText(CommunityActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
