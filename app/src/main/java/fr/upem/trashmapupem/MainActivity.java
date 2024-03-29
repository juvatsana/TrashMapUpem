package fr.upem.trashmapupem;

import android.content.DialogInterface;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
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

import fr.upem.trashmapupem.Task.GetAllTrashTask;

/**
 * Activite principale.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private GetAllTrashTask getTrashTask = null;
    final static String TAG_MAP="FRAGMENT_PMAP";
    final static String TAG_LIST="FRAGMENT_PLIST";

    /**
     * Override de la methode onCreate.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Location location = new Location("");
        location.setLatitude(48.8392733d);
        location.setLongitude(2.5850778d);
        if(savedInstanceState!=null)
        {
            double MyLatitude = savedInstanceState.getDouble("MyLatitude");
            double MyLongitude = savedInstanceState.getDouble("MyLongitude");
            if((Double.compare(MyLatitude,0.0d)!=0)&&(Double.compare(MyLongitude,0.0d)!=0))
            {
                location.setLatitude(MyLatitude);
                location.setLongitude(MyLongitude);
                Log.i("OnCreate Main saves","Good values");
            }
            Log.e("OnCreate Main saves","Bad/No values");
        }
        else
        {
            Log.e("OnCreate Main saves","savedInstanceState not on");
        }
        setContentView(R.layout.activity_drawer);

        FragmentMap fragment = null;
        Class fragmentClass;
        fragmentClass = FragmentMap.class;
        try {
            fragment = (FragmentMap) FragmentMap.newInstance(this);
            //fragment.setCurrentLocation(location);

            Log.i("LaunchTask", "Ok");

            getTrashTask = new GetAllTrashTask();
            getTrashTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flContent, fragment,TAG_MAP).commit();

        // Assure that the transaction is made.
        fragmentManager.executePendingTransactions();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

    }

    /**
     * Override de la methode onSaveInstanceState.
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i("onSaveInstanceState","Location saved.");
        Location currentLocation = loadLastLocationFromFragmentMap();

        if(currentLocation==null)return;

        savedInstanceState.putDouble("MyLatitude", currentLocation.getLatitude());
        savedInstanceState.putDouble("MyLongitude", currentLocation.getLongitude());
    }

    /**
     * Setup le contenu du Drawer.
     * @param navigationView
     */
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

    /**
     * Override de la methode onBackPressed pour afficher un evenement customise.
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    /**
     * Cache le Fragment recupere avec le tag du fragment.
     * @param tag Tag du Fragment.
     */
    public void hideFragment(String tag)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment testFragmentList=fragmentManager.findFragmentByTag(tag);
        if(testFragmentList!=null)
        {
            Log.i("test Fragment",testFragmentList.getId()+" : "+R.id.flContent);
            Log.i("test Fragment",tag+" : NOT NULL");
            fragmentManager.beginTransaction().hide(testFragmentList).commit();

            // Assure that the transaction is made.
            fragmentManager.executePendingTransactions();
        }
        else
        {
            Log.i("test Fragment",tag+" : NULL");
        }
    }

    /**
     * Affiche un Fragment.
     * @param fragment Le Fragment a afficher.
     * @return retourne true si le Fragment a ete affiche.
     */
    public boolean showFragment(Fragment fragment)
    {
        if(fragment==null)return false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.onStart();
        fragmentManager.beginTransaction().show(fragment).commit();

        // Assure that the transaction is made.
        fragmentManager.executePendingTransactions();
        return true;
    }

    /**
     * Affiche la FragmentMap.
     * @param fragment Le FragmentMap a afficher.
     * @param config Avec une config speciale.
     * @return retourne true si le FragmentMap a ete affiche.
     */
    public boolean showFragmentMap(FragmentMap fragment,FragmentMap.FM_CONFIG config)
    {
        if(fragment==null)return false;
        fragment.loadConfig(config);
        showFragment(fragment);
        return true;
    }

    /**
     * Charge la derniere localisation de la FragmentMap.
     * @return Retourne la locasation de la FragmentMap ou une par default si la FragmentMap n'existe plus.
     */
    public Location loadLastLocationFromFragmentMap()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentMap theFragmentMap = (FragmentMap)fragmentManager.findFragmentByTag(TAG_MAP);
        Location locationDefault = new Location("");
        locationDefault.setLatitude(48.8392733d);
        locationDefault.setLongitude(2.5850778d);
        if(theFragmentMap==null)
        {
            return locationDefault;
        }
        Location testLocation = theFragmentMap.getCurrentLocation();
        if(testLocation==null)
        {
            return locationDefault;
        }
        return testLocation;
    }

    /**
     * Charge la FragmentMap.
     * @param config Utilise une config.
     */
    public void loadFragmentMap(FragmentMap.FM_CONFIG config)
    {
        hideFragment(TAG_LIST);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentMap testFragmentMap = (FragmentMap)fragmentManager.findFragmentByTag(TAG_MAP);
        if(showFragmentMap(testFragmentMap,config))return;
        Fragment fragment = FragmentMap.newInstance(this);
        fragmentManager.beginTransaction().add(R.id.flContent, fragment,TAG_MAP).commit();

        // Assure that the transaction is made.
        fragmentManager.executePendingTransactions();
        Log.i("loadFragmentMap", "added");
    }



    public boolean showFragmentMapTrack(FragmentMap fragment,Location location)
    {
        if(fragment==null)return false;
        fragment.loadConfig(FragmentMap.FM_CONFIG.TRACK);
        fragment.setTrackLocation(location);
        showFragment(fragment);
        return true;
    }

    /**
     * Charge un fragment map avec le mode Track.
     * @param pm La PoubelleMarker à afficher.
     */
    public void loadFragmentMapTrack(PoubelleMarker pm)
    {
        LatLng ll = pm.getMarkerOptions().getPosition();
        Location location = new Location("");
        location.setLatitude(ll.latitude);
        location.setLongitude(ll.longitude);
        hideFragment(TAG_LIST);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentMap testFragmentMap = (FragmentMap)fragmentManager.findFragmentByTag(TAG_MAP);
        if(showFragmentMapTrack(testFragmentMap,location))return;
        FragmentMap fragment = (FragmentMap)FragmentMap.newInstance(this);
        fragment.setTrackLocation(location);
        fragmentManager.beginTransaction().add(R.id.flContent, fragment,TAG_MAP).commit();

        // Assure that the transaction is made.
        fragmentManager.executePendingTransactions();
        Log.i("loadFragmentMap", "added");
    }

    /**
     * Charge la FragmentListDistance.
     */
    public void loadFragmentList()
    {
        Location thelocation = loadLastLocationFromFragmentMap();
        hideFragment(TAG_MAP);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentListDistance testFragmentList = (FragmentListDistance)fragmentManager.findFragmentByTag(TAG_LIST);
        if(testFragmentList!=null)
        {
            testFragmentList.setCurrentLocation(thelocation);
            showFragment(testFragmentList);
            return;
        }
        Fragment fragment = FragmentListDistance.newInstance(this,thelocation);
        fragmentManager.beginTransaction().add(R.id.flContent, fragment,TAG_LIST).commit();

        // Assure that the transaction is made.
        fragmentManager.executePendingTransactions();
        Log.i("loadFragmentList","added");
    }

    /**
     * Apelle lorsque l'on choisit une option dans le menu.
     * @param menuItem
     */
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_list:
                Log.i("nonono","in list");
                fragmentClass = FragmentMap.class;
                try {
                    loadFragmentList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_map:
                Log.i("nonono","in map");
                fragmentClass = FragmentMap.class;
                try {
                    loadFragmentMap(FragmentMap.FM_CONFIG.MAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_remove_trash:
                Log.i("nonono","in delete");
                fragmentClass = FragmentMap.class;
                try {
                    loadFragmentMap(FragmentMap.FM_CONFIG.DELETE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.nav_add_trash:
                Log.i("nonono", "in add");
                fragmentClass = FragmentMap.class;
                try {
                    loadFragmentMap(FragmentMap.FM_CONFIG.ADD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                Log.i("nonono", "on default");
                fragmentClass = FragmentMap.class;
        }

        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        // menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    /**
     * Override de la methode onOptionsItemSelected.
     * @param item menu selectione.
     * @return true si pris en compte.
     */
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

    /**
     * Apeller lorsque l'activite a fini de demarrer, apres le onStart.
     * @param savedInstanceState Sauvegarde.
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}