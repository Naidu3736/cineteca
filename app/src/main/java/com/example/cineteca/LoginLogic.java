package com.example.cineteca;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginLogic extends AppCompatActivity {

    private DataUsers dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        dbHelper = new DataUsers(this);

        TextView goToRegister = findViewById(R.id.Register);
        EditText etUserOrEmail = findViewById(R.id.etUserOrEmail);
        EditText etPassword = findViewById(R.id.PasswordC);
        Button btnLogin = findViewById(R.id.btnLogin);

        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginLogic.this, SignUpLogic.class));
        });

        btnLogin.setOnClickListener(v -> {
            String userOrEmail = etUserOrEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (userOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingresa tus datos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.validateLogin(userOrEmail, password)) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginLogic.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}