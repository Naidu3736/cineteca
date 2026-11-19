package com.example.cineteca;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {

    private static final String HEADER = "title,rating,poster_url";

    public static void export(OutputStream out, List<Movie> movies) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.write(HEADER);
        writer.newLine();
        for (Movie m : movies) {
            writer.write(escape(m.getTitle()) + ","
                    + m.getRating() + ","
                    + escape(m.getPosterUrl()));
            writer.newLine();
        }
        writer.flush();
    }

    public static List<Movie> importFrom(InputStream in) throws IOException {
        List<Movie> movies = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8));
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            List<String> f = parseLine(line);
            if (f.size() < 2 || f.get(0).isEmpty()) continue;
            double rating = 0;
            try {
                rating = Double.parseDouble(f.get(1));
            } catch (NumberFormatException ignored) { }
            String poster = f.size() > 2 ? f.get(2) : "";
            movies.add(new Movie(f.get(0), rating, poster));
        }
        return movies;
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else if (ch == '"') {
                    inQuotes = false;
                } else {
                    sb.append(ch);
                }
            } else if (ch == '"') {
                inQuotes = true;
            } else if (ch == ',') {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(ch);
            }
        }
        fields.add(sb.toString());
        return fields;
    }
}