package gscop.mfm_application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class ouverture_appli extends Activity implements View.OnClickListener {

    private EditText nomEntre;
    private EditText Birthday;
    private EditText Birthmonth;
    private EditText Birthyear;
//    private EditText prenomEntre;
    private TextView texteDate;
    private String birthdate = null;
    private Button boutonValider;
    private Button boutonEffacer;
    private Button buttonExit;
    private Button boutonPatient;
    private final Context context = this;
    private String name;
    private String day;
    private String month;
    private String year;
    private String surname= "";
    private TextView tvDisplayDate;
    private Calendar cal;
    private int varRandom;
    private static final int DATE_DIALOG_ID = 999;
    private Button btnChangeDate;
    private boolean chosenDate = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ouverture_appli);


        //startLockTask();
        // On utilise la méthode findViewById pour récupérer les éléments de la vue
        // R est la classe qui contient les ressources
        boutonValider = (Button) findViewById(R.id.boutonvalider);
        //boutonEffacer = (Button) findViewById(R.id.buttonerase);

        nomEntre = (EditText) findViewById(R.id.nom);
        nomEntre.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                int tamanho = nomEntre.getText().toString().length();
                if (tamanho==6) {
                    String text = nomEntre.getText().toString();
                    try {
                        int num = Integer.parseInt(text);
                        nomEntre.clearFocus();
                        Birthday.requestFocus();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                        nomEntre.setError("Que des chiffres !");
                        nomEntre.requestFocus();
                    }
                }
            }
        });
        Birthday = (EditText) findViewById(R.id.birthday);
        Birthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                int tamanho = Birthday.getText().toString().length();
                if (tamanho==2) {
                    String text = Birthday.getText().toString();
                    try {
                        int num = Integer.parseInt(text);
                        Birthday.clearFocus();
                        Birthmonth.requestFocus();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                        Birthday.setError("Que des chiffres !");
                        Birthday.requestFocus();
                    }
                }
            }
        });

        Birthmonth = (EditText) findViewById(R.id.birthmonth);
        Birthmonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                int tamanho = Birthmonth.getText().toString().length();
                if (tamanho==2) {
                    String text = Birthmonth.getText().toString();
                    try {
                        int num = Integer.parseInt(text);
                        Birthmonth.clearFocus();
                        Birthyear.requestFocus();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                        Birthmonth.setError("Que des chiffres !");
                        Birthmonth.requestFocus();
                    }
                }
            }
        });
        Birthyear = (EditText) findViewById(R.id.birthyear);
        Birthyear.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                int tamanho = Birthyear.getText().toString().length();
                if (tamanho==4) {
                    String text = Birthyear.getText().toString();
                    try {
                        int num = Integer.parseInt(text);
                        View view = ouverture_appli.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                        Birthyear.setError("Que des chiffres !");
                        Birthyear.requestFocus();
                    }
                }
            }
        });


        //texteDate = (TextView) findViewById(R.id.texteBirthdate);
        // On met un listener qui regarde quand on clique sur le bouton
        // Pour le bouton valider
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère le nom, le prénom et la date de naissance
                boolean ident_ok = false;
                boolean day_ok = false;
                boolean month_ok = false;
                boolean year_ok = false;

                name = nomEntre.getText().toString();
                int length_name = name.length();
                if (length_name == 6) {
                    try {
                        int num = Integer.parseInt(name);
                        ident_ok = true;
                    } catch (NumberFormatException e) {}
                }
                if (ident_ok == false) {
                    Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                    nomEntre.setError("Remplir correctement l´identifiant !");
                    nomEntre.requestFocus();
                }

                day  = Birthday.getText().toString();
                int length_day = day.length();
                if (length_day == 2) {
                    try {
                        int num = Integer.parseInt(day);
                        day_ok = true;
                    } catch (NumberFormatException e) {}
                }
                if (day_ok == false) {
                    Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                    Birthday.setError("Remplir correctement le jour !");
                    Birthday.requestFocus();
                }


                month  = Birthmonth.getText().toString();
                int length_month = month.length();
                if (length_month == 2) {
                    try {
                        int num = Integer.parseInt(month);
                        month_ok = true;
                    } catch (NumberFormatException e) {}
                }
                if (month_ok == false) {
                    Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                    Birthmonth.setError("Remplir correctement le mois !");
                    Birthmonth.requestFocus();
                }


                year  = Birthyear.getText().toString();
                int length_year = year.length();
                if (length_year == 4) {
                    try {
                        int num = Integer.parseInt(year);
                        year_ok = true;
                    } catch (NumberFormatException e) {}
                }
                if (year_ok == false) {
                    Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                    Birthyear.setError("Remplir correctement l'année !");
                    Birthyear.requestFocus();
                }


                if ((ident_ok == true) & (day_ok == true) & (month_ok == true) & (year_ok == true)) {

                    try {
/*
                        try {
                            File randomfolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"random");
                            if (!randomfolder.exists()) {
                                randomfolder.mkdirs();
                            }
                            String filePath = randomfolder.toString() + "/random.txt";
                            File randomfile = new File(filePath);

                            boolean teste = randomfile.exists();
                            if (teste){
                                String[] lines = new String[1000];
                                BufferedReader br = new BufferedReader(new FileReader(randomfile));
                                String line_temp;
                                int j=0;
                                while ((line_temp = br.readLine()) != null) {
                                    lines[j]=line_temp;
                                    j++;
                                }
                                j=j-1;
                                br.close();
                                FileWriter writer = new FileWriter(randomfile, true);
                                int complete_sets= Math.round(j/4);
                                int uncomplete_set=j-complete_sets*4;
                                if ((uncomplete_set==0) ||(uncomplete_set==1)) {
                                    varRandom = (int) Math.floor(Math.random()*2);
                                    writer.append(varRandom+"\n");
                                } else if (uncomplete_set==2) {
                                    int a= Integer.parseInt(lines[j]);
                                    int b= Integer.parseInt(lines[j-1]);
                                    if (a==b){
                                        varRandom = (int) 1 - Integer.parseInt(lines[j]);
                                        writer.append(varRandom+"\n");
                                    } else {
                                        varRandom = (int) Math.floor(Math.random()*2);
                                        writer.append(varRandom+"\n");
                                    }
                                } else {// uncomplete_set=3
                                    int a= Integer.parseInt(lines[j]);
                                    int b= Integer.parseInt(lines[j-1]);
                                    int c= Integer.parseInt(lines[j-2]);
                                    if ((a==b) || (a==c)) {
                                        varRandom = (int) 1 - Integer.parseInt(lines[j]);
                                        writer.append(varRandom + "\n");
                                    } else {
                                        varRandom = (int) 1 - Integer.parseInt(lines[j-1]);
                                        writer.append(varRandom + "\n");
                                    }
                                }
                                writer.flush();
                                writer.close();

                            } else {
                                String timeStamp = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(new Date());
                                FileWriter writer = new FileWriter(randomfile);
                                writer.append("Fichier crée en "+ timeStamp+ "\n" );
                                varRandom = (int) Math.floor(Math.random()*2);
                                writer.append(varRandom+"\n");
                                writer.flush();
                                writer.close();
                                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "something went wrong.. ");
                            e.printStackTrace();
                        }
*/

                        // On lance une nouvelle activité : l'interface du choix d'item
                        birthdate= day + "/" + month + "/" + year;
                        Log.i("",birthdate+" is a number");
                        Intent myIntent = new Intent(ouverture_appli.this, choice_item.class);
                        myIntent.putExtra("name", name);
                        myIntent.putExtra("surname", surname);
                        myIntent.putExtra("birthdate", birthdate);
                        if (varRandom==0){
                            myIntent.putExtra("varRandom", 0);
                        } else {
                            myIntent.putExtra("varRandom", 1);
                        }

                        startActivity(myIntent);
                        // On ferme l'activité en cours
                        ouverture_appli.this.finish();

                    } catch (Exception e) { // Problème inconnu avec la date choisie
                        Toast.makeText(getApplicationContext(), R.string.internalError, Toast.LENGTH_LONG).show();
                    }


                }
            }
        });



        // Pour le bouton "Quitter"
        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopLockTask();
                ouverture_appli.this.finish();
                System.exit(0);
            }
        });

        boutonPatient = (Button) findViewById(R.id.boutonPatient);
        boutonPatient.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(ouverture_appli.this,Patients.class);
                startActivity(intent);
                //Inserted by Adriana 04/03/2018
                ouverture_appli.this.finish();
            }
        });
    }



    public void mymy (){
        //stopLockTask();
        ouverture_appli.this.finish();
        System.exit(0);
    }



    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back_answer = true;
              stopLockTask();
              finish();
        }
        return back_answer;
    }


    @Override
    public void onClick(View v) {
        showDialog(0);
    }



}


