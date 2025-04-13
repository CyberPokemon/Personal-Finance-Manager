package com.neuromancers.personalfinancemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "FinanceAppPrefs";
    private static final String KEY_IS_REGISTERED = "is_registered";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean(KEY_IS_REGISTERED, false);

        if (isRegistered) {
            // User is registered -> Go to Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            // First time -> Go to Registration
            startActivity(new Intent(MainActivity.this, TermsAndCondition.class));
        }

        finish();
    }
}