package gscop.mfm_application;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class do_item19 extends Activity {

    private static final String TAG = "www.gscop.mfm" ;
    private Button boutonTerminer;
    private final Context context = this;
    private Button move_CD;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Dessin_item19 dessin;
    private Bitmap cartoBitmap;
    private TextView state;
    private ArrayList tableauX;
    private ArrayList tableauY;
    private boolean click_first = false;
    private int varRandom;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private ArrayList isPalm;
    private float mImageX,mImageY;
    private Long durationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.do_item19);

        // Permet de cacher la barre de notifications et bloquer l'expansion
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //------------------------------------------------------------------

        dessin = (Dessin_item19) findViewById(R.id.drawingItem19);
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
                if(click_first == true){
                    Toast toast = Toast.makeText(context,R.string.toast_blockrect,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
                    toast.show();
                }
                else {
                    boutonTerminer.setClickable(false);
                    // Action quand on appuie sur terminer -> affiche la cartographie
                    state.setText(R.string.saving);
                    dessin.getPaint().setColor(Color.BLUE);
                    dessin.draw(dessin.getCanvas());
                    Intent myIntent = new Intent(do_item19.this, carto_item19.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("varRandom", varRandom);
                    dessin.orderPaths();
                    cartoBitmap = dessin.getCartographie();
                    tableauX = dessin.getTableauX();
                    tableauY = dessin.getTableauY();
                    eventDownTimes = dessin.getEventDownTimes();
                    eventUpTimes = dessin.getEventUpTimes();
                    mImageX = dessin.getImageX();
                    mImageY = dessin.getImageY();
                    isPalm = dessin.getBooleanPalm();
                    durationTime = dessin.getDurationTime();
                    myIntent.putExtra("path", saveToInternalStorage(cartoBitmap));
                    myIntent.putExtra("tableauX", tableauX);
                    myIntent.putExtra("tableauY", tableauY);
                    myIntent.putExtra("eventUpTimes", eventUpTimes);
                    myIntent.putExtra("eventDownTimes", eventDownTimes);
                    myIntent.putExtra("mImageX",mImageX);
                    myIntent.putExtra("mImageY",mImageY);
                    myIntent.putExtra("isPalm",isPalm);
                    myIntent.putExtra("durationTime",durationTime);
                    startActivity(myIntent);
                    // On ferme l'activité en cours
                    finish();
                }
            }
        });

        move_CD = (Button) findViewById(R.id.button_move);
        move_CD.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(click_first == false){
                    dessin.getBooleanClick(true);
                    state.setText(R.string.moverect);
                    // Pour changer l'image background du bouton
                    move_CD.setBackgroundResource(R.drawable.dismoverect_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check_block);
                    // Pour afficher une avis
                    click_first = true;
                    Toast toast = Toast.makeText(context,R.string.toast_movecd,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
                    toast.show();
                }
                else{
                    dessin.getBooleanClick(false);
                    state.setText(R.string.enCours);
                    move_CD.setBackgroundResource(R.drawable.moverect_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check);
                    click_first = false;
                }
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
                            // On revient à l'écran des consignes de l'item 19
                            Intent myIntent = new Intent(do_item19.this, consignes_item19.class);
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

    //permet cacher vite le status bar (Notifications)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i(TAG, "onWindowFocusChanged()");
        try {
            if (!hasFocus) {
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                this.sendBroadcast(it);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

