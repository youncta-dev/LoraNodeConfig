package com.youncta.loranodeconfig;

import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DevicesFragment extends ListFragment implements OnItemClickListener, OnSaveData {
    private static final String ARG_PARAM = "param1";
    private String mParam1;
    private OnFragmentInteractionListener mListener;

    TextView deviceInfo;
    ListView deviceList;

    public DevicesFragment() {
        // Required empty public constructor
    }

    public static DevicesFragment newInstance(String param1) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_devices, container, false);

        final AppCompatActivity act = (AppCompatActivity) getActivity();
        act.findViewById(R.id.toolbar);
        act.setTitle("Devices");

        deviceInfo = (TextView) root.findViewById(R.id.device_info);
        deviceList = (ListView) root.findViewById(R.id.device_list);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Planets, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }

    void initData() {
    }

    @Override
    public void saveData() {

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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Object source, int action, Object par);
    }
}
