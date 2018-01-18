package com.youncta.loranodeconfig;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfigFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfigFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigFragment extends Fragment implements OnSaveData {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    LocationManager locationManager = null;

    EditText deviceName;
    EditText deviceEui;
    EditText applicationKey;
    EditText locationName;
    EditText gpsLon;
    EditText gpsLat;
    EditText gpsAlt;
    Button configureDevice;
    Button testDevice;

    final private static String applicationServer = "172.30.0.8:8090";

    private OnFragmentInteractionListener mListener;

    public ConfigFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ConfigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigFragment newInstance(String param1) {
        ConfigFragment fragment = new ConfigFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_config, container, false);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        act.findViewById(R.id.toolbar);
        act.setTitle("System");

        deviceName = (EditText) root.findViewById(R.id.input_device_name);
        deviceEui = (EditText) root.findViewById(R.id.input_device_eui);
        applicationKey = (EditText) root.findViewById(R.id.input_application_key);
        locationName = (EditText) root.findViewById(R.id.input_location_name);

        gpsLon = (EditText) root.findViewById(R.id.input_gps_lon);
        gpsLat = (EditText) root.findViewById(R.id.input_gps_lat);
        gpsAlt = (EditText) root.findViewById(R.id.input_gps_alt);

        configureDevice = (Button) root.findViewById(R.id.configure_device_button);
        testDevice = (Button) root.findViewById(R.id.test_device_button);
        configureDevice.setEnabled(true);
        testDevice.setEnabled(false);
        testDevice.setBackgroundColor(Color.GRAY);

        final ImageView locSourceIndIcon = (ImageView) root.findViewById(R.id.gps_source_indicator);
        final ImageView deviceNameSourceIndIcon = (ImageView) root.findViewById(R.id.device_name_source_indicator);
        final ImageView deviceEuiSourceIndIcon = (ImageView) root.findViewById(R.id.device_eui_source_indicator);
        final ImageView applicationKeySourceIndIcon = (ImageView) root.findViewById(R.id.application_key_source_indicator);
        final ImageView locationNameSourceIndIcon = (ImageView) root.findViewById(R.id.location_name_source_indicator);


        deviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                deviceNameSourceIndIcon.setImageResource(R.drawable.ic_edit_black);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        locationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                locationNameSourceIndIcon.setImageResource(R.drawable.ic_edit_black);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        deviceEuiSourceIndIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deviceEui.setText(RandomStringUtils.randomNumeric(16));
                Toast.makeText(act, "Device EUI generated", Toast.LENGTH_SHORT).show();
                if (!configureDevice.isEnabled()) {
                    testDevice.setEnabled(false);
                    testDevice.setBackgroundColor(Color.GRAY);
                    configureDevice.setEnabled(true);
                    configureDevice.setBackgroundColor(getResources().getColor(R.color.app));
                }
            }
        });

        applicationKeySourceIndIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                applicationKey.setText(RandomStringUtils.randomNumeric(32));
                Toast.makeText(act, "Application Key generated", Toast.LENGTH_SHORT).show();
            }
        });

        locSourceIndIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Location location = getLastKnownLocation();
                gpsLon.setText(String.format("%2.2f", location.getLongitude()));
                gpsLat.setText(String.format("%2.2f", location.getLatitude()));
                gpsAlt.setText(String.format("%2.2f", location.getAltitude()));
                locSourceIndIcon.setImageResource(R.drawable.ic_gps_fixed);
                Toast.makeText(act, "Location Updated", Toast.LENGTH_SHORT).show();
            }
        });

        configureDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (deviceName.length() != 0 && deviceEui.length() != 0 && applicationKey.length() != 0) {
                    createNode();
                } else {
                    Toast.makeText(act, "Some fields are empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        testDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!configureDevice.isEnabled() && deviceEui.length() != 0) {
                    testNode();
                } else {
                    Toast.makeText(act, "Configure device first", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }


    void initData() {
        DecimalFormat coordinateFormatter = new DecimalFormat("00.000");
    }

    public void createNode() {
        final String REQUEST_TAG = "configuration.node";
        String url = "https://" + applicationServer + "/api/devices";

        JSONObject params = new JSONObject();
        try {
            params.put("applicationID", "1");
            params.put("description", "A new test device...");
            params.put("deviceProfileID", "e4fe0442-c898-4f12-8eee-f4e66e941db6");
            params.put("devEUI", deviceEui.getText().toString());
            params.put("name", deviceName.getText().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final String requestBody = params.toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(REQUEST_TAG, response.toString());
                        addKey();
                        Toast.makeText(getActivity(), "Device configured successfully", Toast.LENGTH_LONG).show();
                        configureDevice.setEnabled(false);
                        configureDevice.setBackgroundColor(Color.GRAY);
                        testDevice.setEnabled(true);
                        testDevice.setBackgroundColor(getResources().getColor(R.color.app));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(REQUEST_TAG, "Error: " + error.getMessage());
                        String body;
                        if (error.networkResponse.data != null) {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                System.out.println(body);
                                if (body.contains("exists")) {
                                    Toast.makeText(getActivity(), "Error: DeviceEUI already exists", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error: Server not responding", Toast.LENGTH_LONG).show();
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3JhLWFwcC1zZXJ2ZXIiLCJpYXQiOjE1MTU2NzgzMzMsImV4cCI6MTU0NzIzOTUzMywiYXVkIjoibG9yYS1hcHAtc2VydmVyIiwic3ViIjoidXNlciIsInVzZXJuYW1lIjoiYWRtaW4ifQ.-XPr8-H84fUn_w5WYCEkMogP-YP-eARTjN3eCv7O4SI";
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> params = new HashMap<String, String>();
                headersSys.remove("Authorization");
                params.put("Authorization", jwtToken);
                params.putAll(headersSys);
                return params;
            }
        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(postRequest, REQUEST_TAG);
    }

    public void addKey() {
        final String REQUEST_TAG = "configuration.key";
        String url = "https://" + applicationServer + "/api/devices/" + deviceEui.getText().toString() + "/keys";

        JSONObject params = new JSONObject();
        try {
            JSONObject subParams = new JSONObject();
            subParams.put("appKey", applicationKey.getText().toString());
            params.put("devEUI", deviceEui.getText().toString());
            params.put("deviceKeys", subParams);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final String requestBody = params.toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(REQUEST_TAG, response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(REQUEST_TAG, "Error: " + error.getMessage());
                        String body;
                        if (error.networkResponse.data != null) {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                Toast.makeText(getActivity(), "Error: Server not responding", Toast.LENGTH_LONG).show();
                                System.out.println(body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3JhLWFwcC1zZXJ2ZXIiLCJpYXQiOjE1MTU2NzgzMzMsImV4cCI6MTU0NzIzOTUzMywiYXVkIjoibG9yYS1hcHAtc2VydmVyIiwic3ViIjoidXNlciIsInVzZXJuYW1lIjoiYWRtaW4ifQ.-XPr8-H84fUn_w5WYCEkMogP-YP-eARTjN3eCv7O4SI";
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> params = new HashMap<String, String>();
                headersSys.remove("Authorization");
                params.put("Authorization", jwtToken);
                params.putAll(headersSys);
                return params;
            }
        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(postRequest, REQUEST_TAG);
    }

    public void testNode() {
        final String REQUEST_TAG = "test.node";
        String url = "https://" + applicationServer + "/api/devices/" + deviceEui.getText().toString() + "/queue";

        JSONObject params = new JSONObject();
        try {
            params.put("reference", "reference-string");
            params.put("confirmed", true);
            params.put("fPort", 2);
            params.put("data", "MQ==");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final String requestBody = params.toString();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(REQUEST_TAG, response.toString());
                        Toast.makeText(getActivity(), "LED blink notification sent", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(REQUEST_TAG, "Error: " + error.getMessage());
                        String body;
                        if (error.networkResponse.data != null) {
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                if (body.contains("object does not exist")) {
                                    Toast.makeText(getActivity(), "Error: Start device first", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error: Server not responding", Toast.LENGTH_LONG).show();
                                }
                                System.out.println(body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJsb3JhLWFwcC1zZXJ2ZXIiLCJpYXQiOjE1MTU2NzgzMzMsImV4cCI6MTU0NzIzOTUzMywiYXVkIjoibG9yYS1hcHAtc2VydmVyIiwic3ViIjoidXNlciIsInVzZXJuYW1lIjoiYWRtaW4ifQ.-XPr8-H84fUn_w5WYCEkMogP-YP-eARTjN3eCv7O4SI";
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> params = new HashMap<String, String>();
                headersSys.remove("Authorization");
                params.put("Authorization", jwtToken);
                params.putAll(headersSys);
                return params;
            }
        };

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getContext()).addToRequestQueue(postRequest, REQUEST_TAG);
    }

    private String formatString(String data) {
        return StringUtils.removeEnd(data.replaceAll("(.{2})", "$1:"), ":");
    }

    @Override
    public void saveData() {
        DecimalFormat coordinateFormatter = new DecimalFormat("00.000");
        String pos = gpsLat.getText() + "," + gpsLon.getText() + "," + gpsAlt.getText();
        String info = deviceName.getText() + ";" + formatString(deviceEui.getText().toString()) + ";" + formatString(applicationKey.getText().toString()) + ";" + locationName.getText() + ";" + pos + ";";

        NfcManager.getInstance().setTextMessage(info);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Object source, int action, Object par);
    }


    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            try {
                l = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if (bestLocation == null) {
            bestLocation = new Location("dummy");
            bestLocation.setLongitude(45.00);
            bestLocation.setLatitude(9.00);
            bestLocation.setAltitude(0);
        }
        return bestLocation;
    }
}
