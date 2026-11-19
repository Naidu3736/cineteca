package com.example.cineteca;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MovieStorageManager {

    public static final String MODE_SQLITE = "SQLITE";
    public static final String MODE_CSV = "CSV";

    private static final String PREFS_NAME = "cineteca_storage_prefs";
    private static final String KEY_STORAGE_MODE = "storage_mode";
    private static final String CSV_FILE_NAME = "movies.csv";

    private final Context context;
    private final SharedPreferences prefs;
    private final MovieDbHelper dbHelper;

    public MovieStorageManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.dbHelper = new MovieDbHelper(this.context);
        ensureMoviesTableExists();
    }

    private void ensureMoviesTableExists() {
        dbHelper.getWritableDatabase().execSQL(
                "CREATE TABLE IF NOT EXISTS movies ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "title TEXT NOT NULL, "
                        + "rating REAL, "
                        + "poster_url TEXT)"
        );
    }

    public String getStorageMode() {
        return prefs.getString(KEY_STORAGE_MODE, MODE_SQLITE);
    }

    public void setStorageMode(String mode) {
        if (!MODE_CSV.equals(mode) && !MODE_SQLITE.equals(mode)) {
            mode = MODE_SQLITE;
        }
        prefs.edit().putString(KEY_STORAGE_MODE, mode).apply();
    }

    public boolean isCsvMode() {
        return MODE_CSV.equals(getStorageMode());
    }

    public boolean isSqliteMode() {
        return MODE_SQLITE.equals(getStorageMode());
    }

    /**
     * Conecta el RadioGroup de fragment_settings.xml (rg_storage_mode)
     * para que muestre el modo actual y permita al usuario cambiar entre SQLite y CSV.
     */
    public void bindStorageRadioGroup(RadioGroup rgStorageMode) {
        if (rgStorageMode == null) return;

        rgStorageMode.setOnCheckedChangeListener(null);
        if (isCsvMode()) {
            rgStorageMode.check(R.id.rb_storage_csv);
        } else {
            rgStorageMode.check(R.id.rb_storage_sqlite);
        }

        rgStorageMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_storage_csv) {
                setStorageMode(MODE_CSV);
                Toast.makeText(group.getContext(),
                        "Almacenamiento cambiado a CSV", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.rb_storage_sqlite) {
                setStorageMode(MODE_SQLITE);
                Toast.makeText(group.getContext(),
                        "Almacenamiento cambiado a SQLite", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void bindSettingsView(View settingsRootView) {
        if (settingsRootView == null) return;
        RadioGroup rg = settingsRootView.findViewById(R.id.rg_storage_mode);
        bindStorageRadioGroup(rg);
    }

    public List<Movie> getAll() {
        if (isCsvMode()) {
            return readAllFromCsv();
        }
        ensureMoviesTableExists();
        return dbHelper.getAll();
    }

    public long insert(Movie movie) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            long nextId = current.size() + 1;
            Movie withId = new Movie(nextId, movie.getTitle(), movie.getRating(), movie.getPosterUrl());
            current.add(withId);
            return writeAllToCsv(current) ? nextId : -1;
        }
        ensureMoviesTableExists();
        return dbHelper.insert(movie);
    }

    public int update(Movie movie) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            int updated = 0;
            for (int i = 0; i < current.size(); i++) {
                if (current.get(i).getId() == movie.getId()) {
                    current.set(i, movie);
                    updated = 1;
                    break;
                }
            }
            if (updated > 0 && !writeAllToCsv(current)) {
                return 0;
            }
            return updated;
        }
        ensureMoviesTableExists();
        return dbHelper.update(movie);
    }

    public int delete(long id) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            int removed = 0;
            for (int i = current.size() - 1; i >= 0; i--) {
                if (current.get(i).getId() == id) {
                    current.remove(i);
                    removed = 1;
                    break;
                }
            }
            if (removed > 0 && !writeAllToCsv(current)) {
                return 0;
            }
            return removed;
        }
        ensureMoviesTableExists();
        return dbHelper.delete(id);
    }

    public File getCsvFile() {
        return new File(context.getFilesDir(), CSV_FILE_NAME);
    }

    public void exportCurrentMoviesToCsv(OutputStream out) throws IOException {
        CsvHelper.export(out, getAll());
    }

    private List<Movie> readAllFromCsv() {
        File file = getCsvFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileInputStream in = new FileInputStream(file)) {
            List<Movie> imported = CsvHelper.importFrom(in);
            List<Movie> withIds = new ArrayList<>(imported.size());
            for (int i = 0; i < imported.size(); i++) {
                Movie m = imported.get(i);
                withIds.add(new Movie(i + 1, m.getTitle(), m.getRating(), m.getPosterUrl()));
            }
            return withIds;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private boolean writeAllToCsv(List<Movie> movies) {
        File file = getCsvFile();
        try (FileOutputStream out = new FileOutputStream(file, false)) {
            CsvHelper.export(out, movies);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
