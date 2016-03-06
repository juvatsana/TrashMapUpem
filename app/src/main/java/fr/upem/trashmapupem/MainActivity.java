package fr.upem.trashmapupem;

import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = FragmentMap.class;
        try {
            fragment = (Fragment) FragmentMap.newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Assure that the transaction is made.
        getSupportFragmentManager().executePendingTransactions();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        FragmentMap fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_list:
                Log.i("nonono","in list");
                fragmentClass = FragmentMap.class;
                break;
            case R.id.nav_map:
                Log.i("nonono","in map");
                fragmentClass = FragmentMap.class;
                try {
                    fragment = (FragmentMap) FragmentMap.newInstance(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_add_trash:
                Log.i("nonono", "in delete");
                fragmentClass = FragmentMap.class;
                try {
                    fragment = (FragmentMap) FragmentMap.newInstance(this);

                    /*MarkerOptions test = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pb))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(new LatLng(48.838790, 2.585753));
                    fragment.addFragmentMapMarker(test);*/
                    fragment.setOnListenerAjout(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Render a message
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("Click and press yes for add marker").show();
                break;
            default:
                Log.i("nonono","on default");
                fragmentClass = FragmentMap.class;
        }


        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Assure that the transaction is made.
        getSupportFragmentManager().executePendingTransactions();

        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        // menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}