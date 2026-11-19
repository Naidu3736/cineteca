package com.example.cineteca;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
        TextView forgotPassword = findViewById(R.id.Password);

        forgotPassword.setOnClickListener(v -> mostrarRestore());

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
                String username = dbHelper.getUsername(userOrEmail);
                getSharedPreferences("cineteca_session", MODE_PRIVATE)
                        .edit().putString("current_user", username).apply();
                Notification.saludo(this, username);
                startActivity(new Intent(LoginLogic.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarRestore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar contraseña");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        EditText inputUser = new EditText(this);
        inputUser.setHint("Usuario o correo registrado");
        layout.addView(inputUser);

        EditText inputNewPass = new EditText(this);
        inputNewPass.setHint("Nueva contraseña");
        inputNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNewPass);

        builder.setView(layout);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String userOrEmail = inputUser.getText().toString().trim();
            String newPass = inputNewPass.getText().toString().trim();

            if (userOrEmail.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updatePassword(userOrEmail, newPass)) {
                Notification.contrasenaCambiada(this);
            } else {
                Toast.makeText(this, "No se encontró ese usuario o correo", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}