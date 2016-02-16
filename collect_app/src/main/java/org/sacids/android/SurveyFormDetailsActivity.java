package org.sacids.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.sacids.android.adapters.FeedbackListAdapter;
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
    private FeedbackListAdapter adapter;
    private ListView listFeedbacks;
    private Button btnEditForm;
    private Button btnFeeedback;
    private EditText editFeedback;
    private TextView formName;
    private TextView formStatus;
    private ProgressDialog progressDialog;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String password;
    private SurveyForm mForm;
    private String serverUrl;
    private String message = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form_details);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, getString(R.string.default_sacids_username));
        password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, getString(R.string.default_sacids_password));
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        formName = (TextView) findViewById(R.id.tvform_name);
        formStatus = (TextView) findViewById(R.id.tv_form_status);
        listFeedbacks = (ListView) findViewById(R.id.list_feedback);
        editFeedback = (EditText) findViewById(R.id.edit_feedback);
        btnFeeedback = (Button) findViewById(R.id.btn_submit_feedback);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            mForm = b.getParcelable(".models.SurveyForm");
            Log.d(TAG, "From parcel => " + mForm.toString());

            //set form name
            String form_name = mForm.getDisplayName();
            formName.setText(form_name);

            //set form status
            String form_status = mForm.getStatus();
            formStatus.setText(form_status);

            // get feedback from the server
            getFeedbackFromServer();


            //if submit feedback
            btnFeeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message = editFeedback.getText().toString();

                    if (editFeedback.getText().length() < 1) {
                        editFeedback.setError("Your feedback is required");
                    } else {
                        //post feedback to the server
                        postFeedbackToServer();
                    }
                }
            });

        }
    }

    private void getFeedbackFromServer() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading feedback...");
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", mForm.getJrFormId());
        params.add("username", username);

        String feedbackURL = serverUrl + "/feedback/get_feedback";

        // research/feedback/get_feedback
        RestClient.get(username, password, feedbackURL, params, new JsonHttpResponseHandler() {

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

                    Log.d(TAG, "Found " + feedbackList.size() + " feedback");

                    //feedbackList adapter
                    adapter = new FeedbackListAdapter(SurveyFormDetailsActivity.this, feedbackList);
                    listFeedbacks.setAdapter(adapter);
                    //refreshDisplay();
                } else if (statusCode == 204) {
                    Log.d(TAG, "No feedback to display");
                    Toast.makeText(SurveyFormDetailsActivity.this, "No Feedback found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                if (statusCode == 401) {
                    //TODO apply authentication here
                    Toast.makeText(SurveyFormDetailsActivity.this, "Unauthorized " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }
        });
    }


    //Function to post details to the server
    private void postFeedbackToServer() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Posting feedback...");
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", mForm.getJrFormId());
        params.add("username", username);
        params.add("message", message);

        String post_feedbackURL = serverUrl + "/feedback/post_feedback";

        // research/feedback/get_feedback
        RestClient.post(post_feedbackURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                Log.d(TAG, headers.toString());

                if (statusCode == 200) {
                    Log.d(TAG, "Saving feedback success");
                    Toast.makeText(SurveyFormDetailsActivity.this, "Feedback sent, will get back to you soon", Toast.LENGTH_SHORT).show();

                } else if (statusCode == 204) {
                    Log.d(TAG, "Saving Feedback failed");
                    Toast.makeText(SurveyFormDetailsActivity.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                if (statusCode == 401) {
                    //TODO apply authentication here
                    Toast.makeText(SurveyFormDetailsActivity.this, "Unauthorized " + responseString, Toast.LENGTH_SHORT).show();
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