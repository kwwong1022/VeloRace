package scm.kaifwong8.velorace;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RideActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "RideActivity";
    private static final float ALPHA = 0.8f;
    public static final int CALCULATION_UPDATE_INTERVAL = 100;
    public static final int DEFAULT_UPDATE_INTERVAL = 300;
    public static final int FAST_UPDATE_INTERVAL = 300;
    public static final int LOCATION_PERMISSION_REQUEST = 1;

    private TextView tv_time;
    private TextView tv_speed;
    private TextView tv_rideTime;
    private FloatingActionButton btn_lock;
    private FloatingActionButton btn_flash;
    private FloatingActionButton btn_play;
    private BalanceView balanceView;
    private SpeedGraphView speedGraphView;
    private BalanceGraphView balanceGraphView;
    private ResultView resultView;
    // location
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location userLocation;
    private String speed;
    // google map
    private GoogleMap mMap;
    // sensor
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private float movement = 0;
    private float vax = 0;
    private float ax = 0;
    private float gx = 0;
    private float heading = 0;
    private float lightLevel = 0;
    private float averagedLightLevel = 9999;
    private float averagedGx = 0;
    private ArrayList<Float> lightLevelArr = new ArrayList<>();
    private ArrayList<Float> balanceArr = new ArrayList<>();
    private Sensor sensor_a;
    private Sensor sensor_m;
    private Sensor sensor_p;
    private Sensor sensor_l;
    private boolean isFall = false;
    private boolean firstFall = true;
    private boolean aReady = false;
    private boolean mReady = false;
    private float[] values_a = new float[3];
    private float[] values_m = new float[3];
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == sensor_a) {
                vax = event.values[0];
                values_a = event.values.clone();
                aReady = true;

                if (isRidePaused) {
                    balanceView.update(0);
                } else {
                    balanceView.update(gx);
                }
            }
            if (event.sensor == sensor_m) {
                values_m = event.values.clone();
                mReady = true;
            }
            if (event.sensor == sensor_p) {
                if (isSwipeFlashEnabled) {
                    if (event.values[0]>0) toggleFlash();
                }
                if (averagedLightLevel < 55) {
                    if (event.values[0]>0) toggleFlash();
                }
            }
            if (event.sensor == sensor_l) {
                lightLevel = event.values[0];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    // date & time
    private Timer secTimer;
    private Timer rideTimer;
    private String distance;
    private boolean isRidePaused = true;
    private int rideSecond = 0;
    // tool
    private CameraManager cameraManager;
    private boolean flashAvailable;
    private boolean isFlashOn = false;
    private boolean isLocked = false;
    // record var
    private String maxSpd = "0";
    private String maxElev = "0";
    private String avgSpd;
    private float elev = 0;
    private float currSpeed = 0;
    private float totalSpd;
    private int stopVal = 0;
    private int moveVal = 0;
    private boolean autoStart = false;
    private boolean autoPause = false;
    private boolean autoStarted = false;

    // setting
    private String phoneNumber;
    private boolean isAutoStartEnabled;
    private boolean isSwipeFlashEnabled;
    private boolean isFallDetectionEnabled;

    // result
    private ConstraintLayout resultContainer;
    private ConstraintLayout rideResultContainer;
    private ConstraintLayout calResultContainer;
    private ArrayList<RideRecord> rideRecords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // flag & contentView
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            setContentView(R.layout.activity_ride);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        // setting preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settingPreferences", MODE_PRIVATE);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                isAutoStartEnabled = sharedPreferences.getBoolean("AUTO_START", false);
                isSwipeFlashEnabled = sharedPreferences.getBoolean("SWIPE_FLASH", false);
                isFallDetectionEnabled = sharedPreferences.getBoolean("FALL_DETECTION", false);
                phoneNumber = sharedPreferences.getString("phoneNumber", "");
                Log.d(TAG, "auto_start: " + sharedPreferences.getBoolean("AUTO_START", false));
                Log.d(TAG, "swipe_flash: " + sharedPreferences.getBoolean("SWIPE_FLASH", false));
                Log.d(TAG, "fall_detection: " + sharedPreferences.getBoolean("FALL_DETECTION", false));
                Log.d(TAG, "phone number: " + sharedPreferences.getString("PHONE_NUMBER", ""));
            }
        }, 100);

        // init view
        tv_time = findViewById(R.id.txt_currTime);
        tv_rideTime = findViewById(R.id.txt_ride_time);
        tv_speed = findViewById(R.id.txt_speed);
        TextView tv_date = findViewById(R.id.txt_today);
        TextView tv_distance = findViewById(R.id.txt_distance);
        TextView tv_avgSpd = findViewById(R.id.txt_average_speed);
        TextView tv_maxSpd = findViewById(R.id.txt_max_speed);
        ImageView btn_exit = findViewById(R.id.btn_back);
        btn_lock = findViewById(R.id.btn_lock);
        btn_flash = findViewById(R.id.btn_flash);
        btn_play = findViewById(R.id.btn_play);

        /**
         * Code modified from function addViewUseLayoutParams(), found at:
         * https://www.jianshu.com/p/16e34f919e1a
         * */
        // init tool customView
        ConstraintLayout balanceViewContainer = findViewById(R.id.balance_view_container);
        this.balanceView = new BalanceView(this);
        balanceViewContainer.addView(balanceView);
        ConstraintLayout speedGraphContainer = findViewById(R.id.speed_graph_container);
        this.speedGraphView = new SpeedGraphView(this);
        speedGraphContainer.addView(speedGraphView);
        ConstraintLayout balanceGraphContainer = findViewById(R.id.balance_graph_container);
        this.balanceGraphView = new BalanceGraphView(this);
        balanceGraphContainer.addView(balanceGraphView);
        resultContainer = findViewById(R.id.result_container);
        rideResultContainer = findViewById(R.id.ride_result_graph_container);
        this.resultView = new ResultView(this);
        rideResultContainer.addView(resultView);

        btn_exit.setOnClickListener(v -> {
            if (rideSecond==0) finish();

            if (rideSecond!=0 && !isLocked) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to finish this ride?")
                        .setPositiveButton("Yes", (dialog, which) -> showResult())
                        .setNegativeButton("No", (dialog, which) -> isRidePaused = false)
                        .show();
            }
        });

        /**
         * Code modified from function onCreate(), found at:
         * https://www.youtube.com/watch?v=_xUcYfbtfsI&t=1754s
         * */
        // location settings
        locationRequest = new LocationRequest();
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // location update
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                updateMap(locationResult.getLocations().get(0));
                updateLocation(locationResult);
                updateSpeed(locationResult.getLastLocation());
                updateAltitude(locationResult.getLastLocation());
                RideActivity.this.currSpeed = locationResult.getLocations().get(0).hasSpeed()? locationResult.getLocations().get(0).getSpeed():0.0f;
            }
        };
        // location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(RideActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);

        // sensor
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensor_a = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor_a == null) {
            Log.e(TAG, "onCreateView: accelerometer not detected");
        }
        sensor_m = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (sensor_m == null) {
            Log.e(TAG, "onCreateView: magnetic field sensor not detected");
        }
        sensor_p = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (sensor_p == null) {
            Log.e(TAG, "onCreateView: proximity sensor not detected");
        }
        sensor_l = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (sensor_l == null) {
            Log.e(TAG, "onCreateView: light sensor not detected");
        }

        // date & time
        Date time = Calendar.getInstance().getTime();
        List<String> dateList = Arrays.asList(DateFormat.getDateInstance(DateFormat.FULL).format(time).split(","));
//        Log.d(TAG, "onCreate: " + Locale.getDefault().getLanguage());
        if (Locale.getDefault().getLanguage() == "en") {
            // for eng
            tv_date.setText(dateList.get(1).substring(0, dateList.get(1).length()-4).trim());
        } else {
            // for cn
            tv_date.setText(dateList.get(0).trim());
        }

        // lock
        btn_lock.setOnClickListener(v -> {
            if (isLocked) Toast.makeText(RideActivity.this, "Long press to unlock", Toast.LENGTH_SHORT).show();
            if (!isLocked) {
                isLocked = true;
                Toast.makeText(RideActivity.this, "Screen locked", Toast.LENGTH_SHORT).show();
                btn_lock.setImageResource(R.drawable.ic_baseline_lock_open_24);
            }
        });
        btn_lock.setOnLongClickListener(v -> {
            if (isLocked) isLocked = false;
            btn_lock.setImageResource(R.drawable.ic_baseline_lock_24);
            Toast.makeText(RideActivity.this, "Unlocked", Toast.LENGTH_SHORT).show();
            return true;
        });
        // ride time
        btn_play.setOnClickListener(v -> {
            if (!isLocked) {
                isRidePaused = !isRidePaused;
                if (!isRidePaused) {
                    findViewById(R.id.map_container).setBackgroundColor(Color.rgb(145, 233, 255));
                    btn_play.setImageResource(R.drawable.ic_baseline_pause_24);

                    if (rideSecond == 0 && !autoStart && !autoPause) {
                        autoStart = autoPause = true;
                    }
                    if (rideSecond != 0 && autoStart == false) {
                        autoStart = true;
                    }

                } else {
                    findViewById(R.id.map_container).setBackgroundColor(Color.rgb(255, 145, 145));
                    btn_play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    btn_play.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 191, 145)));

                    if (autoStarted) {
                        autoStart = false;
                    }
                }
            }
        });
        tv_rideTime.setText(R.string.default_ride_time);
        rideTimer = new Timer();
        TimerTask rideTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRidePaused) {
                            rideSecond++;
                            RideActivity.this.tv_rideTime.setText(getRideTime());
                            if (speed != null) totalSpd += Float.valueOf(speed);
                            avgSpd = Double.toString((Math.round((totalSpd/rideSecond)*10)/10.d));
                            tv_avgSpd.setText(avgSpd);
                            distance = Double.toString((Math.round((totalSpd/rideSecond/3600) * rideSecond*10)/10.d));
                            tv_distance.setText(distance + " km");
                            if (speed != null) maxSpd =  Float.valueOf(speed)>Float.valueOf(maxSpd)? speed:maxSpd;
                            tv_maxSpd.setText(maxSpd);
                            speedGraphView.update((float) (currSpeed*3.6));
                        }
                    }
                });
            }
        };
        rideTimer.scheduleAtFixedRate(rideTimerTask, 0, 1000);

        // flash light
        flashAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        btn_flash.setOnClickListener(v -> { if (!isLocked) toggleFlash(); });

        // calculation
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // heading
                if (aReady && mReady) {
                    float[] values = new float[3];
                    float[] R = new float[9];
                    SensorManager.getRotationMatrix(R, null, values_a, values_m);
                    SensorManager.getOrientation(R, values);

                    heading = (float) Math.toDegrees(values[0])+90;   // -90 for testing device: landscape location
                    aReady = mReady = false;
                }

                // balance
                gx = ALPHA * gx + (1 - ALPHA) * values_a[1];
                ax = ALPHA * ax + (1 - ALPHA) * values_a[0];
                movement = vax - ax;

                if (!isRidePaused) balanceGraphView.update(gx);
            }
        }, CALCULATION_UPDATE_INTERVAL, CALCULATION_UPDATE_INTERVAL);

        // second timer : always on
        secTimer = new Timer();
        secTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // time
                String[] sp = Calendar.getInstance().getTime().toString().split("\\s+");
                List<String> times = Arrays.asList(sp[3].split(":"));
                int temp = Integer.parseInt(times.get(0))%12;
                String hr = temp<10? "0"+temp:String.valueOf(temp);

//                Log.d(TAG, "ax: " + movement + ", ay: " + values_a[1] + ", az: " + values_a[2]);

                // autoStart processing
                if (autoStart && isAutoStartEnabled) {
                    if (isRidePaused && movement>1 || movement<-1) moveVal++;
                    Log.d(TAG, "moveVal: " + moveVal);
                    if (moveVal>5) {
                        isRidePaused = false;
                        autoStarted = true;
                    }
                }
                if (autoPause && isAutoStartEnabled) {
                    if (speed != null) {
                        if (!isRidePaused && movement<1 && movement>-1 && Float.parseFloat(speed)<2) stopVal++;
                    }

                    if (stopVal>6) {
                        isRidePaused = true;
                        //autoPaused = true;
                    }
                }
                if (rideSecond%60==0 && isAutoStartEnabled) { moveVal = stopVal = 0; }

                // check dangerous
                if (!isRidePaused) {
                    if (rideSecond%2==0 && gx > 5 || rideSecond%2==0 && gx < -5) vibrator.vibrate(1000);
                }

                // environment light level
                if (lightLevelArr.size()>4) lightLevelArr.remove(0);
                lightLevelArr.add(lightLevel);
                if (lightLevelArr.size()>4) {
                    for (int i=0; i<lightLevelArr.size(); i++) {
                        averagedLightLevel += lightLevelArr.get(i);
                    }
                    averagedLightLevel = averagedLightLevel/lightLevelArr.size();
                    Log.d(TAG, "averaged light level: " + averagedLightLevel);
                }

                // fall detection
                if (isFallDetectionEnabled) {
                    if (balanceArr.size()>2) balanceArr.remove(0);
                    balanceArr.add(gx);
                    if (balanceArr.size()>2) {
                        for (int i=0; i<balanceArr.size(); i++) {
                            averagedGx += balanceArr.get(i);
                        }
                        averagedGx = averagedGx/balanceArr.size();
                        Log.d(TAG, "fall - averaged gx: " + averagedGx);
                    }
                    if (!isRidePaused && averagedGx>8 || !isRidePaused && averagedGx<-8) isFall = true;
                }

                if (isFall && firstFall) {
                    firstFall = false;

                    // call
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    Log.d(TAG, "contact: " + phoneNumber);
                    startActivity(i);
                }

                // ride record
                if (!isRidePaused) { rideRecords.add(new RideRecord(rideSecond, gx, elev, currSpeed)); }

                runOnUiThread(() -> {
                    if (Integer.parseInt(times.get(0))<12) {
                        tv_time.setText(hr + " : " + times.get(1) + " am");
                    } else {
                        tv_time.setText(hr + " : " + times.get(1) + " pm");
                    }

                    // autoStart UI processing
                    if (moveVal>5 && isAutoStartEnabled) {
                        findViewById(R.id.map_container).setBackgroundColor(Color.rgb(145, 233, 255));
                        btn_play.setImageResource(R.drawable.ic_baseline_pause_24);
                        moveVal = 0;
                    }
                    if (stopVal>6 && isAutoStartEnabled) {
                        findViewById(R.id.map_container).setBackgroundColor(Color.rgb(255, 145, 145));
                        btn_play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        stopVal = 0;
                    }
                });
            }
        }, 1000, 1000);

        updateGPS();

        // result
        resultContainer.setTranslationY(5000);
//        Button btn_checkSpeed = findViewById(R.id.btn_speed);
//        Button btn_checkBalance = findViewById(R.id.btn_balance);
//        Button btn_checkElevation = findViewById(R.id.btn_elevation);
        Button btn_finish = findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(v -> finish());
    }

    private void showResult() {
        isRidePaused = true;
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        secTimer.cancel();
        rideTimer.cancel();

        btn_lock.setZ(1);
        btn_flash.setZ(1);
        btn_play.setZ(1);
        resultContainer.setZ(2);

        ConstraintLayout container_rideTimeGraph = findViewById(R.id.ride_time_graph_container);
        ConstraintLayout container_avgSpdGraph = findViewById(R.id.avg_spd_graph_container);
        ConstraintLayout container_maxSpdGraph = findViewById(R.id.max_spd_graph_container);
        ConstraintLayout container_finalResultGraph = findViewById(R.id.result_graph_container);

        ResultBarGraph rideTimeGraph = new ResultBarGraph(this);
        ResultBarGraph avgSpdGraph = new ResultBarGraph(this);
        ResultBarGraph maxSpdGraph = new ResultBarGraph(this);
        CalResultView finalResultGraph = new CalResultView(this);

        TextView tv_rideTimeResult = findViewById(R.id.tv_result_ride_time);
        TextView tv_avgSpdResult = findViewById(R.id.tv_result_avg_spd);
        TextView tv_maxSpdResult = findViewById(R.id.tv_result_max_spd);
        tv_rideTimeResult.setText("Ride Time: " + getRideTime());
        tv_avgSpdResult.setText("Average Speed: " + avgSpd + " km/h");
        tv_maxSpdResult.setText("Max Speed: " + maxSpd + " km/h");
        container_rideTimeGraph.addView(rideTimeGraph);
        container_avgSpdGraph.addView(avgSpdGraph);
        container_maxSpdGraph.addView(maxSpdGraph);
        container_finalResultGraph.addView(finalResultGraph);

        rideTimeGraph.update(rideSecond, 0);
        avgSpdGraph.update(Float.parseFloat(avgSpd), 1);
        maxSpdGraph.update(Float.parseFloat(maxSpd), 2);
        finalResultGraph.update(rideSecond, Float.parseFloat(avgSpd), Float.parseFloat(maxSpd), distance);

        ObjectAnimator animator = ObjectAnimator.ofFloat(resultContainer, "translationY", 0);
        animator.setDuration(500);
        animator.start();

        resultView.update(rideRecords, Float.parseFloat(maxSpd), Float.parseFloat(maxElev));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                }
        }
    }

    private String getRideTime() {
        String hr, min, s;
        s = rideSecond%60<10? "0"+(rideSecond%60):String.valueOf(rideSecond%60);
        min = rideSecond/60<10? "0"+(rideSecond/60):String.valueOf(rideSecond/60);
        hr = rideSecond/3600<10? "0"+(rideSecond/3600):String.valueOf(rideSecond/3600);
        return hr+" : "+min+" : "+s;
    }
    private void toggleFlash() {
        FloatingActionButton btn_flash = findViewById(R.id.btn_flash);
        isFlashOn = !isFlashOn;

        if (isFlashOn) {
            if (flashAvailable) {
                btn_flash.setImageResource(R.drawable.ic_baseline_flashlight_off_24);
                cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) { e.printStackTrace(); }
            } else { Toast.makeText(RideActivity.this, "Flash light not available", Toast.LENGTH_SHORT); }
        } else {
            btn_flash.setImageResource(R.drawable.ic_baseline_flashlight_on_24);
            if (flashAvailable) {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Code modified from function updateGPS(), found at:
     * https://www.youtube.com/watch?v=_xUcYfbtfsI&t=1754s
     * */
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RideActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    RideActivity.this.userLocation = location;
                    if (ActivityCompat.checkSelfPermission(RideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
                    updateMap(userLocation);
                    updateSpeed(userLocation);
                    updateAltitude(userLocation);
                }
            });
        } else {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            //}
        }
    }

    private void updateMap(Location userLocation) {
        mMap.clear();

        if (!isRidePaused) {
            CameraPosition userPosition = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                    .bearing(heading)   // degrees clockwise from north
                    .zoom(17).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPosition));

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                    //.title("")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user)))
                    .showInfoWindow();
        }
    }
    private void updateLocation(LocationResult locationResult) {
        if (!isRidePaused) {
            Geocoder geocoder = new Geocoder(RideActivity.this.getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddress = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), 1);
                if (listAddress != null && listAddress.size() > 0) {
                    String aa = listAddress.get(0).getAdminArea()!=null? listAddress.get(0).getAdminArea():"";
                    String saa = listAddress.get(0).getSubAdminArea()!=null? listAddress.get(0).getSubAdminArea():"";
                    String tf = listAddress.get(0).getThoroughfare()!=null? listAddress.get(0).getThoroughfare():"-";
                    String stf = listAddress.get(0).getSubThoroughfare()!=null? listAddress.get(0).getSubThoroughfare():"";
                    TextView tempTv = RideActivity.this.findViewById(R.id.txt_area);
                    tempTv.setText(aa+saa);
                    tempTv = RideActivity.this.findViewById(R.id.txt_street);
                    tempTv.setText(tf+" "+stf);
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
    private void updateAltitude(Location location) {
        String altitude = "0.0";
        if (!isRidePaused) {
            TextView tempView = findViewById(R.id.txt_elevation);
            if (location!=null) altitude = Double.toString((Math.round(location.getAltitude()*10)/10.d));
            tempView.setText(altitude + " m");
            elev = Float.parseFloat(altitude);
            maxElev = Float.parseFloat(altitude)>Float.parseFloat(maxElev)? altitude:maxElev;
            tempView = findViewById(R.id.txt_max_elevation);
            tempView.setText(maxElev + " m");
        }
    }
    private void updateSpeed(Location location) {
        RideActivity.this.speed = location!=null? Double.toString((Math.round(location.getSpeed()*36)/10.d)):"0.0";
        if (isRidePaused){
            tv_speed.setText("0.0");
        }
        if (tv_speed != null && !isRidePaused) {
            tv_speed.setText(RideActivity.this.speed);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // map style
        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));
            if (!success) Log.e(TAG, "Style parsing failed.");
        } catch (Exception e) { Log.e(TAG, "Can't find style. Error: ", e); }

        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setTrafficEnabled(false);

        CameraPosition userPosition = new CameraPosition.Builder()
                .target(new LatLng(22.3166, 114.191))
                .bearing(heading)   // degrees clockwise from north
                .zoom(17).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(userPosition));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sensor_a != null) {
            sensorManager.registerListener(sensorEventListener, sensor_a, SensorManager.SENSOR_DELAY_UI);
        }
        if (sensor_m != null) {
            sensorManager.registerListener(sensorEventListener, sensor_m, SensorManager.SENSOR_DELAY_UI);
        }
        if (sensor_p != null) {
            sensorManager.registerListener(sensorEventListener, sensor_p, SensorManager.SENSOR_DELAY_UI);
        }
        if (sensor_l != null) {
            sensorManager.registerListener(sensorEventListener, sensor_l, SensorManager.SENSOR_DELAY_UI);
        }

        if (ActivityCompat.checkSelfPermission(RideActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            updateGPS();
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (sensor_a != null) {
            sensorManager.unregisterListener(sensorEventListener, sensor_a);
        }
        if (sensor_m != null) {
            sensorManager.unregisterListener(sensorEventListener, sensor_m);
        }
        if (sensor_p != null) {
            sensorManager.unregisterListener(sensorEventListener, sensor_p);
        }
        if (sensor_l != null) {
            sensorManager.unregisterListener(sensorEventListener, sensor_l);
        }

        //fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }
    @Override
    protected void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        secTimer.cancel();
        rideTimer.cancel();
        super.onDestroy();
    }
}