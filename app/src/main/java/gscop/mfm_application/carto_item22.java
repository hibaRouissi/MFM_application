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
import android.os.Bundle;
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
    private int varRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.carto_item22);

        carto = (ImageView) findViewById(R.id.cartographieItem22);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            path = intent.getStringExtra("path");
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                carto.setImageBitmap(cartoBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            tableauX = intent.getIntegerArrayListExtra("tableauX");
            tableauY = intent.getIntegerArrayListExtra("tableauY");
        }

        infosPatient = (TextView) findViewById(R.id.infosPatient);
        infosPatient.setText(" Patient : " + name + " " + surname + " \n Né(e) le : " + birthdate);

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

        // Pour le bouton "Recommencer"
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boutonRecommencer = (Button) findViewById(R.id.boutonRecommencer);
        boutonRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boutonRecommencer.setBackgroundColor(Color.GRAY);
                // Quand on clique sur le bouton recommencer, ça retourne sur l'interface do_item22
                builder.setMessage("Êtes-vous certain de vouloir recommencer l'exercice ? \n (le tracé sera perdu)")
                        .setCancelable(true)
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // On remet le bouton recommencer en bleu
                                boutonRecommencer.setBackgroundColor(getResources().getColor(R.color.myBlue));
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                // Quand on clique sur le bouton valider, ça ouvre l'interface des commentaires du kiné
                // Si varRandom = 1, on doit faire la version papier avant d'accéder aux commentaires
                if (varRandom == 1) {
                    // On demande de réaliser l'item 22 version papier
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.paper22)
                            .setTitle("MFM Papier")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent myIntent = new Intent(carto_item22.this, comments_item22.class);
                                    myIntent.putExtra("name", name);
                                    myIntent.putExtra("surname", surname);
                                    myIntent.putExtra("birthdate", birthdate);
                                    myIntent.putExtra("path", path);
                                    myIntent.putExtra("tableauX", tableauX);
                                    myIntent.putExtra("tableauY", tableauY);
                                    myIntent.putExtra("varRandom", varRandom);
                                    startActivity(myIntent);
                                    // On ferme l'activité en cours
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    // varRandom = 0 -> on lance simplement l'interface des commentaires
                    Intent myIntent = new Intent(carto_item22.this, comments_item22.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("path", path);
                    myIntent.putExtra("tableauX", tableauX);
                    myIntent.putExtra("tableauY", tableauY);
                    myIntent.putExtra("varRandom", varRandom);
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
