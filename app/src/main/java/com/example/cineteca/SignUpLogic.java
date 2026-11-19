package com.example.cineteca;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpLogic extends AppCompatActivity {

    private DataUsers dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_interface);

        dbHelper = new DataUsers(this);

        EditText Username = findViewById(R.id.Username);
        EditText Email = findViewById(R.id.Email);
        EditText Password = findViewById(R.id.PasswordC);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView goToLogin = findViewById(R.id.GLogin);

        btnRegister.setOnClickListener(v -> {
            String username = Username.getText().toString().trim();
            String email = Email.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }


            boolean registeruser = dbHelper.registeruser(username, email, password);
            if (registeruser) {
                Toast.makeText(this, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                Notification.saludo(this, username);
                finish(); 
            } else {
                Toast.makeText(this, "El usuario o correo ya está registrado", Toast.LENGTH_SHORT).show();
            }
        });

        goToLogin.setOnClickListener(v -> finish());
    }
}