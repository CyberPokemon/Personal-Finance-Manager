package com.neuromancers.personalfinancemanager;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView txtUserName, txtDob, txtAddress, txtCash;
    LinearLayout bankAccountsContainer;
    DatabaseHelper dbHelper;
    long currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_home);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        setContentView(R.layout.activity_home);

        txtUserName = findViewById(R.id.txt_user_name);
        txtDob = findViewById(R.id.txt_dob);
        txtAddress = findViewById(R.id.txt_address);
        txtCash = findViewById(R.id.txt_cash);
        bankAccountsContainer = findViewById(R.id.bank_accounts_container);

        dbHelper = new DatabaseHelper(this);

        currentUserId = dbHelper.getLastInsertedUserId();

        User user = dbHelper.getUser(currentUserId);
        List<BankAccount> accounts = dbHelper.getBankAccounts(currentUserId);

        if (user != null) {
            txtUserName.setText("Name: " + user.getName());
            txtDob.setText("DOB: " + user.getDob());
            txtAddress.setText("Address: " + user.getAddress());
            txtCash.setText("Cash in hand: ₹" + user.getCash());

            for (BankAccount acc : accounts) {
                TextView accountView = new TextView(this);
                accountView.setText("Bank: " + acc.bankName +
                        "\nAccount No: " + acc.accountNumber +
                        "\nIFSC: " + acc.ifscCode +
                        "\nBalance: ₹" + acc.balance);
                accountView.setPadding(16, 16, 16, 16);
                bankAccountsContainer.addView(accountView);
            }
        }
    }
}