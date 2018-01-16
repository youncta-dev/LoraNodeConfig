package com.youncta.loranodeconfig;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoginFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener, ConfigFragment.OnFragmentInteractionListener {

    NfcAdapter mNfcAdapter;

    NavigationView navigationView;

    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    OnTagDetected nfcManager;

    Context context;

    FloatingActionMenu fabMenuLoad;
    FloatingActionMenu fabMenuWrite;

    Fragment currentFragment = null;

    Snackbar snackBar;
    public static final int DISPLAY_SNACKBAR_MESSAGE = 0x12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not enabled.", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FAB

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment f = LoginFragment.newInstance("");
        transaction.add(R.id.content_main, f).commit();
        currentFragment = f;

        nfcManager = new NfcManager();

        // Check location access permissions
        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] { }, 12);

        }
        */
        fabMenuLoad = (FloatingActionMenu) this.findViewById(R.id.fab_menu_read);
        fabMenuLoad.setVisibility(View.VISIBLE);

        FloatingActionButton fabNfcRead = (FloatingActionButton) this.findViewById(R.id.fab_nfc_read);
        fabNfcRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fabMenuWrite = (FloatingActionMenu) this.findViewById(R.id.fab_menu_write);
        fabMenuWrite.setVisibility(View.VISIBLE);

        FloatingActionButton fabNfcWrite = (FloatingActionButton) this.findViewById(R.id.fab_nfc_write);
        fabNfcWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment != null) {
                    ((OnSaveData) currentFragment).saveData();
                }
                NfcManager.getInstance().setTextMessage("pappoo");
                snackBar = Snackbar.make(MainActivity.this.findViewById(R.id.content_main), R.string.nfcWriteInstructions, Snackbar.LENGTH_LONG);
                snackBar.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fabMenuWrite.isOpened()) {
            fabMenuWrite.close(true);
        } else if (fabMenuLoad.isOpened()) {
            fabMenuLoad.close(true);
        }
        else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = null;

        if (id == R.id.nav_info) {
            f = MainFragment.newInstance("");
        } else if (id == R.id.nav_system) {
            f = ConfigFragment.newInstance("");
        }

        if (f != null) {
            currentFragment = f;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_main, f).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Object source, int action, Object par) {
            switch (action) {
                case LoginFragment.ENABLE_APP:
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    Menu navigationMenu = navigationView.getMenu();
                    navigationMenu.findItem(R.id.nav_info).setEnabled(true);
                    navigationMenu.findItem(R.id.nav_system).setEnabled(true);
                    //navigationMenu.findItem(R.id.nav_ethernet).setEnabled(true);
                    //navigationMenu.findItem(R.id.nav_licensing).setEnabled(true);
                    break;
                case LoginFragment.DISABLE_APP:
                    break;

                default:
                    break;
            }

    }



    @Override
    protected void onPause() {

        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


    }

    @Override
    public void onNewIntent(Intent intent) {

        Log.i("YMOBILE", "Tag discovered");
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        nfcManager.onTagDetected(context, tag, data);

        Toast.makeText(getApplicationContext(), "Tag detected", Toast.LENGTH_LONG).show();

    }

    public  void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[2];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("application/"+ getApplicationContext().getPackageName());
            //filters[0].addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        filters[1] = new IntentFilter();
        filters[1].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);


    }

    public  void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }



}
