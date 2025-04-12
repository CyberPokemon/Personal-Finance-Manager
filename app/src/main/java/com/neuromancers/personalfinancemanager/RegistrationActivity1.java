package com.neuromancers.personalfinancemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class RegistrationActivity1 extends AppCompatActivity {
    EditText nameInput, dobInput, addressInput;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_registration1);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

//        SharedPreferences prefs = getSharedPreferences("FinanceAppPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("is_registered", true);
//        editor.apply();

        setContentView(R.layout.activity_registration1);

        nameInput = findViewById(R.id.input_name);
        dobInput = findViewById(R.id.input_dob);
        addressInput = findViewById(R.id.input_address);
        nextBtn = findViewById(R.id.btn_next);

        dobInput.setFocusable(false);
        dobInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String dob = dobInput.getText().toString();
                String address = addressInput.getText().toString();

                if (name.isEmpty() || dob.isEmpty() || address.isEmpty()) {
                    Toast.makeText(RegistrationActivity1.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RegistrationActivity1.this, CashInputActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("dob", dob);
                    intent.putExtra("address", address);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dobInput.setText(selectedDate);
                    }
                }, year, month, day);

        datePickerDialog.show();

    }
}