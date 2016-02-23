package org.sacids.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.sacids.android.R;
import org.sacids.android.application.Collect;
import org.sacids.android.preferences.PreferencesActivity;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Navigation drawer
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
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

            case R.id.action_fill_new_form:
                //Launching the fill blank form Activity
                Collect.getInstance().getActivityLogger().logAction(this, "fillBlankForm", "click");
                Intent i = new Intent(getApplicationContext(), FormChooserList.class);
                startActivity(i);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return false;
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                //Forms Fragment
                fragment = new FormsFragment();
                break;
            case 1:
                //form_list
                Intent downloadForms = new Intent(getApplicationContext(),
                        FormDownloadList.class);
                startActivity(downloadForms);
                break;
            case 2:
                //fill blank form
                Intent blankForms = new Intent(getApplicationContext(),
                        FormChooserList.class);
                startActivity(blankForms);
                break;
            case 3:
                //Edit forms
                Intent editForms = new Intent(getApplicationContext(),
                        InstanceChooserList.class);
                startActivity(editForms);
                break;
            case 4:
                //send finalized Forms
                Intent sendForms = new Intent(getApplicationContext(),
                        InstanceUploaderList.class);
                startActivity(sendForms);
                break;
            case 5:
                //delete saved forms
                Intent deleteForms = new Intent(getApplicationContext(),
                        FileManagerTabs.class);
                startActivity(deleteForms);
                break;
            case 6:
                //General Settings
                Intent mySettings = new Intent(this, PreferencesActivity.class);
                startActivity(mySettings);
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            //set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

}
