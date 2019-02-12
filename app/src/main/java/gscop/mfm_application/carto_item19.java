package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class carto_item19 extends Activity {

    private static final String TAG = "g.scop.mfm_application" ;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Button buttonExit;
    private Button boutonRecommencer;
    private Button boutonValider;
    private TextView infosPatient;
    private String path = "";
    private Bitmap cartoBitmap;
    private ImageView carto;
    private final Context context = this;
    private ArrayList tableauX;
    private ArrayList tableauY;
    private ArrayList isPalm;
    private int varRandom;
    private Dessin_carto19 dessin_carto19;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private Float mImageX,mImageY;
    private Long durationTime;
    //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
    private Float mImage2X,mImage2Y;

    private boolean touched_inside;
    private int demi_boucles;
    private int max_within_range;
    private int min_within_range;
    private int max_outside_window;
    private int min_outside_window;
    private ArrayList<Integer> max_x = new ArrayList<>();
    private ArrayList<Integer> min_x = new ArrayList<>();
    private int resultat_cotation_automatique;
    private boolean cotation_3;
    private boolean last_touch_ok;
    private boolean first_touch_ok;
    private boolean firsttouch_in_tray;
    private boolean lasttouch_in_tray;

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();
    private boolean test_velocity;
    private int missed_turns;
    private int number_of_traces;
    private int resultat_cotation_therapeute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.carto_item19);

        //carto = (ImageView) findViewById(R.id.cartographieItem19);
        dessin_carto19 = (Dessin_carto19) findViewById(R.id.dessin_carto19);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            path = intent.getStringExtra("path");
            tableauX = intent.getIntegerArrayListExtra("tableauX");
            tableauY = intent.getIntegerArrayListExtra("tableauY");
            eventUpTimes = (ArrayList) intent.getSerializableExtra("eventUpTimes");
            mImageY = intent.getFloatExtra("mImageY", 0f);
            isPalm = (ArrayList) intent.getSerializableExtra("isPalm");
            eventDownTimes = (ArrayList) intent.getSerializableExtra("eventDownTimes");
            mImageX = intent.getFloatExtra("mImageX", 0f);
            //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
            mImage2X = intent.getFloatExtra("mImage2X", 0f);
            mImage2Y = intent.getFloatExtra("mImage2Y", 0f);
            durationTime = intent.getLongExtra("durationTime", 0);

            touched_inside = intent.getBooleanExtra("touched_inside", false);
            demi_boucles = intent.getIntExtra("demi_boucles", 0);
            max_within_range = intent.getIntExtra("max_within_range", 0);
            min_within_range = intent.getIntExtra("min_within_range", 0);
            max_outside_window = intent.getIntExtra("max_outside_window", 0);
            min_outside_window = intent.getIntExtra("min_outside_window", 0);
            missed_turns = intent.getIntExtra("missed_turns", 0);
            max_x = intent.getIntegerArrayListExtra("max_x");
            min_x = intent.getIntegerArrayListExtra("min_x");
            cotation_3 = intent.getBooleanExtra("cotation_3", false);
            last_touch_ok = intent.getBooleanExtra("last_touch_ok", false);
            first_touch_ok = intent.getBooleanExtra("first_touch_ok", false);
            firsttouch_in_tray = intent.getBooleanExtra("firsttouch_in_tray", false);
            lasttouch_in_tray = intent.getBooleanExtra("lasttouch_in_tray", false);

            my_times=(ArrayList) intent.getSerializableExtra("my_times");
            my_X=(ArrayList) intent.getSerializableExtra("my_X");
            my_Y=(ArrayList) intent.getSerializableExtra("my_Y");
            test_velocity=intent.getBooleanExtra("test_velocity",false);
            number_of_traces= intent.getIntExtra("number_of_traces",0);
            resultat_cotation_therapeute=intent.getIntExtra("resultat_cotation_therapeute",4);

            dessin_carto19.getTabX(tableauX);
            dessin_carto19.getTabY(tableauY);
            dessin_carto19.getEventUpTimes(eventUpTimes);
            dessin_carto19.getEventDownTimes(eventDownTimes);
            dessin_carto19.getCdPosition(mImageX, mImageY, mImage2X, mImage2Y);

            dessin_carto19.getmy_times(my_times);
            dessin_carto19.getmy_X(my_X);
            dessin_carto19.getmy_Y(my_Y);



            //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
            //dessin_carto19.getCdPosition2x(mImage2X,mImage2Y);
            dessin_carto19.getIsPalm(isPalm);
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                //carto.setImageBitmap(cartoBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        infosPatient = (TextView) findViewById(R.id.infosPatient);
        Log.d(TAG, " duration : " + durationTime);
        infosPatient.setText("Patient : " + name + " " + surname + " \nDurée : " + durationTime / 1000 + " secondes ");

        // Pour le bouton "Quitter"
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Êtes-vous certain de vouloir quitter l'application ?")
                        .setCancelable(true)
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // On quitte l'application courante
                                carto_item19.this.finish();
                                System.exit(0);
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // Pour le bouton "Valider"
        boutonValider = (Button) findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonValider.setBackgroundColor(Color.GRAY);
                Intent myIntent = new Intent(carto_item19.this, comments_item19.class);
                myIntent.putExtra("name", name);
                myIntent.putExtra("surname", surname);
                myIntent.putExtra("birthdate", birthdate);
                myIntent.putExtra("path", path);
                myIntent.putExtra("tableauX", tableauX);
                myIntent.putExtra("tableauY", tableauY);
                myIntent.putExtra("varRandom", varRandom);
                myIntent.putExtra("eventUpTimes", eventUpTimes);
                myIntent.putExtra("eventDownTimes", eventDownTimes);
                myIntent.putExtra("mImageX", mImageX);
                myIntent.putExtra("mImageY", mImageY);
                //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
                myIntent.putExtra("mImage2X", mImage2X);
                myIntent.putExtra("mImage2Y", mImage2Y);
                myIntent.putExtra("isPalm", isPalm);
                myIntent.putExtra("durationTime", durationTime);
                myIntent.putExtra("touched_inside",touched_inside);
                myIntent.putExtra("demi_boucles",demi_boucles);
                myIntent.putExtra("max_within_range",max_within_range);
                myIntent.putExtra("min_within_range",min_within_range);
                myIntent.putExtra("max_outside_window",max_outside_window);
                myIntent.putExtra("min_outside_window",min_outside_window);
                myIntent.putExtra("min_outside_window",min_outside_window);
                myIntent.putExtra("max_x",max_x);
                myIntent.putExtra("min_x",min_x);
                myIntent.putExtra("cotation_3",cotation_3);
                myIntent.putExtra("last_touch_ok",cotation_3);
                myIntent.putExtra("first_touch_ok",cotation_3);
                myIntent.putExtra("firsttouch_in_tray",firsttouch_in_tray);
                myIntent.putExtra("lasttouch_in_tray",lasttouch_in_tray);

                myIntent.putExtra("my_times",my_times);
                myIntent.putExtra("my_X",my_X);
                myIntent.putExtra("my_Y",my_Y);
                myIntent.putExtra("test_velocity",test_velocity);
                myIntent.putExtra("missed_turns",missed_turns);
                myIntent.putExtra("number_of_traces",number_of_traces);
                myIntent.putExtra("resultat_cotation_therapeute",resultat_cotation_therapeute);

                startActivity(myIntent);
                // On ferme l'activité en cours
                finish();
            }
        });
    }
    // Quand on appuie sur la touche retour de la tablette -> comme pour le bouton recommencer
    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Êtes-vous certain de vouloir recommencer l'exercice ? \n (le tracé sera perdu)")
                    .setCancelable(true)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            back_answer = true;
                            // On revient à l'écran de réalisation de l'item 19
                            Intent myIntent = new Intent(carto_item19.this, do_item19.class);
                            myIntent.putExtra("name", name);
                            myIntent.putExtra("surname", surname);
                            myIntent.putExtra("birthdate", birthdate);
                            myIntent.putExtra("varRandom", varRandom);
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

}
