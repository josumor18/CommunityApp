package elcarmen.project.community.Business;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import elcarmen.project.community.R;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;

    FragmentTransaction fragmentTransaction;

    private int[] tabSelectedIcons = {R.drawable.ic_group_selected, R.drawable.ic_search_selected, R.drawable.ic_notifications_selected};
    private int[] tabUnselectedIcons = {R.drawable.ic_group_unselected, R.drawable.ic_search_unselected, R.drawable.ic_notifications_unselected};

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
                cambiarFragment(tab.getPosition());
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
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

    private void cambiarFragment(int tab){
        switch(tab){
            case 0:
                //StartFragment startFragment = new StartFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.contenedor, startFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                //SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.contenedor, searchFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                //ChatListFragment chatListFragment = new ChatListFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.contenedor, chatListFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    private void cambiarIconoSeleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabSelectedIcons[position]);
    }

    private void cambiarIconoDeseleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabUnselectedIcons[position]);
    }
}