package com.example.stevan.mosis;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import commands.Command;
import commands.ui.CommandShowToast;
import geo.GeoObj;

import de.hdodenhof.circleimageview.CircleImageView;
import gl.Color;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import system.ArActivity;
import system.DefaultARSetup;
import util.Vec;
import worldData.World;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int REQUEST_READ_PHONE_STATE = 0x2;
    protected static final int TAKE_PICTURE = 0x3;
    protected static final int MY_PERMISSIONS_REQUEST_CAMERA = 0x4;
    protected static final int REQUEST_PERMISSION_WRITE_STORAGE = 0x5;
    protected static final int REQUEST_PERMISSION_LOCATION = 0x6;
    final static int REQ_WIDTH = 640;
    final static int REQ_HEIGHT = 480;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    GoogleApiClient mGoogleClient;
    Location mLastKnownLocation;
    Marker currLocationMarker;
    Marker userLocationMarker;
    LocationManager locationManager;
    Criteria criteria;
    String provider;
    Location location;
    LatLng myPosition;
    LocationRequest mLocationRequest;
    private Menu mOptionsMenu;
    BluetoothAdapter mBluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;
    private ArrayList<User> usersArray;
    private UserAdapter userArrayAdapter;
    private ArrayList<MapObject> mapObjects;
    private MapObjectAdapter mapObjectAdapter;
    private Map<Marker, User> allMarkersMap = new HashMap<Marker, User>();
    private Map<Marker, MapObject> allObjectsMap = new HashMap<Marker, MapObject>();
    ProgressDialog dialog;

    Switch toggleNotifications;
    Intent service;

    Uri imageUri;
    String imageOfProblem;
    android.app.AlertDialog ecProblemAlert, searchDialog;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    protected final static int REQUEST_ENABLE_BT = 10;

    SharedPreferences prefs;

    //---------------------------------------------------------------------------------------------

    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "MYAPP";

    TelephonyManager tManager;

    private UUID MY_UUID_INSECURE;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;

    private BluetoothDevice mmDevice;

    private UUID deviceUUID;

    private ConnectedThread mConnectedThread;

    private boolean isThisDevice = false;

    private Integer userId;
    TimerTask timerTask;
    Timer timer;
    Integer markerPressedCount = 0;
    Boolean isServiceEnabled;
    Boolean isServiceStarted;
    ArrayList<Marker> markersToClear;

    ArrayList<GeoObj> arObjects;
    MapObject object1;

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            locationManager.requestLocationUpdates(provider, 400, 0, this);
        }

        isServiceStarted = prefs.getBoolean("isServiceStarted", true);
        if(isServiceStarted)
        {
            Log.i("Servis", "Gasim servis!");
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, BroadcastManager.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();
            stopService(service);
            prefs.edit().putBoolean("isServiceStarted", false).apply();
        }

        final String[] longitude = {prefs.getString("currentLon", "")};
        final String[] latitude = {prefs.getString("currentLat", "")};
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        longitude[0] = prefs.getString("currentLon", "");
                        latitude[0] = prefs.getString("currentLat", "");
                        new SetPlayerCordsTask().execute(String.valueOf(userId), longitude[0], latitude[0]);
                        //new GetUsersTask().execute(String.valueOf(userId), "friends");
                        new FindObjectsTask().execute(String.valueOf(userId), "radius", String.valueOf(50), String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                    }
                };

                timer = new Timer();
                Integer timePeriod = 30*1000;
                timer.schedule(timerTask, timePeriod, timePeriod);
            }
        }, 1500);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
        //timerTask.cancel();
        //timer.cancel();
        isServiceEnabled = prefs.getBoolean("isServiceEnabled", true);
        if(isServiceEnabled)
        {
            Log.i("Servis", "Palim servis!");
            //service = new Intent(this, BackgroundService.class);
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, BroadcastManager.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            startService(service);
            prefs.edit().putBoolean("isServiceStarted", true).apply();
            prefs.edit().putBoolean("isServiceEnabled", true).apply();
            //timer.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        setPlayerStatus();
        unregisterReceiver(mReceiver);
        //unregisterReceiver(receiver);
        //if(!mBluetoothAdapter.equals(null))
        //mBluetoothAdapter.disable();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        prefs = getSharedPreferences("Logging info", MODE_PRIVATE);
        service = new Intent(this, BackgroundService.class);


        //receiver = new BroadcastManager();
        //registerReceiver(receiver, new IntentFilter("com.example.stevan.mosis.FRIEND_OR_OBJECT_CLOSE"), null, null);

        usersArray = new ArrayList<User>();
        mapObjects = new ArrayList<MapObject>();
        arObjects = new ArrayList<GeoObj>();
        markersToClear = new ArrayList<Marker>();

        userId = prefs.getInt("userId", 0);
        new GetUsersTask().execute(String.valueOf(userId), "friends");
        toggleNotifications = (Switch) findViewById(R.id.notificationOnOff);
        isServiceEnabled = prefs.getBoolean("isServiceEnabled", true);
        if(isServiceEnabled) {
            toggleNotifications.setChecked(true);
        }
        else
        {
            toggleNotifications.setChecked(false);
        }

        MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        Log.i("MOJ UUID", MY_UUID_INSECURE.toString());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

                //Toast.makeText(this, "You must enable your location service!", Toast.LENGTH_LONG).show();
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
        }
        else
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("Permisije", "NIJE DOPUSTIO");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                //Toast.makeText(this, "NIJE DOPUSTIO123!", Toast.LENGTH_SHORT).show();

                //createLocationRequest();
            }
        }
        buildGoogleApiClient();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Toast.makeText(MainActivity.this, deviceName + " " + deviceHardwareAddress, Toast.LENGTH_SHORT).show();
                //ConnectThread connect = new ConnectThread(device);
                //connect.start();

                arrayAdapter.add(deviceName + "-" + deviceHardwareAddress);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFoundedDevices(arrayAdapter);

                    }
                }, 5000);
            }
        }
    };

    private void showFoundedDevices(final ArrayAdapter<String> array)
    {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.friends32p);
        builderSingle.setTitle("Choose your friend ");

        builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isThisDevice = true;
                String deviceInformation = array.getItem(which);
                String[] data = deviceInformation.split("-");
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(data[1]);
                startClient(device, MY_UUID_INSECURE);
                //ConnectThread connectThread = new ConnectThread(device);
                //connectThread.start();
                try {
                    Method method = device.getClass().getMethod("createBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                    registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                ArrayList<String> devices = new ArrayList<>();
                for (BluetoothDevice bt : pairedDevices) {
                    if(bt.getAddress() == device.getAddress())
                        Toast.makeText(MainActivity.this, bt.getName() + " " + bt.getAddress(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builderSingle.show();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("SAccepted","Permission is granted");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, TAKE_PICTURE);
                return true;
            } else {

                Log.v("SAccepted","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_STORAGE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("SAccepted","Permission is granted");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
            startActivityForResult(intent, TAKE_PICTURE);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_friend:
                //Toast.makeText(this, "Add friend selected!", Toast.LENGTH_SHORT).show();
                establishBluetoothConnection();
                return true;
            case R.id.view_friends:
                Integer userId1 = getIntent().getExtras().getInt("userId", 0);
                String id = String.valueOf(userId1);
                new GetUsersTask().execute(id, "friends");

                return true;
            case R.id.view_all:
                Integer userId2 = getIntent().getExtras().getInt("userId", 0);
                String idOfUser = String.valueOf(userId2);
                new GetUsersTask().execute(idOfUser, "all");
                return true;
            case R.id.add_object:
                isStoragePermissionGranted();
                return true;
            case R.id.rankings:
                Intent rankingsIntent = new Intent(MainActivity.this, Rankings.class);
                startActivity(rankingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(MainActivity.this, "Request code:" + String.valueOf(requestCode), Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            //Toast.makeText(MainActivity.this, "REQUEST_CHECK_SETTINGS", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                //Toast.makeText(MainActivity.this, "ACTIVITY RESULT:Sve je okej", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }

        if(requestCode == REQUEST_ENABLE_BT)
            if(resultCode == RESULT_OK)
            {
                start();
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                boolean isStarted = mBluetoothAdapter.startDiscovery();
                if(isStarted)
                    Toast.makeText(this, "Discovery started..", Toast.LENGTH_SHORT).show();

            }
        if(requestCode == TAKE_PICTURE)
            if(resultCode == RESULT_OK)
            {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View problemView = inflater.inflate(R.layout.object_adding, null);

                final String[] typeOfProblem = {""}; // mali, srednji ili veliki

                final EditText ecProblemDesc = (EditText) problemView.findViewById(R.id.ecProblemDesc);
                final RadioGroup ecProblemType = (RadioGroup) problemView.findViewById(R.id.ecProblemType);
                final RadioButton optionSmall = (RadioButton) problemView.findViewById(R.id.typeProblemSmall);
                final RadioButton optionMedium = (RadioButton) problemView.findViewById(R.id.typeProblemMedium);
                final RadioButton optionLarge = (RadioButton) problemView.findViewById(R.id.typeProblemLarge);
                Button ecSubmitProblem = (Button) problemView.findViewById(R.id.ecSubmitProblem);

                ecSubmitProblem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String descriptionOfProblem = ecProblemDesc.getText().toString();

                        if(descriptionOfProblem.equals(""))
                        {
                            Toast.makeText(MainActivity.this, "You must enter description of problem!", Toast.LENGTH_SHORT).show();
                        }
                        else if (ecProblemType.getCheckedRadioButtonId() == -1)
                        {
                            // no radio buttons are checked
                            Toast.makeText(MainActivity.this, "You must select type of problem!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // one of the radio buttons is checked
                            if(optionSmall.isChecked())
                                typeOfProblem[0] = "mali";
                            if(optionMedium.isChecked())
                                typeOfProblem[0] = "srednji";
                            if(optionLarge.isChecked())
                                typeOfProblem[0] = "veliki";

                            Integer userId = getIntent().getExtras().getInt("userId", 0);
                            String id = String.valueOf(userId);
                            new AddObjectTask().execute(id, descriptionOfProblem, imageOfProblem, typeOfProblem[0], String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                        }

                    }
                });

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

                builder.setView(problemView);
                ecProblemAlert = builder.create();
                ecProblemAlert.setCanceledOnTouchOutside(true);

                ecProblemAlert.show();
                Uri selectedImage = imageUri;
                getContentResolver().notifyChange(selectedImage, null);
                ImageView ecProblemPic = (ImageView) problemView.findViewById(R.id.ecProblemPic);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);

                    ecProblemPic.setImageBitmap(bitmap);
                    imageOfProblem = encodeToBase64(bitmap, Bitmap.CompressFormat.WEBP, 75);

                    //Toast.makeText(this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        userArrayAdapter = new UserAdapter(this, usersArray, mMap);

        // Add a marker in Sydney, Australia, and move the camera.
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("Permisije", "NIJE DOPUSTIO");

            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            //Toast.makeText(this, "NIJE DOPUSTIO123!", Toast.LENGTH_SHORT).show();

            //createLocationRequest();
        } else {
            Log.i("Permisije", "DOPUSTIO");
            //Toast.makeText(this, "DOPUSTIO123!", Toast.LENGTH_SHORT).show();
            buildGoogleApiClient();
            mGoogleClient.connect();
            createLocationRequest();
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //Poziva ga za API <= 22

        if(currLocationMarker != null)
            currLocationMarker.remove();

        myPosition = new LatLng(location.getLatitude(), location.getLongitude());
        prefs.edit().putString("currentLon", String.valueOf(location.getLongitude())).apply();
        prefs.edit().putString("currentLat", String.valueOf(location.getLatitude())).apply();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myPosition);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mMap.addMarker(markerOptions);

        //zoom to current position:
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,10));
        //Toast.makeText(this, "Location changed!", Toast.LENGTH_SHORT).show();

        // Upis trenutne lokacije korisnika u bazu
        //setPlayerCords((float)location.getLongitude(), (float)location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    protected void createLocationRequest() {
        Log.i("Funkcija", "usao sam u createLocationRequest");
        //Toast.makeText(this, "createLocationRequest!", Toast.LENGTH_SHORT).show();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient,
                        builder.build());


        Log.i("Funkcija", "Podesavam callback");

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                Log.i("Status", status.getStatusMessage());
                //Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.i("SUCCESS", "Sva podesavanja ispunjena!");
                        //Toast.makeText(MainActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                                checkLocationPermission();
                            }
                            return;
                        }
                        //Toast.makeText(MainActivity.this, "onResult:SUCCESS!", Toast.LENGTH_SHORT).show();
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.i("ERROR", "Trazimo da se podese podesavanja!");
                            //Toast.makeText(MainActivity.this, "ERROR: Trazimo da se podese podesavanja!", Toast.LENGTH_SHORT).show();
                            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
                                status.startResolutionForResult(
                                        MainActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        //Toast.makeText(MainActivity.this, "UNAVAILABLE: Ne mogu da se podese podesavanja!", Toast.LENGTH_SHORT).show();
                        Log.i("UNAVAILABLE", "Ne mogu da se podese podesavanja!");
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        if(mLastLocation != null)
        {
            Log.i("Location test", "11233");
            myPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            new FindObjectsTask().execute(String.valueOf(userId), "radius", String.valueOf(50), String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myPosition);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        // Location update za API >= 23

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                prefs.edit().putString("currentLon", String.valueOf(location.getLongitude())).apply();
                prefs.edit().putString("currentLat", String.valueOf(location.getLatitude())).apply();
                //setPlayerCords((float)location.getLongitude(),(float) location.getLatitude());
            }
        });
        //Toast.makeText(this, "ON CONNECTED!", Toast.LENGTH_SHORT).show();

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }

                return;

            }
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO

                }
                break;
            case REQUEST_PERMISSION_WRITE_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photo));
                    imageUri = Uri.fromFile(photo);
                    startActivityForResult(intent, TAKE_PICTURE);
                }
            case REQUEST_PERMISSION_LOCATION:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                    Log.i("OMOGUCIO", "DA");
                    checkLocationPermission();
                    buildGoogleApiClient();
                    mGoogleClient.connect();

                }
            default:
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void makeAlertForAdding(String nameOfUser)
    {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
        builderInner.setMessage(nameOfUser);
        builderInner.setTitle("You are now friend with ");
        builderInner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });
        builderInner.show();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromString(String input, int reqWidth, int reqHeight) {

        byte[] decodedBytes = Base64.decode(input, 0);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);
    }

    public class SetPlayerCordsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/set_player_cords.php";

            final String userId = strings[0];
            final String longitude = strings[1];
            final String latitude = strings[2];

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Set cords response", response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", userId);
                        hashMap.put("lon", longitude);
                        hashMap.put("lat", latitude);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    // Upisujemo u bazu status korisnika

    protected void setPlayerStatus() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int userIdInt = extras.getInt("userId");

            String urlAddress = "http://qosstamen.esy.es/Server/set_player_status.php";

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Response for status", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("id", String.valueOf(userIdInt));
                    hashMap.put("status", "offline");

                    return hashMap;
                }
            };
            requestQueue.add(request);
        }
    }

    protected void addFriend(int userId1, int userId2) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            final String id1 = String.valueOf(userId1);
            final String id2 = String.valueOf(userId2);

            String urlAddress = "http://qosstamen.esy.es/Server/add_friend.php";

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Friends response", response);

                    if(response.length() == 1)
                    {
                        if(response.equals("5"))
                            Toast.makeText(MainActivity.this, "You are already friend with this user!", Toast.LENGTH_SHORT).show();
                    }
                    else if(response.length() > 1)
                    {
                        String[] separated = response.split(" ");
                        if(separated[0].equals("1"))
                        {
                            makeAlertForAdding(separated[1]);
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Error, try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("id1", id1);
                    hashMap.put("id2", id2);

                    return hashMap;
                }
            };
            requestQueue.add(request);
        }
    }

    protected void getUserName(final int userId, final int secondUserId) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            final String id1 = String.valueOf(userId);

            String urlAddress = "http://qosstamen.esy.es/Server/get_user_name.php";

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("User name response", response);

                    if(response.length() > 2 && isThisDevice)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(R.drawable.friends32p);
                        builder.setTitle("Friend request");
                        builder.setMessage("You have request from " + response);
                        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                addFriend(userId, secondUserId);
                            }
                        });
                        builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Error", error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("id1", id1);

                    return hashMap;
                }
            };
            requestQueue.add(request);
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }

    protected boolean establishBluetoothConnection()
    {
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(MainActivity.this, "Your device doesn't support Bluetooth!", Toast.LENGTH_SHORT).show();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        return true;
    }

    public void OnSearchClick(View view)
    {

        final boolean[] isAnythingChecked = {false};

        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        final View searchView = inflater.inflate(R.layout.searching_layout, null);

        final RadioButton typeSearch = (RadioButton) searchView.findViewById(R.id.typeRadio);
        final RadioButton radiusSearch = (RadioButton) searchView.findViewById(R.id.radiusRadio);

        final RadioButton optionSmall = (RadioButton) searchView.findViewById(R.id.typeProblemSmall);
        optionSmall.setEnabled(false);
        final RadioButton optionMedium = (RadioButton) searchView.findViewById(R.id.typeProblemMedium);
        optionMedium.setEnabled(false);
        final RadioButton optionLarge = (RadioButton) searchView.findViewById(R.id.typeProblemLarge);
        optionLarge.setEnabled(false);

        final EditText searchMeters = (EditText) searchView.findViewById(R.id.metersForSearch);
        searchMeters.setEnabled(false);
        Button searchButton = (Button) searchView.findViewById(R.id.searchButton);

        final RadioGroup radioGroup = (RadioGroup) searchView.findViewById(R.id.searchToolsGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                View radioButton = group.findViewById(checkedId);
                int index = group.indexOfChild(radioButton);
                isAnythingChecked[0] = true;

                // Add logic here

                switch (index) {
                    case 0: // first button

                        //Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_SHORT).show();

                        searchMeters.setEnabled(false);
                        optionSmall.setEnabled(true);
                        optionMedium.setEnabled(true);
                        optionLarge.setEnabled(true);


                        break;
                    case 1: // secondbutton

                        //Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_SHORT).show();

                        optionSmall.setEnabled(false);
                        optionMedium.setEnabled(false);
                        optionLarge.setEnabled(false);
                        searchMeters.setEnabled(true);

                        break;
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer userId = getIntent().getExtras().getInt("userId", 0);
                String id = String.valueOf(userId);

                if(isAnythingChecked[0])    // odabrano je nesto od filtera
                {
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    View radioButton = radioGroup.findViewById(radioButtonId);
                    Integer index = radioGroup.indexOfChild(radioButton);


                    if(index.equals(0)) // type search
                    {
                        if(optionSmall.isChecked())
                        {
                            new FindObjectsTask().execute(id, "type", "mali", String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                        }
                        if(optionMedium.isChecked())
                        {
                            new FindObjectsTask().execute(id, "type", "srednji", String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                        }
                        if(optionLarge.isChecked())
                        {
                            new FindObjectsTask().execute(id, "type", "veliki", String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                        }


                    }
                    else    // radius search
                    {
                        String radiusMeters = searchMeters.getText().toString();
                        new FindObjectsTask().execute(id, "radius", radiusMeters, String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                    }
                }
                else    // pretrazuju se svi objekti
                {

                    new FindObjectsTask().execute(id, "all", "nebitno", String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                }


            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setView(searchView);
        searchDialog = builder.create();
        searchDialog.setCanceledOnTouchOutside(true);

        searchDialog.show();

    }

    public void OnNotificationsToggle(View view)
    {
        if(toggleNotifications.isChecked())
        {
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, BroadcastManager.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            startService(service);
            prefs.edit().putBoolean("isServiceStarted", true).apply();
            prefs.edit().putBoolean("isServiceEnabled", true).apply();
            timer.cancel();
        }
        else
        {
            PackageManager pm  = this.getPackageManager();
            ComponentName componentName = new ComponentName(this, BroadcastManager.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            //Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show();
            stopService(service);
            prefs.edit().putBoolean("isServiceStarted", false).apply();
            prefs.edit().putBoolean("isServiceEnabled", false).apply();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    String longitude = prefs.getString("currentLon", "");
                    String latitude = prefs.getString("currentLat", "");
                    new SetPlayerCordsTask().execute(String.valueOf(userId), longitude, latitude);
                }
            };

            timer = new Timer();
            Integer timePeriod = 30*1000;
            timer.schedule(timerTask, timePeriod, timePeriod);
        }
        //prefs.edit().putBoolean("isService")
    }

    UUID uniqueId = UUID.randomUUID();

    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {

                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "BEGIN mAcceptThread " + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (true) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    Log.i("Connection coming", "Accepted");
                    synchronized (MainActivity.this) {

                        // Situation normal. Start the connected thread.
                        connected(socket);

                        // Either not ready or already connected. Terminate new socket.
                        /*try {
                            socket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Could not close unwanted socket", e);
                        }*/

                    }
                }
            }
            Log.i(TAG, "END mAcceptThread");

        }

        public void cancel() {
            Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {

                tmp = mmDevice.createRfcommSocketToServiceRecord(
                        MY_UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    Log.i(TAG, e.toString());
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (MainActivity.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            try {
                connected(mmSocket);
            }
            catch (Exception e) {
                Log.i("Connected: ", e.toString());
            }

        }

        public void cancel() {
            try {
                Log.i(TAG, "Gasimo socket ConnectThread");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        /*if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }*/

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        /*if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }*/

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Integer userId = getIntent().getExtras().getInt("userId", 0);
        String id = String.valueOf(userId);
        Log.i(TAG, "USER ID " + id);
        //byte[] bytes = id.getBytes("UTF-8");
        if(userId > 0)
        {
            byte[] bytes;
            bytes = id.getBytes();
            mConnectedThread.write(bytes);
            Log.i(TAG, "Slanje podataka!");
        }


        // Send the name of the connected device back to the UI Activity
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[256];
            int bytes;
            final Integer thisUserId = getIntent().getExtras().getInt("userId");
            Integer readedUserId = 0;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String readMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received [" + readMessage + "]");
                    readedUserId = Integer.parseInt(readMessage);
                    final Integer finalReadedUserId = readedUserId;
                    getUserName(finalReadedUserId, thisUserId);


                    // Send the obtained bytes to the UI Activity

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public class GetUsersTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/return_players.php";

            final String id = strings[0];
            Log.i("User id", id);
            final String type = strings[1]; // Moze biti all ako izvlacimo sve korisnike ili friends ako trazimo samo prijatelje

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Users response", response);

                        try {
                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                            View subView = inflater.inflate(R.layout.thumbnail, null);
                            CircleImageView userProfilePicture = (CircleImageView) subView.findViewById(R.id.userProfilePicture);

                            JSONArray jsonArray = new JSONArray(response);
                            usersArray.clear();
                            mMap.clear();
                            allMarkersMap.clear();
                            //userArrayAdapter = new UserAdapter(MainActivity.this, usersArray, mMap);
                            // Attach the adapter to a ListView
                            //allMyPubsListView.setAdapter(allMyPubsAdapter);
                            //Za svaki izvuceni ID izvuci sve objave
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                final String userName = jsonObject.getString("Username");
                                final String emailAddress = jsonObject.getString("Email");
                                final String firstName = jsonObject.getString("FirstName");
                                final String lastName = jsonObject.getString("LastName");
                                final String profilePicture = jsonObject.getString("Avatar");
                                final String phoneNumber = jsonObject.getString("PhoneNumber");
                                final Double longitude = jsonObject.getDouble("Longitude");
                                final Double latitude = jsonObject.getDouble("Latitude");

                                final Bitmap bitmap1 = decodeSampledBitmapFromString(profilePicture, REQ_WIDTH, REQ_HEIGHT);

                                final User user = new User(userName, emailAddress, firstName, lastName, bitmap1, phoneNumber, longitude, latitude);

                                usersArray.add(user);
                                if(type.equals("friends"))
                                {
                                    userProfilePicture.setImageBitmap(bitmap1);
                                    BitmapDrawable drawable = (BitmapDrawable) userProfilePicture.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(user.getLatitude(), user.getLongitude()))
                                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MainActivity.this, subView)));

                                    Marker marker = mMap.addMarker(markerOptions);

                                    allMarkersMap.put(marker, user);

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {

                                            User user = allMarkersMap.get(marker);
                                            if(!user.equals(null))
                                            {
                                                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MainActivity.this, user.getFirstName() + " " + user.getLastName(), user.getEmailAddress(), user.getPhoneNumber()));
                                                marker.showInfoWindow();
                                                return true;
                                            }

                                            return false;
                                        }
                                    });
                                }
                                else
                                {
                                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(user.getLatitude(), user.getLongitude()));

                                    Marker marker = mMap.addMarker(markerOptions);
                                    allMarkersMap.put(marker, user);

                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {

                                            try {
                                                User user = allMarkersMap.get(marker);
                                                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MainActivity.this, user.getFirstName() + " " + user.getLastName(), "", ""));
                                                marker.showInfoWindow();
                                                return true;
                                            }
                                            catch (NullPointerException npe)
                                            {
                                                Log.i("Null Exception", npe.toString());
                                            }

                                            return false;
                                        }
                                    });

                                }
                            }
                            userArrayAdapter.notifyDataSetChanged();
                            //dialog.dismiss();

                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", id);
                        hashMap.put("type", type);

                        return hashMap;
                    }
                };

                request.setRetryPolicy(new DefaultRetryPolicy(
                        200,
                        10,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    public class AddObjectTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/input_object.php";

            final String id = strings[0];
            final String description = strings[1];
            final String picture = strings[2];
            final String type = strings[3];
            final String lon = strings[4];
            final String lat = strings[5];
            Log.i("User id", id);

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Adding object response", response);

                        try {

                            if(response.equals("1")) {
                                ecProblemAlert.dismiss();
                                Toast.makeText(MainActivity.this, "Successfully reported problem!", Toast.LENGTH_SHORT).show();
                            }
                            else if(response.equals("-1"))
                                Toast.makeText(MainActivity.this, "Error with database!", Toast.LENGTH_SHORT).show();
                            else if(response.equals("-2"))
                                Toast.makeText(MainActivity.this, "Some problem occurred with sending data!", Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", id);
                        hashMap.put("description", description);
                        hashMap.put("picture", picture);
                        hashMap.put("type", type);
                        hashMap.put("lon", lon);
                        hashMap.put("lat", lat);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    public class FindObjectsTask extends AsyncTask<String, Void, String> {

        boolean test = false;

        @Override
        protected void onPreExecute() {

            /*dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();*/

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/return_object.php";

            final String id = strings[0];
            final String type_search = strings[1];
            final String value_search = strings[2];
            final String lon = strings[3];
            final String lat = strings[4];

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Find objects response", response);

                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            mapObjects.clear();
                            for (Marker marker : markersToClear) {
                                marker.remove();
                            }
                            markersToClear.clear();
                            //mMap.clear();
                            allObjectsMap.clear();
                            arObjects.clear();
                            if(jsonArray.length() > 0)
                                test = true;
                            //Za svaki izvuceni ID izvuci sve objave
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                final String description = jsonObject.getString("Description");
                                final String image = jsonObject.getString("Image");
                                final String type = jsonObject.getString("Type");
                                final Double longitude = jsonObject.getDouble("Longitude");
                                final Double latitude = jsonObject.getDouble("Latitude");

                                final MapObject mapObject = new MapObject(description, image, type, longitude, latitude);

                                mapObjects.add(mapObject);

                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude));

                                final GeoObj object = new GeoObj(latitude, longitude);
                                arObjects.add(object);

                                if(type.equals("mali"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_small_40p));
                                else if(type.equals("srednji"))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.recycle_bin_40p));
                                else
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.garbage_truck_40p));

                                Marker marker = mMap.addMarker(markerOptions);
                                markersToClear.add(marker);

                                allObjectsMap.put(marker, mapObject);

                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(final Marker marker) {

                                        markerPressedCount++;
                                        if (markerPressedCount > 1)
                                        {
                                            markerPressedCount = 0;
                                            if(test)
                                            {
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                builder.setTitle("Clean the trash");
                                                builder.setMessage("Do you want to clean trash for points?");
                                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                                                != PackageManager.PERMISSION_GRANTED)
                                                        {
                                                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                                                    Manifest.permission.CAMERA))
                                                            {

                                                            }
                                                            else {
                                                                ActivityCompat.requestPermissions(MainActivity.this,
                                                                        new String[]{Manifest.permission.CAMERA},
                                                                        MY_PERMISSIONS_REQUEST_CAMERA);
                                                            }
                                                        }
                                                        else
                                                        {

                                                            // Ako je omogucena kamera pokrece se AR sistem

                                                            ArActivity.startWithSetup(MainActivity.this, new DefaultARSetup() {

                                                                @Override
                                                                public void _b_addWorldsToRenderer(GL1Renderer renderer, GLFactory objectFactory, final GeoObj currentPosition) {

                                                                    final MeshComponent meshComponent = GLFactory.getInstance().newDiamond(null);

                                                                    meshComponent.setOnLongClickCommand(new Command() {
                                                                        @Override
                                                                        public boolean execute() {
                                                                            Log.i("OBJEKAT", object1.toString());
                                                                            if(object1.getType().equals("mali"))
                                                                            {
                                                                                CommandShowToast.show(MainActivity.this, "You earned 10 points!");
                                                                                new InputUsersPoints().execute(String.valueOf(userId), "10", String.valueOf(object1.getLon()), String.valueOf(object1.getLat()));

                                                                            }
                                                                            else if(object1.getType().equals("srednji"))
                                                                            {
                                                                                CommandShowToast.show(MainActivity.this, "You earned 20 points!");
                                                                                new InputUsersPoints().execute(String.valueOf(userId), "20", String.valueOf(object1.getLon()), String.valueOf(object1.getLat()));


                                                                            }
                                                                            else if(object1.getType().equals("veliki"))
                                                                            {
                                                                                CommandShowToast.show(MainActivity.this, "You earned 30 points!");
                                                                                new InputUsersPoints().execute(String.valueOf(userId), "30", String.valueOf(object1.getLon()), String.valueOf(object1.getLat()));
                                                                                markersToClear.add(marker);
                                                                            }

                                                                            // Podesiti da se objekat izgubi i prikazati osvajanje poena
                                                                            meshComponent.setScale(new Vec(0,0,0));

                                                                            return true;
                                                                        }
                                                                    });

                                                                    for(int i = 0; i < arObjects.size(); i++)
                                                                    {
                                                                        spawnObj(arObjects.get(i), meshComponent);

                                                                    }

                                                                    renderer.addRenderElement(world);

                                                                }

                                                                @Override
                                                                public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
                                                                    // addSpawnButtonToUI("Spawn Object", guiSetup);
                                                                }

                                                                @Override
                                                                public void addObjectsTo(GL1Renderer renderer, World world, GLFactory objectFactory) {
                                                                }

                                                                private void spawnObj(MeshComponent mesh){
                                                                    // Vec pos = camera.getGPSPositionVec();
                                                                    Vec pos = getCamera().getGPSPositionVec();

                                                                    Log.d("Placing tag", "Placing object at " + pos);
                                                                    GeoObj x = new GeoObj(pos.y, pos.x, pos.z);

                                                                    mesh.setPosition(new Vec(0,1,0));
                                                                    x.setComp(mesh);
                                                                    //CommandShowToast.show(myTargetActivity, "Object spawned" + x.getMySurroundGroup().getPosition());
                                                                    world.add(x);
                                                                }

                                                                private void spawnObj(final GeoObj pos, MeshComponent mesh) {
                                                                    GeoObj x = new GeoObj(pos);

                                                                    mesh.setPosition(Vec.getNewRandomPosInXYPlane(new Vec(), 0.1f, 1f));
                                                                    x.setComp(mesh);
                                                                    //CommandShowToast.show(myTargetActivity, "Object spawned at " + x.getMySurroundGroup().getPosition());
                                                                    world.add(x);
                                                                }

                                                            });
                                                        }
                                                    }
                                                });
                                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                            }
                                        }
                                        else {

                                            object1 = allObjectsMap.get(marker);
                                            if(!object1.equals(null))
                                            {
                                                final Bitmap bitmap = decodeSampledBitmapFromString(object1.getImage(), 75, 75);
                                                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(MainActivity.this, object1.getDescription(), bitmap, object1.getType(), String.valueOf(object1.getLon()), String.valueOf(object1.getLat())));
                                                marker.showInfoWindow();
                                                return true;
                                            }
                                        }

                                        return false;
                                    }
                                });

                            }
                            //dialog.dismiss();
                            searchDialog.dismiss();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", id);
                        hashMap.put("type_search", type_search);
                        hashMap.put("value_search", value_search);
                        hashMap.put("lon", lon);
                        hashMap.put("lat", lat);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }

    public class InputUsersPoints extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String urlAddress = "http://qosstamen.esy.es/Server/input_points.php";

            final String id = strings[0];
            final String points = strings[1];
            final String lon = strings[2];
            final String lat = strings[3];

            try {

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest request = new StringRequest(StringRequest.Method.POST, urlAddress, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Input points response", response);

                        new FindObjectsTask().execute(id, "radius", String.valueOf(50), String.valueOf(myPosition.longitude), String.valueOf(myPosition.latitude));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("id", id);
                        hashMap.put("points", points);
                        hashMap.put("lon", lon);
                        hashMap.put("lat", lat);

                        return hashMap;
                    }
                };

                requestQueue.add(request);

            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String aVoid) {

        }
    }
}
