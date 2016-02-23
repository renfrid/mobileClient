package org.sacids.android.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.sacids.android.R;
import org.sacids.android.adapters.SurveyFormListAdapter;
import org.sacids.android.database.DataSource;
import org.sacids.android.models.SurveyForm;
import org.sacids.android.preferences.PreferencesActivity;
import org.sacids.android.provider.InstanceProviderAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormsFragment extends Fragment {

    List<SurveyForm> surveyForms = new ArrayList<SurveyForm>();
    ListView lvSurveyFormsStatus;
    SurveyFormListAdapter adapter;

    private static String TAG = "Forms Fragment";

    private View rootView;

    public FormsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_forms, container, false);

        SharedPreferences mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        String username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, "");
        String password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, "");

        Log.d(TAG, "Username: " + username + ", password: " + password);

        DataSource ds = new DataSource(getActivity());

        Cursor c = ds.getAllCursor();
        surveyForms = cursorToList(c);


        lvSurveyFormsStatus = (ListView) rootView.findViewById(R.id.lv_survey_forms_status);
        lvSurveyFormsStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO: 1/29/2016 This should just pass FORM details to the other activity then we can perform the below task
                SurveyForm surveyForm = surveyForms.get(position);

                //start form details Activity here.
                Intent formDetailsIntent = new Intent(getActivity(), SurveyFormDetailsActivity.class);
                formDetailsIntent.putExtra(".models.SurveyForm", surveyForm);
                startActivity(formDetailsIntent);
            }
        });
        refreshDisplay();


        return rootView;

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

    private void refreshDisplay() {
        adapter = new SurveyFormListAdapter(getActivity(), surveyForms);
        lvSurveyFormsStatus.setAdapter(adapter);
    }


}