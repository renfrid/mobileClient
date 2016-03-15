package web;

/**
 * Created by Renfrid-Sacids on 3/15/2016.
 */
import android.net.Uri;
import android.util.Log;

import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.net.URL;


/**
 * Created by Akyoo on 9/12/15.
 */

public class BackgroundClient {
    private static SyncHttpClient client = new SyncHttpClient();
    private URL urlObject;


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d("Rest client", "Request page => " + relativeUrl);
        return relativeUrl;
    }
}
