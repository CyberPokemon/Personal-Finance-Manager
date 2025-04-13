package com.neuromancers.personalfinancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TermsAndCondition extends AppCompatActivity {

    private CheckBox checkboxAgree;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_terms_and_condition);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        setContentView(R.layout.activity_terms_and_condition);

        checkboxAgree = findViewById(R.id.checkbox_agree);
        btnNext = findViewById(R.id.Nxt);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxAgree.isChecked()) {
                    // Redirect to NextActivity
                    Intent intent = new Intent(TermsAndCondition.this, RegistrationActivity1.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(TermsAndCondition.this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}