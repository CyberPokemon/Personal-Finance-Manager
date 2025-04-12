package com.neuromancers.personalfinancemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SetPinActivity extends AppCompatActivity {

    EditText inputPin, inputConfirmPin;
    CheckBox checkboxBiometric;
    Button btnFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        inputPin = findViewById(R.id.input_pin);
        inputConfirmPin = findViewById(R.id.input_confirm_pin);
        checkboxBiometric = findViewById(R.id.checkbox_biometric);
        btnFinish = findViewById(R.id.btn_finish);

        btnFinish.setOnClickListener(v -> {
            String pin = inputPin.getText().toString();
            String confirmPin = inputConfirmPin.getText().toString();
            boolean biometricEnabled = checkboxBiometric.isChecked();

            if (pin.length() != 4 || confirmPin.length() != 4) {
                Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pin.equals(confirmPin)) {
                Toast.makeText(this, "PINs do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                saveEncryptedPin(pin, biometricEnabled);
                Toast.makeText(this, "PIN Set Successfully!", Toast.LENGTH_SHORT).show();


                // Retrieve passed data
                Intent dataIntent = getIntent();
                String name = dataIntent.getStringExtra("name");
                String dob = dataIntent.getStringExtra("dob");
                String address = dataIntent.getStringExtra("address");
                double cash = dataIntent.getDoubleExtra("cash", 0.0);

                List<BankAccount> accounts = BankDataHolder.getInstance().getAccounts();

                // Save to database
                DatabaseHelper dbHelper = new DatabaseHelper(SetPinActivity.this);
                long userId = dbHelper.insertUser(name, dob, address, cash);
                for (BankAccount acc : accounts) {
                    dbHelper.insertBankAccount(userId, acc.bankName, acc.accountNumber, acc.ifscCode, acc.balance);
                }



                Intent intent = new Intent(SetPinActivity.this, HomeActivity.class);
                SharedPreferences prefs = getSharedPreferences("FinanceAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_registered", true);
                editor.apply();
                startActivity(intent);
                finish();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save PIN securely", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEncryptedPin(String pin, boolean biometricEnabled)
            throws GeneralSecurityException, IOException {

        MasterKey masterKey = new MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        SharedPreferences encryptedPrefs = EncryptedSharedPreferences.create(
                this,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        SharedPreferences.Editor editor = encryptedPrefs.edit();
        editor.putString("user_pin", pin);
        editor.putBoolean("biometric_enabled", biometricEnabled);
        editor.apply();
    }
}