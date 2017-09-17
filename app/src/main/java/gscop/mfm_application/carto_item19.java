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
            eventDownTimes = (ArrayList) intent.getSerializableExtra("eventDownTimes");
            mImageX = intent.getFloatExtra("mImageX",0f);
            mImageY = intent.getFloatExtra("mImageY",0f);
            isPalm = (ArrayList) intent.getSerializableExtra("isPalm");
            durationTime = intent.getLongExtra("durationTime",0);
            dessin_carto19.getTabX(tableauX);
            dessin_carto19.getTabY(tableauY);
            dessin_carto19.getEventUpTimes(eventUpTimes);
            dessin_carto19.getEventDownTimes(eventDownTimes);
            dessin_carto19.getCdPosition(mImageX,mImageY);
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

        // Pour le bouton "Recommencer"
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boutonRecommencer = (Button) findViewById(R.id.boutonRecommencer);
        boutonRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonRecommencer.setBackgroundColor(Color.GRAY);
                // Quand on clique sur le bouton recommencer, on retourne sur l'interface do_item19
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
                                //  On revient à l'écran de réalisation de l'item 19
                                Intent myIntent = new Intent(carto_item19.this, do_item19.class);
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

        // Pour le bouton "Valider"
        boutonValider = (Button) findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonValider.setBackgroundColor(Color.GRAY);
                // Quand on clique sur le bouton valider, on ouvre l'interface des commentaires du kiné
                // Si varRandom = 1, on doit faire la version papier avant d'accéder aux commentaires
                if (varRandom == 1) {
                    // On demande de réaliser l'item 19 version papier
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.paper19)
                            .setTitle("MFM Papier")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
