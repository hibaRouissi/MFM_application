package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.view.MotionEvent;


public class do_item22 extends Activity {

    private static final String TAG = "www.gscop.mfm";
    private final Context context = this;
    private Button boutonTerminer;
    private Button move_quad;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Dessin_item22 dessin;
    private Bitmap cartoBitmap;
    private TextView state;
    private ArrayList tableauX;
    private ArrayList tableauY;
    private boolean click_first = false;
    private int varRandom;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private ArrayList isPalm;
    private Float mImageX, mImageY;
    private Long durationTime;
    // inserted on 28/02
    private ArrayList xDownList;
    private ArrayList yDownList;
    private TextView infotellwaytext;


    //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
    private Button boutonEffacer;

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();
    private boolean back_answer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.do_item22);

        // Permet de cacher la barre de notifications et bloquer l'expansion
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //------------------------------------------------------------------

        dessin = (Dessin_item22) findViewById(R.id.drawingItem22);
        //Commented by Adriana 06/03/2018
        //state = (TextView) findViewById(R.id.enCours);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
        }

        //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
        // Pour le bouton "EFFACER"
        boutonEffacer = (Button) findViewById(R.id.buttonerase);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(do_item22.this, do_item22.class);
                myIntent.putExtra("name", name);
                myIntent.putExtra("surname", surname);
                myIntent.putExtra("birthdate", birthdate);
                myIntent.putExtra("varRandom", 1);
                startActivity(myIntent);
                // On ferme l'activité en cours
                finish();

                //dessin.cleancompletedPath();
            }
        });

        // Pour le bouton "Stop"
        boutonTerminer = (Button) findViewById(R.id.buttonStop);
        boutonTerminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click_first == true) {
                    Toast toast = Toast.makeText(context, R.string.toast_blockquad, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    boutonTerminer.setClickable(false);
                    // Action quand on appuie sur terminer -> affiche la cartographie
                    //Commented by Adriana 06/03/2018
                    // state.setText(R.string.saving);
                    dessin.getPaint().setColor(Color.BLUE);
                    dessin.draw(dessin.getCanvas());

                    //récupérer les données de la page Dessin_item22
                    cartoBitmap = dessin.getCartographie();
                    tableauX = dessin.getTableauX();
                    tableauY = dessin.getTableauY();
                    eventDownTimes = dessin.getEventDownTimes();
                    eventUpTimes = dessin.getEventUpTimes();
                    mImageX = dessin.getQX();
                    mImageY = dessin.getQY();
                    isPalm = dessin.getBooleanPalm();
                    durationTime = dessin.getDurationTime();
                    // inserted on 28/02
                    xDownList = dessin.getxDownList();
                    yDownList = dessin.getyDownList();
                    boolean test1 = dessin.gettest1();
                    boolean test2 = dessin.gettest2();
                    boolean test_line_ok = dessin.get_test_line_ok();
                    boolean digitou_1 = dessin.getdigitou_1();
                    boolean digitou_2 = dessin.getdigitou_2();
                    boolean digitou_3 = dessin.getdigitou_3();
                    boolean digitou_4 = dessin.getdigitou_4();
                    boolean digitou_5 = dessin.getdigitou_5();
                    boolean digitou_6 = dessin.getdigitou_6();
                    boolean digitou_7 = dessin.getdigitou_7();
                    boolean digitou_8 = dessin.getdigitou_8();
                    boolean digitou_9 = dessin.getdigitou_9();
                    boolean digitou_10 = dessin.getdigitou_10(); // out of design
                    boolean cruzou_1 = dessin.getcruzou_1();
                    boolean cruzou_2 = dessin.getcruzou_2();
                    boolean cruzou_3 = dessin.getcruzou_3();
                    boolean cruzou_4 = dessin.getcruzou_4();
                    boolean cruzou_5 = dessin.getcruzou_5();
                    boolean cruzou_6 = dessin.getcruzou_6();
                    boolean cruzou_7 = dessin.getcruzou_7();
                    boolean cruzou_8 = dessin.getcruzou_8();
                    boolean cruzou_9 = dessin.getcruzou_9();
                    boolean cruzou_10 = dessin.getcruzou_10(); // out of design

                    // itens do nova animação;
                    my_times = dessin.getmy_times();
                    my_X = dessin.getmy_X();
                    my_Y = dessin.getmy_Y();

                    //*********************New - Hiba: pour l'ajout des feedbacks*******************
                    if (!test1) {
                        Toast toast = Toast.makeText(context, "Touche d'abord le centre de la figure!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        LinearLayout toastContentView = (LinearLayout) toast.getView();
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageResource(R.drawable.risk); // toast avec image attention
                        toastContentView.addView(imageView, 0);
                        toastContentView.setRotation(180);
                        toast.show();
                        MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer); //son
                        mp.start();
                    } else {

                        if (test_line_ok && test2 && digitou_1 && digitou_2 && digitou_3 && digitou_4 && digitou_5 &&
                                digitou_6 && digitou_7 && digitou_8 && digitou_9 && !digitou_10 && !cruzou_1 &&
                                !cruzou_2 && !cruzou_3 && !cruzou_4 && !cruzou_5 && !cruzou_6 && !cruzou_7 &&
                                !cruzou_8 && !cruzou_9 && !cruzou_10) {
                            Toast toast = Toast.makeText(context, "Bravo! Le code est bon tu peux lancer l'appel!", Toast.LENGTH_LONG);
                           // Toast toast = new Toast(context);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastContentView = (LinearLayout) toast.getView();
                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setImageResource(R.drawable.good); //image avec smiley
                          //  TextView tv= new TextView(getApplicationContext());
                         //   tv.setText("Le code est bon ! Bien joué !");
                        //    tv.setTextSize(40);
                           toastContentView.addView(imageView, 0);
                        //    toastContentView.addView(tv, 1);
                            toastContentView.setRotation(180);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(context,R.raw.correct_answer);
                            mp.start();

                        } else if (digitou_1 || digitou_2 || digitou_3 || digitou_4 ||
                                digitou_6 || digitou_7 || digitou_8 ||
                                digitou_9) {

                            if (cruzou_1 || cruzou_2 || cruzou_3 || cruzou_4 || cruzou_5 || cruzou_6 ||
                                    cruzou_7 || cruzou_8 || cruzou_9) {
                                Toast toast = Toast.makeText(context, "Attention à ne pas faire glisser ton doigt!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk);
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer);
                                mp.start();

                            } else if (!test_line_ok) {
                                Toast toast = Toast.makeText(context, "Attention à bien toucher le centre de toutes les cases!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk);
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer);
                                mp.start();

                            } else {
                                Toast toast = Toast.makeText(context, "Il faut toucher tous les dessins!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk);
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer);
                                mp.start();
                            }
                        } else if (cruzou_1 || cruzou_2 || cruzou_3 ||
                                cruzou_4 || cruzou_5 || cruzou_6 || cruzou_7 ||
                                cruzou_8 || cruzou_9) {
                            Toast toast = Toast.makeText(context, "Attention à ne pas toucher le quadrillage!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastContentView = (LinearLayout) toast.getView();
                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setImageResource(R.drawable.risk);
                            toastContentView.addView(imageView, 0);
                            toastContentView.setRotation(180);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer);
                            mp.start();
                        } else if (digitou_5 && !digitou_1 && !digitou_2 && !digitou_3 && !digitou_4 &&
                                !digitou_6 && !digitou_7 && !digitou_8 && !digitou_9) {
                            Toast toast = Toast.makeText(context, "Il faut toucher tous les dessins autour du centre!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastContentView = (LinearLayout) toast.getView();
                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setImageResource(R.drawable.risk);
                            toastContentView.addView(imageView, 0);
                            toastContentView.setRotation(180);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer);
                            mp.start();
                        }
                    }
                    //*****************************************************************************************************
                    //passage page suivante
                    final Intent myIntent = new Intent(do_item22.this, comments_item22.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("varRandom", varRandom);
                    dessin.orderPaths();

                    myIntent.putExtra("path", saveToInternalStorage(cartoBitmap));
                    myIntent.putExtra("tableauX", tableauX);
                    myIntent.putExtra("tableauY", tableauY);
                    myIntent.putExtra("eventUpTimes", eventUpTimes);
                    myIntent.putExtra("eventDownTimes", eventDownTimes);
                    myIntent.putExtra("mImageX", mImageX);
                    myIntent.putExtra("mImageY", mImageY);
                    myIntent.putExtra("isPalm", isPalm);
                    myIntent.putExtra("durationTime", durationTime);
                    // inserted on 28/02
                    myIntent.putExtra("xDownList", xDownList);
                    myIntent.putExtra("yDownList", yDownList);
                    myIntent.putExtra("test1", test1);
                    myIntent.putExtra("test2", test2);
                    myIntent.putExtra("digitou_1", digitou_1);
                    myIntent.putExtra("digitou_2", digitou_2);
                    myIntent.putExtra("digitou_3", digitou_3);
                    myIntent.putExtra("digitou_4", digitou_4);
                    myIntent.putExtra("digitou_5", digitou_5);
                    myIntent.putExtra("digitou_6", digitou_6);
                    myIntent.putExtra("digitou_7", digitou_7);
                    myIntent.putExtra("digitou_8", digitou_8);
                    myIntent.putExtra("digitou_9", digitou_9);
                    myIntent.putExtra("digitou_10", digitou_10);
                    myIntent.putExtra("cruzou_1", cruzou_1);
                    myIntent.putExtra("cruzou_2", cruzou_2);
                    myIntent.putExtra("cruzou_3", cruzou_3);
                    myIntent.putExtra("cruzou_4", cruzou_4);
                    myIntent.putExtra("cruzou_5", cruzou_5);
                    myIntent.putExtra("cruzou_6", cruzou_6);
                    myIntent.putExtra("cruzou_7", cruzou_7);
                    myIntent.putExtra("cruzou_8", cruzou_8);
                    myIntent.putExtra("cruzou_9", cruzou_9);
                    myIntent.putExtra("cruzou_10", cruzou_10);
                    myIntent.putExtra("test_line_ok", dessin.get_test_line_ok());
                    myIntent.putExtra("my_times", my_times);
                    myIntent.putExtra("my_X", my_X);
                    myIntent.putExtra("my_Y", my_Y);
                    // startActivity(myIntent);

                    // On ferme l'activité en cours après 20 secondes (pour avoir le temps d'afficher le feedback)

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            startActivity(myIntent);
                            finish();
                        }
                    }, 4000);
                }
            }
        });

        move_quad = (Button) findViewById(R.id.button_move);
        move_quad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click_first == false) {
                    dessin.getBooleanClick(true);
                    //Commented by Adriana 06/03/2018
                    // state.setText(R.string.movequad);
                    // Pour changer l'image background du bouton
                    move_quad.setBackgroundResource(R.drawable.dismovequad_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check_block);
                    // Pour afficher une avis
                    click_first = true;
                    Toast toast = Toast.makeText(context, R.string.toast_movequad, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    dessin.getBooleanClick(false);
                    //state.setText(R.string.enCours);
                    move_quad.setBackgroundResource(R.drawable.movequad_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check);
                    click_first = false;
                }
            }
        });

        //inserted by Hiba


        // infotellwaytext = (TextView) findViewById(R.id.tellwaytext);

    /*    dessin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean test1 = dessin.gettest1();
                if (!test1) {
                    dessin.getBooleanClick(true);
                    // infotellwaytext.setText("La première touche n´as pas été au centre du dessin, ou il n´y a pas eu des touches");
                   // Toast.makeText(getApplicationContext(), "La première touche n´as pas été au centre du dessin, ou il n´y a pas eu des touches", Toast.LENGTH_LONG).show();

                } else {

                    //  infotellwaytext.setText("La première touche n´as pas été au centre du dessin, ou il n´y a pas eu des touches");
                  //  Toast.makeText(getApplicationContext(), "cool", Toast.LENGTH_LONG).show();


                }
            }
        }); */

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
