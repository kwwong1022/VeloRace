package scm.kaifwong8.velorace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ConstraintLayout setting_container;
    ConstraintLayout menu_container;
    ConstraintLayout setting_menu;

    Switch sw_autoStart;
    Switch sw_swipeFlash;
    Switch sw_fallDetection;

    EditText et_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("settingPreferences", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("FIRST_OPEN", false)) {
            sharedPreferences.edit()
                    .putBoolean("AUTO_START", true)
                    .putBoolean("SWIPE_FLASH", false)
                    .putBoolean("FALL_DETECTION", false)
                    .putBoolean("FIRST_OPEN", true)
                    .apply();
        }

        setting_container = findViewById(R.id.setting_container);
        menu_container = findViewById(R.id.setting_container);
        setting_menu = findViewById(R.id.setting_menu);

        setting_container.setTranslationY(-5000);

        Button btn_ride = findViewById(R.id.btn_ride);
        Button btn_setting = findViewById(R.id.btn_setting);
        Button btn_setting_finish = findViewById(R.id.btn_setting_finish);
        et_phone = findViewById(R.id.et_phone);

        btn_ride.setOnClickListener(v -> {
            Log.d(TAG, "btn_ride");
            Intent i = new Intent(MainActivity.this, RideActivity.class);
            startActivity(i);
        });
        btn_setting.setOnClickListener(v -> {
//            Log.d(TAG, "btn_setting");
//            Log.d(TAG, "auto_start: " + sharedPreferences.getBoolean("AUTO_START", false));
//            Log.d(TAG, "swipe_flash: " + sharedPreferences.getBoolean("SWIPE_FLASH", false));
//            Log.d(TAG, "fall_detection: " + sharedPreferences.getBoolean("FALL_DETECTION", false));
            showSetting();
        });
        btn_setting_finish.setOnClickListener(v -> {
            Log.d(TAG, "setting_finish");
            showMenu();
        });

        sw_autoStart = findViewById(R.id.sw_auto_start);
        sw_swipeFlash = findViewById(R.id.sw_swipe_flash);
        sw_fallDetection = findViewById(R.id.sw_fall_detection);

        sw_autoStart.setOnClickListener(v -> {
            if (sw_autoStart.isChecked()) {
                sharedPreferences.edit()
                        .putBoolean("AUTO_START", true)
                        .apply();
            } else {
                sharedPreferences.edit()
                        .putBoolean("AUTO_START", false)
                        .apply();
            }
        });

        sw_swipeFlash.setOnClickListener(v -> {
            if (sw_swipeFlash.isChecked()) {
                sharedPreferences.edit()
                        .putBoolean("SWIPE_FLASH", true)
                        .apply();
            } else {
                sharedPreferences.edit()
                        .putBoolean("SWIPE_FLASH", false)
                        .apply();
            }
        });

        sw_fallDetection.setOnClickListener(v -> {
            if (sw_fallDetection.isChecked()) {
                sharedPreferences.edit()
                        .putBoolean("FALL_DETECTION", true)
                        .apply();
            } else {
                sharedPreferences.edit()
                        .putBoolean("FALL_DETECTION", false)
                        .apply();
            }
        });

        showMenu();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RideActivity.LOCATION_PERMISSION_REQUEST);
        }
    }

    private void showMenu() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(setting_container, "translationY", -5000);
        animator.setDuration(500);
        animator.start();

        setting_container.setZ(1);
        menu_container.setZ(2);
        setting_menu.setZ(1);

        SharedPreferences sharedPreferences = getSharedPreferences("settingPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putString("PHONE_NUMBER", et_phone.getText().toString()).apply();
        Log.d(TAG, "phone number: " + sharedPreferences.getString("PHONE_NUMBER", ""));
    }

    private void showSetting() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(setting_container, "translationY", 0);
        animator.setDuration(500);
        animator.start();

        setting_container.setZ(1);
        menu_container.setZ(2);
        setting_menu.setZ(2);

        SharedPreferences sharedPreferences = getSharedPreferences("settingPreferences", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("AUTO_START", false)) {
            sw_autoStart.setChecked(true);
        } else {
            sw_autoStart.setChecked(false);
        }
        if (sharedPreferences.getBoolean("SWIPE_FLASH", false)) {
            sw_swipeFlash.setChecked(true);
        } else {
            sw_swipeFlash.setChecked(false);
        }
        if (sharedPreferences.getBoolean("FALL_DETECTION", false)) {
            sw_fallDetection.setChecked(true);
        } else {
            sw_fallDetection.setChecked(false);
        }

        et_phone.setText(sharedPreferences.getString("PHONE_NUMBER", ""));
    }
}