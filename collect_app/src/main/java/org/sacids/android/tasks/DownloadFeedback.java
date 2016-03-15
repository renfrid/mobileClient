package org.sacids.android.tasks;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.android.R;
import org.sacids.android.activities.MainActivity;
import org.sacids.android.database.SQLiteHandler;
import org.sacids.android.models.Feedback;
import org.sacids.android.preferences.PreferencesActivity;

import java.util.List;

import web.BackgroundClient;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadFeedback extends IntentService {

    private SharedPreferences mSharedPreferences;
    private String username;
    private String lastId, lastFormId;
    private String serverUrl;

    //Feedback database
    private SQLiteHandler db;

    // A integer, that identifies each notification uniquely
    public static final int NOTIFICATION_ID = 1;

    private static String TAG = "DownloadFeedback";


    public DownloadFeedback() {
        super("DownloadFeedback");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, getString(R.string.default_sacids_username));
            serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

            // SQLite database handler
            db = new SQLiteHandler(getApplicationContext());

            //Int Last inserted feedback
            Feedback lastFeedback = db.getLastFeedback();
            if (lastFeedback != null) {
                lastId = String.valueOf(lastFeedback.getId());
                lastFormId = lastFeedback.getFormId();
            } else {
                lastId = "";
                lastFormId = "";
            }

            // Writing Feedback to log
            Log.d("Last Feedback: ", lastId + "," + lastFormId);

            //pass params to query
            final RequestParams params = new RequestParams();
            params.add("username", username);
            params.add("last_id", lastId);

            String feedbackURL = serverUrl + "/feedback/get_feedback";

            // research/feedback/get_feedback
            BackgroundClient.get(feedbackURL, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(TAG, response.toString());
                    Log.d(TAG, headers.toString());

                    if (statusCode == 200) {
                        try {
                            //response from server
                            JSONArray jsonArray = response.getJSONArray("feedback");

                            //Initialize variables
                            String title = String.valueOf(R.string.app_name);
                            String receivedFormId = "";
                            int feedbackNo = jsonArray.length(); //Number of received feedback

                            //Iterate the jsonArray and print the info of JSONObjects
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //get string data from server
                                int feedbackId = jsonObject.getInt("id");
                                String formId = jsonObject.getString("form_id");
                                String message = jsonObject.getString("message");
                                String dateCreated = jsonObject.getString("date_created");

                                //append feedback data
                                receivedFormId += formId;

                                //insert feedback to database
                                db.addFeedback(new Feedback(feedbackId, receivedFormId, message, dateCreated));
                            }

                            //Feedback Message for Notification
                            String receivedMessages = feedbackNo + " feedback received";

                            //send notification
                            sendNotification(title, receivedMessages, receivedFormId);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (statusCode == 204) {
                        Log.d(TAG, "No feedback to at the moment");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);

                    Log.d(TAG, "Error response " + responseString);
                    if (statusCode == 401) {
                        //TODO apply authentication here
                        Log.d(TAG, "Authentication Failed");
                    }
                }
            });
        }
    }


    private void sendNotification(String title, String message, String formId) {
        // notification is selected
        Intent notifyIntent = new Intent(this, MainActivity.class);
        //notifyIntent.putExtra("form_id", form_id);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_launcher);
        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle(title);
        // Content text, which appears in smaller text below the title
        builder.setContentText(message);
        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        //builder.setSubText("Date: " + date_created);

        //set pending intent
        builder.setContentIntent(pendIntent).setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


}

