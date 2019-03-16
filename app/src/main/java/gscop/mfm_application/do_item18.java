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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class do_item18 extends Activity {

    private static final String TAG = "www.gscop.mfm";
    private Button boutonTerminer;
    private final Context context = this;
    private Button move_CD;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Dessin_item18 dessin;
    private Bitmap cartoBitmap;
    // private TextView state;
    private ArrayList<ArrayList<Float>> tableauX;
    private ArrayList tableauY;
    private boolean click_first = false;
    private int varRandom;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private ArrayList isPalm;
    private Float mImageX, mImageY;
    private Long durationTime;
    //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
    private Button boutonEffacer;
    private int tellway;

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.do_item18);

        // Permet de cacher la barre de notifications et bloquer l'expansion
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //------------------------------------------------------------------

        dessin = (Dessin_item18) findViewById(R.id.drawingItem18);
        //Commented by Adriana 06/03/2018
        //  state = (TextView) findViewById(R.id.enCours);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
        }


        // //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
        // Pour le bouton "EFFACER"
        boutonEffacer = (Button) findViewById(R.id.buttonerase);
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(do_item18.this, do_item18.class);
                myIntent.putExtra("name", name);
                myIntent.putExtra("surname", surname);
                myIntent.putExtra("birthdate", birthdate);
                myIntent.putExtra("varRandom", 1);
                startActivity(myIntent);
                // On ferme l'activité en cours
                finish();
                // dessin.cleancompletedPath();
            }
        });


        // Pour le bouton "Stop"
        boutonTerminer = (Button) findViewById(R.id.buttonStop);
        boutonTerminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click_first == true) {
                    Toast toast = Toast.makeText(context, R.string.toast_blockcd, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
                    toast.show();
                } else {
                    boutonTerminer.setClickable(false);
                    // Action quand on appuie sur terminer -> affiche la cartographie
                    // Commented by Adriana 06/03/2018
                    // / state.setText(R.string.saving);
                    dessin.getPaint().setColor(Color.BLUE);
                    dessin.draw(dessin.getCanvas());


                    cartoBitmap = dessin.getCartographie();
                    tableauX = dessin.getTableauX();
                    tableauY = dessin.getTableauY();
                    Log.i(TAG, "size of tableauY = " + tableauY.size());
                    eventDownTimes = dessin.getEventDownTimes();
                    eventUpTimes = dessin.getEventUpTimes();
                    mImageX = dessin.getCdX();
                    mImageY = dessin.getCdY();
                    isPalm = dessin.getBooleanPalm();
                    durationTime = dessin.getDurationTime();
                    tellway = dessin.getnpaths();
                    // itens para a cotação automática
                    boolean test1 = dessin.gettest1();
                    boolean test2 = dessin.gettest2();
                    boolean test3 = dessin.gettest3();
                    boolean test4 = dessin.gettest4();
                    boolean test5 = dessin.gettest5();
                    boolean test6 = dessin.gettest6();
                    boolean test7 = dessin.gettest7();
                    boolean test_incomplet = dessin.gettest_incomplet();
                    // itens do nova animação;
                    my_times = dessin.getmy_times();
                    my_X = dessin.getmy_X();
                    my_Y = dessin.getmy_Y();

                    //************************new- Hiba: pour l'ajout des feedbacks ****************************************

                    if (!test1) {
                        Toast toast = Toast.makeText(context, "Tu n'as pas d'abord touché le centre, ou il n´y a pas eu de touches", Toast.LENGTH_LONG);
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
                        if (test5) {
                            Toast toast = Toast.makeText(context, "Bravo tu as réussi!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            LinearLayout toastContentView = (LinearLayout) toast.getView();
                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setImageResource(R.drawable.good); // toast avec image attention
                            toastContentView.addView(imageView, 0);
                            toastContentView.setRotation(180);
                            toast.show();
                            MediaPlayer mp = MediaPlayer.create(context,R.raw.correct_answer); //son
                            mp.start();
                        } else if (test6 || test7) {
                            if (test6) {
                                Toast toast = Toast.makeText(context, "C'est bien, mais faut faire le tour de la serrure sans t'arrêter!", Toast.LENGTH_LONG);
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
                                Toast toast = Toast.makeText(context, "C'est bien, mais faut faire le tour en un seul tracé!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk); // toast avec image attention
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer); //son
                                mp.start();
                            }
                        } else if (test2 || test3 || test4) {
                            if (test2) {
                                Toast toast = Toast.makeText(context, "Tu as fais le tour du petit cercle mais essaie le tour de la serrure! ", Toast.LENGTH_LONG); // et sans arret
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk); // toast avec image attention
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer); //son
                                mp.start();
                            } else if (test3) {
                                Toast toast = Toast.makeText(context, "Tu as fais le tour du petit cercle mais il ne faut pas t'arrêter!", Toast.LENGTH_LONG); // mais avec arret
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
                                Toast toast = Toast.makeText(context, "Fais le tour du petit cercle en un seul tracé!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk); // toast avec image attention
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer); //son
                                mp.start();
                            }
                        } else {
                            if (!test_incomplet) {
                                Toast toast = Toast.makeText(context, "Il faut faire le tour de la serrure!", Toast.LENGTH_LONG);
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
                                Toast toast = Toast.makeText(context, "Ton cercle est incomplet!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout toastContentView = (LinearLayout) toast.getView();
                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setImageResource(R.drawable.risk); // toast avec image attention
                                toastContentView.addView(imageView, 0);
                                toastContentView.setRotation(180);
                                toast.show();
                                MediaPlayer mp = MediaPlayer.create(context,R.raw.wrong_answer); //son
                                mp.start();
                            }
                        }
                    }

                        //**************************************************************************************
                        final Intent myIntent = new Intent(do_item18.this, comments_item18.class);
                        myIntent.putExtra("name", name);
                        myIntent.putExtra("surname", surname);
                        myIntent.putExtra("birthdate", birthdate);
                        myIntent.putExtra("varRandom", varRandom);
                        dessin.orderPaths();

                        myIntent.putExtra("path", saveToInternalStorage(cartoBitmap));
                        myIntent.putExtra("test1", test1);
                        myIntent.putExtra("test2", test2);
                        myIntent.putExtra("test3", test3);
                        myIntent.putExtra("test4", test4);
                        myIntent.putExtra("test5", test5);
                        myIntent.putExtra("test6", test6);
                        myIntent.putExtra("test7", test7);

                        myIntent.putExtra("tableauX", tableauX);
                        myIntent.putExtra("tableauY", tableauY);
                        myIntent.putExtra("eventUpTimes", eventUpTimes);
                        myIntent.putExtra("eventDownTimes", eventDownTimes);
                        myIntent.putExtra("mImageX", mImageX);
                        myIntent.putExtra("mImageY", mImageY);
                        myIntent.putExtra("isPalm", isPalm);
                        myIntent.putExtra("durationTime", durationTime);
                        myIntent.putExtra("adriana", tellway);
                        myIntent.putExtra("my_times", my_times);
                        myIntent.putExtra("my_X", my_X);
                        myIntent.putExtra("my_Y", my_Y);
                        myIntent.putExtra("test_incomplet", dessin.gettest_incomplet());

                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                startActivity(myIntent);
                                finish();
                            }
                        }, 3000);

                }
            }
            });





   //                     startActivity(myIntent);
                        // On ferme l'activité en cours
   //                     finish();
      //              }
    //            }
      //      });





/*      Bouton pour faire déplacer le CD. Enlever le 21/11/2018 pour valider la cotation Automatique

        move_CD = (Button) findViewById(R.id.button_move);
        move_CD.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(click_first == false){
                    dessin.getBooleanClick(true);
                    // Commented by Adriana 06/03/2018
                    // state.setText(R.string.movecd);
                    // Pour changer l'image background du bouton
                    move_CD.setBackgroundResource(R.drawable.dismovecd_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check_block);
                    // Pour afficher une avis
                    click_first = true;
                    Toast toast = Toast.makeText(context,R.string.toast_movecd,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 0);
                    toast.show();
                }
                else{
                    dessin.getBooleanClick(false);
                    // Commented by Adriana 06/03/2018
                    // state.setText(R.string.enCours);
                    move_CD.setBackgroundResource(R.drawable.movecd_bord);
                    boutonTerminer.setBackgroundResource(R.drawable.check);
                    click_first = false;
                }
            }
        });

*/

        }

        private boolean back_answer = false;
        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            return false;
        }

        /**
         * Enregistre un bitmap dans la mémoire interne de l'appareil
         *
         * @param bitmapImage l'image à enregistrer
         * @return le chemin de l'image enregistrée
         */
        private String saveToInternalStorage (Bitmap bitmapImage){
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
        public void onWindowFocusChanged ( boolean hasFocus){
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
