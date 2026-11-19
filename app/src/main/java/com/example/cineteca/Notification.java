package com.example.cineteca;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public final class Notification {

    private static final String CHANNEL_ID = "cineteca_channel";
    private static int nextId = 1;

    private Notification() {
    }

    public static void saludo(Context context, String usuario) {
        mostrar(context, "Cineteca", "¡Hola " + usuario + "!");
    }

    public static void sesionCerrada(Context context) {
        mostrar(context, "Cineteca", "Sesión cerrada correctamente. ¡Hasta pronto!");
    }

    public static void contrasenaCambiada(Context context) {
        mostrar(context, "Cineteca", "Cambio de contraseña correcto");
    }

    public static void peliculaAgregada(Context context, String pelicula) {
        mostrar(context, "Película agregada", "Se agregó \"" + pelicula + "\".");
    }

    public static void peliculaEliminada(Context context, String pelicula) {
        mostrar(context, "Película eliminada", "Se eliminó \"" + pelicula + "\".");
    }

    public static void peliculaModificada(Context context, String pelicula) {
        mostrar(context, "Película modificada", "Se modificó \"" + pelicula + "\".");
    }

    public static void temaCambiado(Context context, String nombreTema) {
        mostrar(context, "Tema cambiado", "Ahora usas el tema " + nombreTema + ".");
    }



    public static boolean tienePermiso(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }


    private static void mostrar(Context context, String titulo, String mensaje) {
        if (!tienePermiso(context)) {
            return;
        }
        crearCanal(context);

        Intent intent = new Intent(context, LoginLogic.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(logoCircular(context))   // imagen en círculo junto al texto
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            NotificationManagerCompat.from(context).notify(nextId++, builder.build());
        } catch (SecurityException ignored) {
        }
    }

    private static void crearCanal(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID, "Cineteca", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(canal);
            }
        }
    }

    private static Bitmap logoCircular(Context context) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_cineteca);
        int size = 256;
        Bitmap escalado = Bitmap.createScaledBitmap(original, size, size, true);

        Bitmap resultado = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultado);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(escalado, new Rect(0, 0, size, size), new Rect(0, 0, size, size), paint);
        return resultado;
    }
}
