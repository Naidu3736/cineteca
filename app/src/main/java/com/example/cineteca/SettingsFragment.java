package com.example.cineteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cineteca.databinding.FragmentSettingsBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private MovieStorageManager storage;

    private final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
                if (uri == null) return;
                try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                    CsvHelper.export(out, storage.getAll());
                    Toast.makeText(requireContext(), "Exported", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<String[]> importLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
                    MovieDbHelper db = new MovieDbHelper(requireContext());
                    for (Movie movie : CsvHelper.importFrom(in)) {
                        db.insert(movie);
                    }
                    Toast.makeText(requireContext(), "Imported", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Import failed", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Marcar el botón activo según el tema guardado (Dark por defecto)
        if (ThemeHelper.isDarkMode(requireContext())) {
            binding.toggleDarkMode.check(R.id.btn_mode_dark);
        } else {
            binding.toggleDarkMode.check(R.id.btn_mode_light);
        }

        // 2. Aplicar colores actuales al abrir Ajustes
        ThemeHelper.applyThemeToView(binding.getRoot());

        // 3. Conectar los botones Tema Claro / Tema Oscuro
        binding.toggleDarkMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            boolean wantDark = (checkedId == R.id.btn_mode_dark);

            if (wantDark != ThemeHelper.isDarkMode(requireContext())) {
                ThemeHelper.setDarkMode(requireContext(), wantDark);
                ThemeHelper.applyThemeToView(binding.getRoot());
                if (getActivity() != null) {
                    ThemeHelper.applyThemeToView(getActivity().findViewById(R.id.main));
                }
            }
        });

        // 4. Almacenamiento de registros (SQLite / CSV) y botón Descargar CSV
        storage = new MovieStorageManager(requireContext());
        storage.bindStorageRadioGroup(binding.rgStorageMode);
        binding.btnViewCsv.setOnClickListener(v -> exportLauncher.launch("movies.csv"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}