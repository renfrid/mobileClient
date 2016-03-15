package org.sacids.android.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import org.sacids.android.R;
import org.sacids.android.preferences.PreferencesActivity;
import org.sacids.android.receivers.FeedbackReceiver;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    private FormsFragment currentFragment;

    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private PendingIntent pendingIntent;
    private AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //default fragment view
        if (savedInstanceState == null){
            currentFragment = new FormsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, currentFragment).commit();
        }

        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Fragment fragment = null;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        //Forms Fragment
                        currentFragment = new FormsFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame, currentFragment);
                        fragmentTransaction.commit();
                        return true;

                    case R.id.blank_form:
                        //fill blank form
                        Intent blankForms = new Intent(getApplicationContext(),
                                FormChooserList.class);
                        startActivity(blankForms);
                        return true;
                    case R.id.edit_form:
                        //Edit forms
                        Intent editForms = new Intent(getApplicationContext(),
                                InstanceChooserList.class);
                        startActivity(editForms);
                        return true;
                    case R.id.send_forms:
                        //send finalized Forms
                        Intent sendForms = new Intent(getApplicationContext(),
                                InstanceUploaderList.class);
                        startActivity(sendForms);
                        return true;
                    case R.id.delete_forms:
                        //delete saved forms
                        Intent deleteForms = new Intent(getApplicationContext(),
                                FileManagerTabs.class);
                        startActivity(deleteForms);
                        return true;
                    case R.id.download_forms:
                        //form_list
                        Intent downloadForms = new Intent(getApplicationContext(),
                                FormDownloadList.class);
                        startActivity(downloadForms);
                        return true;
                    case R.id.settings:
                        //General Settings
                        Intent mySettings = new Intent(getApplicationContext(), PreferencesActivity.class);
                        startActivity(mySettings);
                        return true;
                    default:
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        // Retrieve a PendingIntent that will perform a broadcast
        Intent feedbackIntent = new Intent(this, FeedbackReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, feedbackIntent, 0);

        // Set the alarm here.
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 6*6*1000000; // 1Hours
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            default:
                return false;
        }

    }

}