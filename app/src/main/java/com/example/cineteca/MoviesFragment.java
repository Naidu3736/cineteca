package com.example.cineteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cineteca.databinding.DialogAddMovieBinding;
import com.example.cineteca.databinding.FragmentMoviesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoviesFragment extends Fragment {

    private FragmentMoviesBinding binding;
    private MovieDbHelper db;
    private MovieAdapter adapter;
    private List<Movie> allMovies = new ArrayList<>();
    private String query = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMoviesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new MovieDbHelper(requireContext());
        adapter = new MovieAdapter();
        binding.recyclerPeliculas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPeliculas.setAdapter(adapter);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                query = text;
                applyFilters();
                return true;
            }
        });

        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, ids) -> applyFilters());
        binding.floatingActionButton.setOnClickListener(v -> showAddDialog());

        loadMovies();
    }

    private void loadMovies() {
        allMovies = db.getAll();
        applyFilters();
    }

    private void applyFilters() {
        List<Movie> result = new ArrayList<>();
        String q = query.toLowerCase().trim();
        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(q)) {
                result.add(movie);
            }
        }

        int checked = binding.chipGroupFilters.getCheckedChipId();
        if (checked == R.id.chip_filter_az) {
            Collections.sort(result, (a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        } else if (checked == R.id.chip_filter_value) {
            Collections.sort(result, (a, b) -> Double.compare(b.getRating(), a.getRating()));
        }

        adapter.submit(result);
    }

    private void showAddDialog() {
        DialogAddMovieBinding form = DialogAddMovieBinding.inflate(getLayoutInflater());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add movie")
                .setView(form.getRoot())
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = String.valueOf(form.etCustomTitle.getText()).trim();
                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double rating = 0;
                    try {
                        rating = Double.parseDouble(String.valueOf(form.etCustomRating.getText()));
                    } catch (NumberFormatException ignored) { }
                    rating = Math.max(0, Math.min(10, rating));

                    String poster = String.valueOf(form.etCustomPoster.getText()).trim();

                    db.insert(new Movie(title, rating, poster));
                    loadMovies();
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}