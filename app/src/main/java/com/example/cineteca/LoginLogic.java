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

        // Pedir permiso de notificaciones (Android 13+)
        if (!Notification.tienePermiso(this)) {
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(), granted -> { })
                    .launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }

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
                Notification.saludo(this, dbHelper.getUsername(userOrEmail));
                startActivity(new Intent(LoginLogic.this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}