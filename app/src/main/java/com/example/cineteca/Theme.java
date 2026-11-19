package com.example.cineteca;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class Theme {

    private static final String PREFS_NAME = "cineteca_theme_prefs";
    private static final String KEY_IS_DARK = "is_dark_mode";

    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_DARK, true);
    }

    public static void setDarkMode(Context context, boolean isDark) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply();

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean toggleTheme(Context context) {
        boolean newState = !isDarkMode(context);
        setDarkMode(context, newState);
        return newState;
    }

    public static void applyThemeToView(View rootView) {
        if (rootView == null) return;
        Context context = rootView.getContext();
        boolean isDark = isDarkMode(context);

        if (rootView.getId() == R.id.main
                || rootView instanceof androidx.constraintlayout.widget.ConstraintLayout
                || rootView instanceof android.widget.ScrollView) {
            if (isDark) {
                rootView.setBackgroundResource(R.drawable.bg_setting);
            } else {
                rootView.setBackgroundColor(Color.parseColor("#FFF8F8"));
            }
        }

        applyRecursive(rootView, isDark);
    }

    private static void applyRecursive(View view, boolean isDark) {
        Context context = view.getContext();
        int crimson = ContextCompat.getColor(context, R.color.crimson_primary);

        // colores modo oscuro
        int cardDark = Color.parseColor("#1A1D24");
        int strokeDark = Color.parseColor("#2C313C");

        // colores modo claro
        int cardLight = Color.WHITE;
        int strokeLight = Color.parseColor("#F5C2C7");       // Borde rosa pastel
        int pastelRedBg = Color.parseColor("#FDE8EA");       // Fondo pastel suave
        int pastelRedSelected = Color.parseColor("#F8B4BC"); // Fondo seleccionado pastel
        int crimsonDarkText = Color.parseColor("#8B1E2B");   // Rojo vino oscuro

        int textPrimary = isDark ? Color.WHITE : Color.parseColor("#1D1416");
        int textSecondary = isDark ? Color.parseColor("#9BA1B0") : Color.parseColor("#6E5659");

        // barra superior e iconos
        if (view instanceof MaterialToolbar) {
            MaterialToolbar toolbar = (MaterialToolbar) view;
            int iconColor = isDark ? Color.WHITE : crimsonDarkText;
            toolbar.setTitleTextColor(iconColor);


            Drawable navIcon = toolbar.getNavigationIcon();
            if (navIcon != null) {
                Drawable wrapped = DrawableCompat.wrap(navIcon.mutate());
                DrawableCompat.setTint(wrapped, iconColor);
                toolbar.setNavigationIcon(wrapped);
            }


            Menu menu = toolbar.getMenu();
            if (menu != null) {
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem item = menu.getItem(i);
                    Drawable icon = item.getIcon();
                    if (icon != null) {
                        Drawable wrapped = DrawableCompat.wrap(icon.mutate());
                        DrawableCompat.setTint(wrapped, iconColor);
                        item.setIcon(wrapped);
                    }
                }
            }


            for (int i = 0; i < toolbar.getChildCount(); i++) {
                View child = toolbar.getChildAt(i);
                if (child instanceof ActionMenuView) {
                    ActionMenuView menuView = (ActionMenuView) child;
                    for (int j = 0; j < menuView.getChildCount(); j++) {
                        View actionItemView = menuView.getChildAt(j);
                        if (isDark) {
                            actionItemView.setBackground(null);
                        } else {
                            GradientDrawable circle = new GradientDrawable();
                            circle.setShape(GradientDrawable.OVAL);
                            circle.setColor(pastelRedBg);
                            circle.setStroke(2, strokeLight);
                            actionItemView.setBackground(circle);
                        }
                    }
                }
            }
            return;
        }


        if (view instanceof SearchView) {
            SearchView searchView = (SearchView) view;
            if (isDark) {
                searchView.setBackgroundResource(R.drawable.shape_text);
            } else {
                GradientDrawable searchBg = new GradientDrawable();
                searchBg.setShape(GradientDrawable.RECTANGLE);
                searchBg.setCornerRadius(27 * context.getResources().getDisplayMetrics().density);
                searchBg.setColor(pastelRedBg);
                searchBg.setStroke((int) (1.2f * context.getResources().getDisplayMetrics().density), strokeLight);
                searchView.setBackground(searchBg);
            }


            EditText searchEdit = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            if (searchEdit != null) {
                searchEdit.setTextColor(textPrimary);
                searchEdit.setHintTextColor(textSecondary);
            }

            int searchIconColor = isDark ? Color.parseColor("#9BA1B0") : crimsonDarkText;
            ImageView magIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
            if (magIcon != null) {
                magIcon.setColorFilter(searchIconColor, PorterDuff.Mode.SRC_IN);
            }
            ImageView closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            if (closeBtn != null) {
                closeBtn.setColorFilter(searchIconColor, PorterDuff.Mode.SRC_IN);
            }
            return;
        }

        if (view instanceof MaterialCardView) {
            MaterialCardView card = (MaterialCardView) view;
            if (card.getRadius() < 60f) {
                card.setCardBackgroundColor(isDark ? cardDark : cardLight);
                card.setStrokeColor(isDark ? strokeDark : strokeLight);
            } else {
                card.setCardBackgroundColor(isDark ? Color.parseColor("#252A34") : pastelRedBg);
            }

        } else if (view instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) view;
            if (isDark) {
                fab.setBackgroundTintList(ColorStateList.valueOf(crimson));
                fab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.on_crimson_primary)));
            } else {
                fab.setBackgroundTintList(ColorStateList.valueOf(pastelRedSelected));
                fab.setImageTintList(ColorStateList.valueOf(crimsonDarkText));
            }

        } else if (view instanceof Chip) {
            Chip chip = (Chip) view;
            if (isDark) {
                chip.setChipBackgroundColorResource(R.color.chip_bg);
                chip.setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text));
            } else {
                int[][] states = new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                };
                chip.setChipBackgroundColor(new ColorStateList(states, new int[]{pastelRedSelected, pastelRedBg}));
                chip.setTextColor(new ColorStateList(states, new int[]{crimsonDarkText, textSecondary}));
                chip.setCheckedIconTint(ColorStateList.valueOf(crimsonDarkText));
            }

        } else if (view instanceof MaterialRadioButton) {
            MaterialRadioButton rb = (MaterialRadioButton) view;
            rb.setTextColor(textPrimary);
            rb.setButtonTintList(ColorStateList.valueOf(isDark ? crimson : crimsonDarkText));


        } else if (view instanceof MaterialButton) {
            MaterialButton btn = (MaterialButton) view;
            int id = btn.getId();

            if (id == R.id.btn_view_csv) {
                if (isDark) {
                    btn.setBackgroundTintList(ColorStateList.valueOf(crimson));
                    btn.setTextColor(Color.BLACK);
                    btn.setIconTint(ColorStateList.valueOf(Color.BLACK));
                } else {
                    btn.setBackgroundTintList(ColorStateList.valueOf(pastelRedSelected));
                    btn.setTextColor(crimsonDarkText);
                    btn.setIconTint(ColorStateList.valueOf(crimsonDarkText));
                }
            } else if (id == R.id.btn_mode_light || id == R.id.btn_mode_dark) {
                if (isDark) {
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    btn.setTextColor(Color.WHITE);
                    btn.setStrokeColor(ColorStateList.valueOf(strokeDark));
                } else {
                    int[][] states = new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{}
                    };
                    btn.setBackgroundTintList(new ColorStateList(states, new int[]{pastelRedSelected, Color.WHITE}));
                    btn.setTextColor(new ColorStateList(states, new int[]{crimsonDarkText, textSecondary}));
                    btn.setStrokeColor(ColorStateList.valueOf(strokeLight));
                }
            } else {
                if (isDark) {
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
                    btn.setTextColor(Color.WHITE);
                    btn.setIconTint(ColorStateList.valueOf(Color.WHITE));
                    btn.setStrokeColor(ColorStateList.valueOf(strokeDark));
                } else {
                    btn.setBackgroundTintList(ColorStateList.valueOf(pastelRedBg));
                    btn.setTextColor(crimsonDarkText);
                    btn.setIconTint(ColorStateList.valueOf(crimsonDarkText));
                    btn.setStrokeColor(ColorStateList.valueOf(strokeLight));
                }
            }

        } else if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTextColor(textPrimary);
            et.setHintTextColor(textSecondary);
            if (!isDark) {
                et.setBackgroundTintList(ColorStateList.valueOf(pastelRedBg));
            } else {
                et.setBackgroundTintList(null);
            }


        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            int currentColor = tv.getCurrentTextColor();
            if (currentColor != crimson && currentColor != crimsonDarkText) {
                if (tv.getTextSize() >= 42f || currentColor == Color.WHITE || currentColor == Color.parseColor("#1D1416")) {
                    tv.setTextColor(textPrimary);
                } else {
                    tv.setTextColor(textSecondary);
                }
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyRecursive(group.getChildAt(i), isDark);
            }
        }
    }
}