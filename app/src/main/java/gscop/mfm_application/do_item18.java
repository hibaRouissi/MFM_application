package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class do_item18 extends Activity {

    private Button boutonTerminer;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Dessin_item18 dessin;
    private Bitmap cartoBitmap;
    private TextView state;
    private ArrayList tableauX;
    private ArrayList tableauY;
    private int varRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.do_item18);
        // Permet de cacher la barre de notifications
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dessin = (Dessin_item18) findViewById(R.id.drawingItem18);
        state = (TextView) findViewById(R.id.enCours);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            varRandom = intent.getIntExtra("varRandom",-1); // -1 par défaut
        }

        // Pour le bouton "Stop"
        boutonTerminer = (Button) findViewById(R.id.buttonStop);
        boutonTerminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonTerminer.setClickable(false);
                // Action quand on appuie sur terminer -> affiche la cartographie
                state.setText(R.string.saving);
                dessin.getPaint().setColor(Color.BLUE);
                dessin.draw(dessin.getCanvas());
                Intent myIntent = new Intent(do_item18.this, carto_item18.class);
                myIntent.putExtra("name", name);
                myIntent.putExtra("surname", surname);
                myIntent.putExtra("birthdate", birthdate);
                myIntent.putExtra("varRandom",varRandom);
                cartoBitmap = dessin.getCartographie();
                tableauX = dessin.getTableauX();
                tableauY = dessin.getTableauY();
                myIntent.putExtra("path", saveToInternalStorage(cartoBitmap));
                myIntent.putExtra("tableauX",tableauX);
                myIntent.putExtra("tableauY",tableauY);
                startActivity(myIntent);
                // On ferme l'activité en cours
                finish();
            }
        });
    }

    private boolean back_answer = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Êtes-vous certain de vouloir quitter l'exercice ?\n(le tracé sera perdu)")
                    .setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            back_answer = true;
                            // On revient à l'écran des consignes de l'item 18
                            Intent myIntent = new Intent(do_item18.this, consignes_item18.class);
                            myIntent.putExtra("name", name);
                            myIntent.putExtra("surname", surname);
                            myIntent.putExtra("birthdate", birthdate);
                            myIntent.putExtra("varRandom",varRandom);
                            startActivity(myIntent);
                            // On ferme l'activité en cours
                            finish();
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            back_answer = false;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return back_answer;
    }

    /**
     * Enregistre un bitmap dans la mémoire interne de l'appareil
     *
     * @param bitmapImage l'image à enregistrer
     * @return le chemin de l'image enregistrée
     */
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "cartographie.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}
