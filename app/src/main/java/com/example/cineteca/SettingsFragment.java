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

    private final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"), uri -> {
                if (uri == null) return;
                try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                    CsvHelper.export(out, new MovieDbHelper(requireContext()).getAll());
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

        // Cerrar sesión: notificación de despedida y cierra la app
        binding.btnSettingsLogout.setOnClickListener(v -> {
            Notification.sesionCerrada(requireContext());
            requireActivity().finishAffinity();
        });
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        binding.btnExportCsv.setOnClickListener(v -> exportLauncher.launch("movies.csv"));
//        binding.btnImportCsv.setOnClickListener(v ->
//                importLauncher.launch(new String[]{"text/csv", "text/comma-separated-values", "text/plain"}));
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}