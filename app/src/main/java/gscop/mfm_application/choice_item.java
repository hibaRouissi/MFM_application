package gscop.mfm_application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.util.Log;
import static android.content.ContentValues.TAG;



public class choice_item extends Activity {

    private TextView textNomPrenomPatient;
    private TextView random_choix;
    private Button buttonItem18;
    private Button buttonItem19;
    private Button buttonItem22;
    private Button buttonExit;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private int varRandom;
    private final Context context = this;
    private static Boolean querosair =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "querosair= %b"+querosair);
        if (querosair){
            choice_item.this.finish();
            System.exit(0);
            finish();
        } else {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.choice_item);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            textNomPrenomPatient = (TextView) findViewById(R.id.PatientName);
            random_choix = (TextView) findViewById(R.id.random_choice);

            ActivityManager myact = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            // ActivityManager.getLockTaskModeState api is not available in pre-M.
            if (myact.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                Log.d(TAG, "locktask... ");
                startLockTask();
            }
            // On récupère les données de l'activité précédente pour afficher les données du patient
            Intent intent = getIntent();
            if (intent != null) {
                name = intent.getStringExtra("name");
                surname = intent.getStringExtra("surname");
                birthdate = intent.getStringExtra("birthdate");
                varRandom = intent.getIntExtra("varRandom", -1);
/*                if (varRandom == 0) {
                    random_choix.setText(" Choix de randomisation: PAPIER D´ABORD ");
                } else {
                    random_choix.setText(" Choix de randomisation: TABLETTE D´ABORD");
                }
*/
                textNomPrenomPatient.setText("Patient : " + name + " \nNé(e) le : " + birthdate);
            }

            // Pour le bouton "Quitter"
            buttonExit = (Button) findViewById(R.id.buttonExit);
            buttonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    querosair =true;
                    Intent myIntent = new Intent(choice_item.this, choice_item.class);
                    startActivity(myIntent);
                    // On ferme l'activité en cours
                    finish();
                }
            });


            buttonItem18 = (Button) findViewById(R.id.buttonitem18);
            buttonItem18.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // On demande la version tablette en premier : on ouvre la nouvelle interface
                    Intent myIntent = new Intent(choice_item.this, consignes_item18.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("varRandom", varRandom); // included in 12/03
                    startActivity(myIntent);
                    // On ferme l'activité en cours
                    finish();
                }
            });


            buttonItem19 = (Button) findViewById(R.id.buttonitem19);
            buttonItem19.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // On demande la version tablette en premier : on ouvre la nouvelle interface
                    Intent myIntent = new Intent(choice_item.this, consignes_item19.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("varRandom", varRandom); // included in 12/03
                    startActivity(myIntent);
                    // On ferme l'activité en cours
                    finish();
                }
            });


            buttonItem22 = (Button) findViewById(R.id.buttonitem22);
            buttonItem22.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(choice_item.this, consignes_item22.class);
                    myIntent.putExtra("name", name);
                    myIntent.putExtra("surname", surname);
                    myIntent.putExtra("birthdate", birthdate);
                    myIntent.putExtra("varRandom", varRandom);
                    startActivity(myIntent);
                    // On ferme l'activité en cours
                    finish();
                }
            });

        }
    }



    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       return false;
    }
}
