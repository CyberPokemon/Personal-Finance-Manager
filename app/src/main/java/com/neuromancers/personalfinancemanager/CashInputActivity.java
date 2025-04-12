package com.neuromancers.personalfinancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CashInputActivity extends AppCompatActivity {

    EditText inputCash;
    LinearLayout bankAccountsContainer;
    Button btnAddAccount, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_input);

        inputCash = findViewById(R.id.input_cash);
        bankAccountsContainer = findViewById(R.id.bank_accounts_container);
        btnAddAccount = findViewById(R.id.btn_add_account);
        btnNext = findViewById(R.id.btn_next_bank);

        addBankAccountField(); // Add initial fields

        btnAddAccount.setOnClickListener(v -> addBankAccountField());

        btnNext.setOnClickListener(v -> {
            String cash = inputCash.getText().toString();
            if (cash.isEmpty()) {
                Toast.makeText(this, "Enter cash in hand", Toast.LENGTH_SHORT).show();
                return;
            }

            int count = bankAccountsContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = bankAccountsContainer.getChildAt(i);
                EditText bankName = view.findViewById(R.id.input_bank_name);
                EditText accNum = view.findViewById(R.id.input_account_number);
                EditText ifsc = view.findViewById(R.id.input_ifsc);
                EditText balance = view.findViewById(R.id.input_balance);

                if (bankName.getText().toString().isEmpty() || accNum.getText().toString().isEmpty()
                        || ifsc.getText().toString().isEmpty() || balance.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Fill all bank details", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // All inputs are valid, proceed to next activity
            Intent intent = new Intent(CashInputActivity.this, SetPinActivity.class);
            startActivity(intent);
        });
    }

    private void addBankAccountField() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View accountView = inflater.inflate(R.layout.bank_account_input, bankAccountsContainer, false);

        Button deleteBtn = accountView.findViewById(R.id.btn_delete_account);
        deleteBtn.setOnClickListener(v -> bankAccountsContainer.removeView(accountView));

        bankAccountsContainer.addView(accountView);
    }


}