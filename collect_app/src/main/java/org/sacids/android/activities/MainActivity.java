package org.sacids.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.sacids.android.R;
import org.sacids.android.SurveyFormDetailsActivity;
import org.sacids.android.application.Collect;
import org.sacids.android.database.DataSource;
import org.sacids.android.models.SurveyForm;
import org.sacids.android.preferences.PreferencesActivity;
import org.sacids.android.provider.InstanceProviderAPI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    List<SurveyForm> surveyForms = new ArrayList<SurveyForm>();
    ListView lvSurveyFormsStatus;
    SurveyFormListAdapter adapter;

    private static String TAG = "Main Activity";

    DataSource ds = new DataSource(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, "");
        String password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, "");

        Log.d(TAG, "Username: " + username + ", password: " + password);

        Cursor c = ds.getAllCursor();
        surveyForms = cursorToList(c);

        findViewById(R.id.btn_start_odk_forms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            }
        });

        lvSurveyFormsStatus = (ListView) findViewById(R.id.lv_survey_forms_status);
        lvSurveyFormsStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO: 1/29/2016 This should just pass FORM details to the other activity then we can perform the below task

                SurveyForm surveyForm = surveyForms.get(position);

                //start form details Activity here.
                Intent formDetailsIntent = new Intent(MainActivity.this, SurveyFormDetailsActivity.class);
                formDetailsIntent.putExtra(".models.SurveyForm", surveyForm);
                startActivity(formDetailsIntent);
            }
        });
        refreshDisplay();
    }

    private List<SurveyForm> cursorToList(Cursor c) {
        List<SurveyForm> mList = new ArrayList<>();
        while (c.moveToNext()) {
            SurveyForm mForm = getSurveyFormFromCursor(c);
            Log.d(TAG, mForm.toString() + "\n");
            mList.add(mForm);
        }
        c.close();
        return mList;
    }

    private SurveyForm getSurveyFormFromCursor(Cursor c) {
        SurveyForm mForm = new SurveyForm();
        mForm.setId(c.getLong(c.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID)));
        mForm.setDisplayName(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME)));
        mForm.setSubmissionUri(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.SUBMISSION_URI)));
        mForm.setInstanceFilePath(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH)));
        mForm.setJrFormId(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_FORM_ID)));
        mForm.setJrVersion(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_VERSION)));
        mForm.setStatus(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS)));
        boolean canEdit = Boolean.parseBoolean(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.CAN_EDIT_WHEN_COMPLETE))); //.equalsIgnoreCase("true")) ? true : false;
        mForm.setCanEditWhenComplete(canEdit);
        //mForm.setLastStatusChangeDate(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE)));
        mForm.setDisplaySubText(c.getString(c.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_SUBTEXT)));
        return mForm;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {
            case R.id.action_fill_new_form:
                Collect.getInstance().getActivityLogger().logAction(this, "fillBlankForm", "click");
                Intent i = new Intent(getApplicationContext(), FormChooserList.class);
                startActivity(i);
                break;

            case R.id.action_change_language:
                showChangeLanguageDialog();
                break;
            case R.id.action_show_unsent_forms:
                Cursor c = ds.getUnsentCursor();
                surveyForms = cursorToList(c);
                refreshDisplay();
                break;
            case R.id.action_show_sent_and_unsent_forms:
                Cursor cursor = ds.getAllCursor();
                surveyForms = cursorToList(cursor);
                refreshDisplay();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChangeLanguageDialog() {
        //todo Open dialog to select language.
    }

    private void refreshDisplay() {
        adapter = new SurveyFormListAdapter(this, surveyForms);
        lvSurveyFormsStatus.setAdapter(adapter);
    }


    class SurveyFormListAdapter extends BaseAdapter {

        private Context context;
        private List<SurveyForm> myFormList;

        public SurveyFormListAdapter(Context context, List<SurveyForm> myFormList) {
            this.context = context;
            this.myFormList = myFormList;
        }

        @Override
        public int getCount() {
            return myFormList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = li.inflate(R.layout.single_form_info, null);

            SurveyForm mForm = myFormList.get(position);

            TextView tv = (TextView) view.findViewById(R.id.tv_form_name);
            tv.setText(mForm.getDisplayName());

            tv = (TextView) view.findViewById(R.id.tv_form_short_description);
            tv.setText(mForm.getDisplaySubText());

            tv = (TextView) view.findViewById(R.id.tv_form_status);
            tv.setText(mForm.getStatus());
            // change color depending on the form current form status

            return view;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // populates the list if there have been any changes after leaving the activity
        surveyForms = cursorToList(ds.getUnsentCursor());
        refreshDisplay();
    }
}
