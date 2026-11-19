package com.example.cineteca;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpLogic extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_interface);

        TextView GoToLogin = findViewById(R.id.GLogin);
        GoToLogin.setOnClickListener(v -> finish());
    }
}