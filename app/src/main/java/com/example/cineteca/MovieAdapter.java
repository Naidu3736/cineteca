package com.example.cineteca;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final List<Movie> items = new ArrayList<>();

    public void submit(List<Movie> movies) {
        items.clear();
        items.addAll(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_save, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = items.get(position);
        holder.title.setText(movie.getTitle());
        holder.rating.setText(String.format(Locale.US, "%.1f", movie.getRating()));

        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            try {
                holder.poster.setImageURI(Uri.parse(posterUrl));
            } catch (Exception e) {
                holder.poster.setImageDrawable(null);
            }
        } else {
            holder.poster.setImageDrawable(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView rating;
        final ImageView poster;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvTitulo);
            rating = view.findViewById(R.id.tvRating);
            poster = view.findViewById(R.id.ivPoster);
        }
    }
}