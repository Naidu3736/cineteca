package com.example.cineteca;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    }

    private String getCurrentUser() {
        return context.getSharedPreferences("cineteca_session", Context.MODE_PRIVATE)
                .getString("current_user", "default");
    }

    public String getStorageMode() {
        return prefs.getString(KEY_STORAGE_MODE + "_" + getCurrentUser(), MODE_SQLITE);
    }

    public void setStorageMode(String mode) {
        if (!MODE_CSV.equals(mode) && !MODE_SQLITE.equals(mode)) {
            mode = MODE_SQLITE;
        }
        prefs.edit().putString(KEY_STORAGE_MODE + "_" + getCurrentUser(), mode).apply();
    }

    public boolean isCsvMode() {
        return MODE_CSV.equals(getStorageMode());
    }

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
                writeAllToCsv(dbHelper.getAll());
                setStorageMode(MODE_CSV);

                Toast.makeText(group.getContext(),
                        "Csv activo @Almacenamiento cambiado a CSV", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.rb_storage_sqlite) {
                setStorageMode(MODE_SQLITE);
                Toast.makeText(group.getContext(),
                        "Almacenamiento cambiado a SQLite", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<Movie> getAll() {
        if (isCsvMode()) {
            return readAllFromCsv();
        }
        return dbHelper.getAll();
    }

    public long insert(Movie movie) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            current.add(movie);
            return writeAllToCsv(current) ? current.size() : -1;
        }
        return dbHelper.insert(movie);
    }

    public int update(Movie movie) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            for (int i = 0; i < current.size(); i++) {
                if (current.get(i).getId() == movie.getId()) {
                    current.set(i, movie);
                    return writeAllToCsv(current) ? 1 : 0;
                }
            }
            return 0;
        }
        return dbHelper.update(movie);
    }

    public int delete(long id) {
        if (isCsvMode()) {
            List<Movie> current = readAllFromCsv();
            for (int i = 0; i < current.size(); i++) {
                if (current.get(i).getId() == id) {
                    current.remove(i);
                    return writeAllToCsv(current) ? 1 : 0;
                }
            }
            return 0;
        }
        return dbHelper.delete(id);
    }

    public File getCsvFile() {
        return new File(context.getFilesDir(), "movies_" + getCurrentUser() + ".csv");
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
