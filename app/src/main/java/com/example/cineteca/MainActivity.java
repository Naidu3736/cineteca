package com.example.cineteca;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.cineteca.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Ajuste de barras del sistema (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // 2. Conectar la Toolbar con los fragmentos
        setupToolbar();

        // 3. Cargar la pantalla de Películas por defecto
        if (savedInstanceState == null) {
            navegarA(new MoviesFragment(), false);
        }

        // 4. Manejo del botón Atrás (físico o por gesto)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    actualizarBarra("Cineteca", false);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupToolbar() {
        // Clic en el ícono de Ajustes (definido en top_app_bar.xml)
        binding.materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                navegarA(new SettingsFragment(), true);
                actualizarBarra("Ajustes", true);
                return true;
            }
            return false;
        });

        // Clic en la flecha de regreso (cuando estamos en Ajustes)
        binding.materialToolbar.setNavigationOnClickListener(v -> {
            getSupportFragmentManager().popBackStack();
            actualizarBarra("Cineteca", false);
        });
    }

    // Intercambio dinámico de fragmentos en el contenedor
    public void navegarA(Fragment fragment, boolean agregarAlBackStack) {
        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (agregarAlBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void actualizarBarra(String titulo, boolean mostrarFlechaAtras) {
        binding.materialToolbar.setTitle(titulo);
        if (mostrarFlechaAtras) {
            binding.materialToolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        } else {
            binding.materialToolbar.setNavigationIcon(null);
        }
    }
}