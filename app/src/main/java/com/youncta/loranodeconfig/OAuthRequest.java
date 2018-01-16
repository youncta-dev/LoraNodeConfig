package com.youncta.loranodeconfig;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Di Bacco Antonio on 10/7/2017.
 */

public class OAuthRequest {

    String accessTokenUrl;
    String serviceUrl;
    String accessToken;
    Context context;

    OAuthRequestListener listener;

    public interface OAuthRequestListener {
        void OnOAuthRequestFinished(JSONObject response);
    }

    public OAuthRequest(Context context, String server, String accessTokenUrl, String serviceUrl) {
        this.context = context;
        this.accessTokenUrl = server+accessTokenUrl;
        this.serviceUrl = server+serviceUrl;

    }

    public void startRequest(OAuthRequestListener listener) {
        this.listener = listener;
        requestAccessToken();
    }

    private void requestService() {
        String url =  serviceUrl+"&access_token=" +accessToken;
        // make HTTP request to retrieve the weather
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url , null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response)  {
                     if (listener != null)
                        listener.OnOAuthRequestFinished(response);

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                if (listener != null)
                    listener.OnOAuthRequestFinished(null);
            }
        })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        Volley.newRequestQueue(context.getApplicationContext()).add(jsonObjReq);
    }

    private void requestAccessToken() {

        String url =  accessTokenUrl;
        // make HTTP request to retrieve the weather
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url , null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response)  {
                try {
                    // Parsing json object response
                    // response will be a json object
                    accessToken = response.getString("access_token");
                    System.out.println(accessToken);

                    requestService();

                } catch (JSONException e) {
                    accessToken = null;
                    if (listener != null)
                        listener.OnOAuthRequestFinished(null);
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                accessToken = null;

                if (listener != null)
                    listener.OnOAuthRequestFinished(null);
            }
        })  {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = "my-trusted-client" + ":" + "secret";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        Volley.newRequestQueue(context.getApplicationContext()).add(jsonObjReq);
    }
}
