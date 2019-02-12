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

public class carto_item18 extends Activity {

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
    private Dessin_carto18 dessin_carto18;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private Float mImageX,mImageY;
    private Long durationTime;
    private int tellway;
    // variáveis de cotação automática
    private boolean test1;
    private boolean test2;
    private boolean test3;
    private boolean test4;
    private boolean test5;
    private boolean test6;
    private boolean test7;
    private boolean test_incomplet;
    private int resultat_cotation_therapeute;

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.carto_item18);

        //carto = (ImageView) findViewById(R.id.cartographieItem18);
        dessin_carto18 = (Dessin_carto18) findViewById(R.id.dessin_carto18);

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
            tellway=intent.getIntExtra("adriana",0);
            // itens da cotação automática
            test1 = intent.getBooleanExtra("test1",false);
            test2 = intent.getBooleanExtra("test2",false);
            test3 = intent.getBooleanExtra("test3",false);
            test4 = intent.getBooleanExtra("test4",false);
            test5 = intent.getBooleanExtra("test5",false);
            test6 = intent.getBooleanExtra("test6",false);
            test7 = intent.getBooleanExtra("test7",false);
            test_incomplet = intent.getBooleanExtra("test_incomplet",false);
            resultat_cotation_therapeute=intent.getIntExtra("resultat_cotation_therapeute",4);


            my_times=(ArrayList) intent.getSerializableExtra("my_times");
            my_X=(ArrayList) intent.getSerializableExtra("my_X");
            my_Y=(ArrayList) intent.getSerializableExtra("my_Y");

            durationTime = intent.getLongExtra("durationTime", 0);
            dessin_carto18.getTabX(tableauX);
            dessin_carto18.getTabY(tableauY);
            dessin_carto18.getEventUpTimes(eventUpTimes);
            dessin_carto18.getEventDownTimes(eventDownTimes);
            dessin_carto18.getCdPosition(mImageX, mImageY);
            dessin_carto18.getIsPalm(isPalm);

            dessin_carto18.getmy_times(my_times);
            dessin_carto18.getmy_X(my_X);
            dessin_carto18.getmy_Y(my_Y);

            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                //carto.setImageBitmap(cartoBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Pour le bouton "Valider"
        boutonValider = (Button) findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 boutonValider.setBackgroundColor(Color.GRAY);

                                                 Intent myIntent = new Intent(carto_item18.this, comments_item18.class);
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
                                                 myIntent.putExtra("Gomes", tellway);
                                                 // itens da cotação automática
                                                 myIntent.putExtra("test1", test1);
                                                 myIntent.putExtra("test2", test2);
                                                 myIntent.putExtra("test3", test3);
                                                 myIntent.putExtra("test4", test4);
                                                 myIntent.putExtra("test5", test5);
                                                 myIntent.putExtra("test6", test6);
                                                 myIntent.putExtra("test7", test7);
                                                 myIntent.putExtra("test_incomplet", test_incomplet);
                                                 myIntent.putExtra("my_times",my_times);
                                                 myIntent.putExtra("my_X",my_X);
                                                 myIntent.putExtra("my_Y",my_Y);
                                                 myIntent.putExtra("resultat_cotation_therapeute",resultat_cotation_therapeute);
                                                 startActivity(myIntent);
                                                 // On ferme l'activité en cours
                                                 finish();


                                             }
                                         });

        infosPatient = (TextView) findViewById(R.id.infosPatient);
        Log.d(TAG," duration : " + durationTime);
        infosPatient.setText("Patient : " + name + " " + surname + " \nDurée : " + durationTime/1000 + " secondes ");

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
                                carto_item18.this.finish();
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

/*        // Pour le bouton "Recommencer"
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boutonRecommencer = (Button) findViewById(R.id.boutonRecommencer);
        boutonRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonRecommencer.setBackgroundColor(Color.GRAY);
                // Quand on clique sur le bouton recommencer, on retourne sur l'interface do_item18
                builder.setMessage("Êtes-vous certain de vouloir recommencer l'exercice ? \n (le tracé sera perdu)")
                        .setCancelable(false)
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // On remet le bouton recommencer en bleu
                                boutonRecommencer.setBackgroundColor(getResources().getColor(R.color.myBlue));
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  On revient à l'écran de réalisation de l'item 18
                                Intent myIntent = new Intent(carto_item18.this, do_item18.class);
                                myIntent.putExtra("name", name);
                                myIntent.putExtra("surname", surname);
                                myIntent.putExtra("birthdate", birthdate);
                                myIntent.putExtra("varRandom", varRandom);
                                startActivity(myIntent);
                                // On ferme l'activité en cours
                                finish();
                            }
                        });
                        AlertDialog alert = builder.create();
                alert.show();
            }
        });
*/

/*
        // Pour le bouton "Valider"
        boutonValider = (Button) findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonValider.setBackgroundColor(Color.GRAY);
                // Quand on clique sur le bouton valider, on ouvre l'interface des commentaires du kiné
                // Si varRandom = 1, on doit faire la version papier avant d'accéder aux commentaires
                varRandom =0; // inserte on 01/03
                if (varRandom == 1) {
                    // On demande de réaliser l'item 18 version papier
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.paper18)
                            .setTitle("MFM Papier")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent myIntent = new Intent(carto_item18.this, comments_item18.class);
                                    myIntent.putExtra("name", name);
                                    myIntent.putExtra("surname", surname);
                                    myIntent.putExtra("birthdate", birthdate);
                                    myIntent.putExtra("path", path);
                                    myIntent.putExtra("tableauX", tableauX);
                                    myIntent.putExtra("tableauY", tableauY);
                                    myIntent.putExtra("varRandom", varRandom);
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
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    // varRandom = 0 -> on lance simplement l'interface des commentaires
                    Intent myIntent = new Intent(carto_item18.this, comments_item18.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("path", path);
                    myIntent.putExtra("tableauX", tableauX);
                    myIntent.putExtra("tableauY", tableauY);
                    myIntent.putExtra("varRandom", varRandom);
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

        */





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
                            // On revient à l'écran de réalisation de l'item 18
                            Intent myIntent = new Intent(carto_item18.this, do_item18.class);
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

