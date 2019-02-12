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

public class carto_item22 extends Activity {

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
    private Dessin_carto22 dessin_carto22;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private Float mImageX,mImageY;
    private Long durationTime;
    // inserted on 28/02
    private ArrayList xDownList;
    private ArrayList yDownList;


    private boolean test1 ;// si le premier contact est au centre de l'image
    private boolean test2 ; //si le patient a levé son doigt après le premier contact au centre
    private boolean digitou_1 ;
    private boolean digitou_2 ;
    private boolean digitou_3 ;
    private boolean digitou_4 ;
    private boolean digitou_5 ;
    private boolean digitou_6 ;
    private boolean digitou_7 ;
    private boolean digitou_8 ;
    private boolean digitou_9 ;
    private boolean digitou_10 ; // out of design

    private boolean cruzou_1 ;
    private boolean cruzou_2 ;
    private boolean cruzou_3 ;
    private boolean cruzou_4 ;
    private boolean cruzou_5 ;
    private boolean cruzou_6 ;
    private boolean cruzou_7 ;
    private boolean cruzou_8 ;
    private boolean cruzou_9 ;
    private boolean cruzou_10 ; // out of design
    private boolean test_line_ok;

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();
    private int resultat_cotation_therapeute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.carto_item22);

        dessin_carto22 = (Dessin_carto22) findViewById(R.id.dessin_carto22);

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
            eventDownTimes = (ArrayList) intent.getSerializableExtra("eventDownTimes");
            mImageX = intent.getFloatExtra("mImageX", 0f);
            mImageY = intent.getFloatExtra("mImageY", 0f);
            isPalm = (ArrayList) intent.getSerializableExtra("isPalm");
            durationTime = intent.getLongExtra("durationTime", 0);
            // inserted on 28/02
            xDownList = (ArrayList) intent.getSerializableExtra("xDownList");
            yDownList = (ArrayList) intent.getSerializableExtra("yDownList");
            dessin_carto22.setxDownList(xDownList);
            dessin_carto22.setyDownList(yDownList);
            //28/02 until here
            dessin_carto22.getTabX(tableauX);
            dessin_carto22.getTabY(tableauY);
            dessin_carto22.getEventUpTimes(eventUpTimes);
            dessin_carto22.getEventDownTimes(eventDownTimes);
            dessin_carto22.getCdPosition(mImageX, mImageY);
            dessin_carto22.getIsPalm(isPalm);
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
            resultat_cotation_therapeute=intent.getIntExtra("resultat_cotation_therapeute",4);

            my_times=(ArrayList) intent.getSerializableExtra("my_times");
            my_X=(ArrayList) intent.getSerializableExtra("my_X");
            my_Y=(ArrayList) intent.getSerializableExtra("my_Y");
            dessin_carto22.getmy_times(my_times);
            dessin_carto22.getmy_X(my_X);
            dessin_carto22.getmy_Y(my_Y);


            test1 = intent.getBooleanExtra("test1", false);
            test2 = intent.getBooleanExtra("test2", false);
            digitou_1 = intent.getBooleanExtra("digitou_1", false);
            digitou_2 = intent.getBooleanExtra("digitou_2", false);
            digitou_3 = intent.getBooleanExtra("digitou_3", false);
            digitou_4 = intent.getBooleanExtra("digitou_4", false);
            digitou_5 = intent.getBooleanExtra("digitou_5", false);
            digitou_6 = intent.getBooleanExtra("digitou_6", false);
            digitou_7 = intent.getBooleanExtra("digitou_7", false);
            digitou_8 = intent.getBooleanExtra("digitou_8", false);
            digitou_9 = intent.getBooleanExtra("digitou_9", false);
            digitou_10 = intent.getBooleanExtra("digitou_10", false);
            cruzou_1 = intent.getBooleanExtra("cruzou_1", false);
            cruzou_2 = intent.getBooleanExtra("cruzou_2", false);
            cruzou_3 = intent.getBooleanExtra("cruzou_3", false);
            cruzou_4 = intent.getBooleanExtra("cruzou_4", false);
            cruzou_5 = intent.getBooleanExtra("cruzou_5", false);
            cruzou_6 = intent.getBooleanExtra("cruzou_6", false);
            cruzou_7 = intent.getBooleanExtra("cruzou_7", false);
            cruzou_8 = intent.getBooleanExtra("cruzou_8", false);
            cruzou_9 = intent.getBooleanExtra("cruzou_9", false);
            cruzou_10 = intent.getBooleanExtra("cruzou_10", false);
            test_line_ok = intent.getBooleanExtra("test_line_ok", false);

            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
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
                                carto_item22.this.finish();
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
                Intent myIntent = new Intent(carto_item22.this, comments_item22.class);
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
                myIntent.putExtra("isPalm", isPalm);
                myIntent.putExtra("durationTime", durationTime);

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
                myIntent.putExtra("xDownList", xDownList);
                myIntent.putExtra("yDownList", yDownList);
                myIntent.putExtra("test_line_ok",test_line_ok);

                myIntent.putExtra("my_times",my_times);
                myIntent.putExtra("my_X",my_X);
                myIntent.putExtra("my_Y",my_Y);
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
                            // On revient à l'écran de réalisation de l'item 22
                            Intent myIntent = new Intent(carto_item22.this, do_item22.class);
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
