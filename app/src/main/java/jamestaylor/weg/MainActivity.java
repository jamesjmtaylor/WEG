package jamestaylor.weg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jamestaylor.weg.Equipment.Equipment;
import jamestaylor.weg.Equipment.EquipmentContent;

import java.lang.reflect.Field;

import jamestaylor.weg.R;

// An activity representing a single Item detail screen.

// Note: All categories marked as "Done" have representations in this class as well as the XMLs
// Done-Create listeners for all the editTextViews to update when changed using TipCalculator example.
// Done-Create a scrollView listener using the twitterSearches example.
// Done-Created a Listener for the ImageView using the flagQuiz example.
/**
 * This is the main activity for WEG and is what the user initially sees when they start the app
 */

public class MainActivity extends ActionBarActivity {

    //public static final String ARG_ITEM_ID = "item_id";

    /**
     * The weapon content this fragment is presenting.
     */
    //private EquipmentContent.Equipment mWeapon;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    // constants used when saving/restoring state
    //private static final String CURRENT_EQUIPMENT = "CURRENT_EQUIPMENT"; //May not be necessary

    // two member variables for the MainActivity Drawer
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    ArrayAdapter<Equipment> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Set the ListView for the drawer menu
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setDrawerRightEdgeSize(this, mDrawerLayout, 0.4f); // Adjusts the drawer sensitivity to 40% of the screen
        mActivityTitle = getTitle().toString();

        //add the drawer items using the helper class
        addDrawerItems();
        setupDrawer();

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);

        /*
        savedInstanceState is non-null when there is fragment state
        saved from previous configurations of this activity
        (e.g. when rotating the screen from portrait to landscape).
        In this case, the fragment will automatically be re-added
        to its container so we don't need to manually add it.
        For more information, see the Fragments API guide at:
        http://developer.android.com/guide/components/fragments.html
        */
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Creates the start fragment and commits it to the activity
            MainStartFragment fragment = new MainStartFragment();
            fragmentTransaction.add(R.id.item_detail_container, fragment, "START_FRAGMENT");
            fragmentTransaction.commit();
        }
    }

    public static void setDrawerRightEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;

        try {
            // find ViewDragHelper and set it accessible
            Field rightDraggerField = drawerLayout.getClass().getDeclaredField("mRightDragger");
            rightDraggerField.setAccessible(true);
            ViewDragHelper rightDragger = (ViewDragHelper) rightDraggerField.get(drawerLayout);
            // find edgesize and set is accessible
            Field edgeSizeField = rightDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(rightDragger);
            // set new edgesize
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(rightDragger, Math.max(edgeSize, (int) (displaySize.x * displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
            // ignore
        } catch (IllegalArgumentException e) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    // helper method to add items and configure the list
    private void addDrawerItems() {
        // This creates an array adapter for the Weapons hash map
        mAdapter = new ArrayAdapter<Equipment>(this,
                android.R.layout.simple_list_item_1,
                EquipmentContent.EQUIPMENT_LIST);

        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // The code below updates the fragment from this activity
                mDrawerLayout.closeDrawer(mDrawerList);
                new Handler().postDelayed(new Runnable() { // A runnable allows you to execute the code as a mini-method in a method.
                    @Override
                    public void run() {
                        MainFragment fragment = new MainFragment();
                        Bundle args = new Bundle();
                        args.putString(MainFragment.ARG_ITEM_ID, mAdapter.getItem(position).toString());
                        fragment.setArguments(args);

                        FragmentManager fragmentManager = getFragmentManager(); //support fragment manager allows you to manipulate existing fragments.
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            fragmentManager.popBackStack();
                        }
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        /*The first field is an int that represents the View to be replaced.The
                        second field is the fragment replacing it.The third field is a tag String that
                        you can use to select the fragment from the BackStack with.*/
                        fragmentTransaction.replace(R.id.item_detail_container, fragment, "EQUIPMENT_FRAGMENT");
                        fragmentTransaction.addToBackStack("EQUIPMENT_FRAGMENT");
                        fragmentTransaction.commit();


                        // update selected item and title, then close the drawer
                        mDrawerList.setItemChecked(position, true);

                        getSupportActionBar().setTitle(mActivityTitle);
                    }
                }, 200); // The 200 millisecond post delayed handler allows the tab to slide back
                // before loading the fragment to avoid a stutter in the slide.
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            //  Called when a drawer has settled in a completely open state
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0); // this disables the arrow @ completed state
                getSupportActionBar().setTitle("Equipment");
                invalidateOptionsMenu();
            }
            // Called when a drawer has settled in a completely closed state
            public void onDrawerClosed(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0); // this disables the animation
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();


                mDrawerToggle.syncState();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false); // removes the hamburger animation and replaces it with just the arrow if set to false
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    // Syncs the indicator to match the current state of the navigation drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    // Keeps things in sync when the configuration of the Activity changes (i.e. orientation change)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.home) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            // Creates the start fragment and commits it to the activity
            MainStartFragment fragment = new MainStartFragment();
            fragmentTransaction.add(R.id.item_detail_container, fragment);
            fragmentTransaction.commit();

        }
        if (id == R.id.memorization_aid) {
            Intent mem = new Intent(this, MemorizationAidActivity.class);
            startActivity(mem);
            return true;
        }
        else if (id == R.id.weapon_calculator) {
            Intent calc = new Intent(this, WeaponCalculatorActivity.class);
            startActivity(calc);
            return true;
        }
        else if (id == R.id.help) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(R.string.help);
            aboutBuilder.setMessage(R.string.main_help);

            aboutBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setNeutralButton

            // create an AlertDialog from the Builder
            AlertDialog aboutDialog = aboutBuilder.create();
            aboutDialog.show(); // show the dialog
            return true;
        }
        else if (id == R.id.about) {
            // create a new AlertDialog Builder and set its title
            AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
            aboutBuilder.setTitle(R.string.about);
            aboutBuilder.setMessage(R.string.about_message);

            aboutBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                } // end method onClick
            }); // end call to setNeutralButton

            // create an AlertDialog from the Builder
            AlertDialog aboutDialog = aboutBuilder.create();
            aboutDialog.show(); // show the dialog
            return true;
        }

        // Opens the drawer from the right when selected, rather than the left
        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        }

        // Activate the navigation drawer toggle

        //if (mDrawerToggle.onOptionsItemSelected(item)) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
            MainStartFragment fragment = new MainStartFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            fm.beginTransaction().replace(R.id.item_detail_container, fragment).commit();
            /* Learning point: using android.R.id.content selects the entirety of the current
             fragment or activity id int, in this case, the activity, which prevents the drawer
             menu from displaying. R.id.item_detail_container selects one component of the XML
             while leaving everything else alone.*/
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}
