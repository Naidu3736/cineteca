package com.example.cineteca;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginLogic extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        TextView GoToRegister = findViewById(R.id.Register);
        GoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginLogic.this, SignUpLogic.class));
        });
    }
}