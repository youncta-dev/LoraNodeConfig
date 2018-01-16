package com.youncta.loranodeconfig;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


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

    EditText applicationKey;
    EditText deviceName;
    EditText locationName;
    EditText gpsLon;
    EditText gpsLat;
    EditText gpsAlt;

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

        applicationKey = (EditText) root.findViewById(R.id.input_application_key);
        deviceName = (EditText) root.findViewById(R.id.input_device_name);
        locationName = (EditText) root.findViewById(R.id.input_location_name);

        gpsLon = (EditText) root.findViewById(R.id.input_gps_lon);
        gpsLat = (EditText) root.findViewById(R.id.input_gps_lat);
        gpsAlt = (EditText) root.findViewById(R.id.input_gps_alt);

        final ImageView locSourceIndIcon = (ImageView) root.findViewById(R.id.gps_source_indicator);
        final ImageView systemNameSourceIndIcon = (ImageView) root.findViewById(R.id.system_name_source_indicator);
        final ImageView locationNameSourceIndIcon = (ImageView) root.findViewById(R.id.location_name_source_indicator);


        applicationKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                systemNameSourceIndIcon.setImageResource(R.drawable.ic_edit_black);

                return false;
            }


        });

        deviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationNameSourceIndIcon.setImageResource(R.drawable.ic_edit_black);
            }
        });

        locSourceIndIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Location location = getLastKnownLocation();
                gpsLon.setText(String.format("%2.2f", location.getLongitude()));
                gpsLat.setText(String.format("%2.2f", location.getLatitude()));
                gpsAlt.setText(String.format("%2.2f", location.getAltitude()));
                locSourceIndIcon.setImageResource(R.drawable.ic_gps_fixed);
                Toast.makeText(act, "clicked", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }


    void initData() {

        DecimalFormat coordinateFormatter = new DecimalFormat("00.000");

    }

    @Override
    public void saveData() {
        DecimalFormat coordinateFormatter = new DecimalFormat("00.000");
        String pos =  gpsLat.getText() + "," + gpsLon.getText() + "," + gpsAlt.getText();
        String info = applicationKey.getText() + ";" + deviceName.getText() + ";" + locationName.getText() + ";" + pos + ";";

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
        String locationProvider = null;
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;


            try {
                l = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {

            }
            if (l == null) {
                continue;
            }
            locationProvider = provider;
            if (bestLocation == null) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

        if (bestLocation == null) {
            bestLocation = new Location("dummy");
            bestLocation.setLongitude(84.9789);
            bestLocation.setLatitude(56.4661);
            bestLocation.setAltitude(0);
        }
        return bestLocation;
    }
}
