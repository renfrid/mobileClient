package org.sacids.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.android.application.Collect;
import org.sacids.android.models.Feedback;
import org.sacids.android.models.SurveyForm;
import org.sacids.android.preferences.PreferencesActivity;
import org.sacids.android.provider.InstanceProviderAPI;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import web.RestClient;

public class SurveyFormDetailsActivity extends Activity {

    private static final boolean DO_NOT_EXIT = false;
    private AlertDialog mAlertDialog;
    private static String TAG = "SurveyForm";

    private List<Feedback> feedbackList = new ArrayList<Feedback>();
    private MessagesListAdapter adapter;
    private ListView lvFeedbacks;
    private Button btnEditForm;
    private ProgressDialog progressDialog;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String password;
    private SurveyForm mForm;
    private String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form_details);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, "");
        password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, "");
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        lvFeedbacks = (ListView) findViewById(R.id.lv_feedbacks);
        btnEditForm = (Button) findViewById(R.id.btn_edit_form);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mForm = b.getParcelable(".models.SurveyForm");
            Log.d(TAG, "From parcel => " + mForm.toString());

            if (mForm.isCanEditWhenComplete()) {
                btnEditForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startEditingSavedForm(mForm);
                    }
                });
            } else {
                if (mForm.getStatus().equalsIgnoreCase(InstanceProviderAPI.STATUS_SUBMITTED))
                    btnEditForm.setVisibility(View.GONE);
            }
            // get feedback from the server
            getFeedbackFromServer();
        }
    }

    private void getFeedbackFromServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading feedback...");
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", mForm.getJrFormId());
        params.add("username", "username");

        // research/feedback/get_feedback
        RestClient.get(username, password, serverUrl + "/feedback/get_feedback", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                Log.d(TAG, headers.toString());

                if (statusCode == 200) {

                    try {
                        feedbackList = getMessagesFromJsonResponse(response.getJSONArray("feedback"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Found " + feedbackList.size() + " feedbacks");

                    refreshDisplay();
                } else if (statusCode == 204) {
                    Log.d(TAG, "No feedback to display");
                    Toast.makeText(SurveyFormDetailsActivity.this, "No content found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                if (statusCode == 401) {
                    Toast.makeText(SurveyFormDetailsActivity.this, "Un authorized " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (statusCode == 401) {
                    Toast.makeText(SurveyFormDetailsActivity.this, "Un authorized " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_servey_form_details, menu);
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
            case R.id.action_refresh:
                getFeedbackFromServer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void refreshDisplay() {
        adapter = new MessagesListAdapter(this, feedbackList);
        lvFeedbacks.setAdapter(adapter);

        //     Toast.makeText(SurveyFormDetailsActivity.this, "No feedbackList to display", Toast.LENGTH_SHORT).show();
    }

    private void startEditingSavedForm(SurveyForm surveyForm) {
        Uri instanceUri = ContentUris.withAppendedId(InstanceProviderAPI.InstanceColumns.CONTENT_URI, surveyForm.getId());

        Collect.getInstance().getActivityLogger().logAction(this, "onListItemClick", instanceUri.toString());

        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)) {
            // caller is waiting on a picked form
            setResult(RESULT_OK, new Intent().setData(instanceUri));
        } else {
            // the form can be edited if it is incomplete or if, when it was
            // marked as complete, it was determined that it could be edited
            // later.
            String status = surveyForm.getStatus();
            boolean canEdit = surveyForm.isCanEditWhenComplete();
            if (!canEdit) {
                createErrorDialog(getString(R.string.cannot_edit_completed_form), DO_NOT_EXIT);
                return;
            }
            // caller wants to view/edit a form, so launch formentryactivity
            startActivity(new Intent(Intent.ACTION_EDIT, instanceUri));
        }
    }


    private void createErrorDialog(String errorMsg, final boolean shouldExit) {
        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog",
                                shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }


    class MessagesListAdapter extends BaseAdapter {

        private Context context;
        private List<Feedback> messageList;

        public MessagesListAdapter(Context context, List<Feedback> feedbacks) {
            this.context = context;
            this.messageList = feedbacks;
        }

        @Override
        public int getCount() {
            return messageList.size();
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
            View view = li.inflate(R.layout.single_message, null);

            Feedback msg = messageList.get(position);

            TextView tv = (TextView) view.findViewById(R.id.tv_message);
            tv.setText(msg.getMessage());

            tv = (TextView) view.findViewById(R.id.tv_date_sent);
            tv.setText(msg.getDate().toString());

//            tv = (TextView) view.findViewById(R.id.tv_form_status);
//            tv.setText(msg.getViewedBy());
            // change color depending on the form current form status

            return view;
        }
    }

    private List<Feedback> getMessagesFromJsonResponse(JSONArray jsonArray) throws JSONException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {

            @Override
            public String translateName(Field field) {
                if (field.getName().equals("userId"))
                    return "user_id";

                if (field.getName().equals("formId"))
                    return "form_id";

//                if (field.getName().equals("date"))
//                    return "date";

                return field.getName();
            }
        });
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Feedback>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }
}