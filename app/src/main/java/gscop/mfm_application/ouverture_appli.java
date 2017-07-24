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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Pattern;

public class ouverture_appli extends Activity implements View.OnClickListener {

    private EditText nomEntre;
    private EditText prenomEntre;
    private TextView texteDate;
    private String birthdate = null;
    private Button boutonValider;
    private Button boutonEffacer;
    private Button buttonExit;
    private final Context context = this;
    private String name;
    private String surname;
    private TextView tvDisplayDate;
    private Calendar cal;
    private int year;
    private int month;
    private int day;
    private static final int DATE_DIALOG_ID = 999;
    private Button btnChangeDate;
    private boolean chosenDate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ouverture_appli);

        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        tvDisplayDate = (TextView) findViewById(R.id.tvDate);

        // On utilise la méthode findViewById pour récupérer les éléments de la vue
        // R est la classe qui contient les ressources
        boutonValider = (Button) findViewById(R.id.boutonvalider);
        boutonEffacer = (Button) findViewById(R.id.buttonerase);

        nomEntre = (EditText) findViewById(R.id.nom);
        prenomEntre = (EditText) findViewById(R.id.prenom);
        // On range le clavier quand le champ prénom n'est plus sélectionné
        prenomEntre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        texteDate = (TextView) findViewById(R.id.texteBirthdate);

        // On met un listener qui regarde quand on clique sur le bouton
        // Pour le bouton valider
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On récupère le nom, le prénom et la date de naissance
                name = nomEntre.getText().toString();
                int length_name = name.length();
                name = name.toUpperCase();
                surname = prenomEntre.getText().toString();
                int length_surname = surname.length();

                // On vérifie que tous les champs ont été remplis
                // On vérifie que le nom et le prénom ont été remplis
                if (length_name > 0 && length_surname > 0) {
                    // On vérifie que le nom et le prénom entrés contiennent bien que des lettres, tirets et espaces possibles
                    if (Pattern.matches("[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ'-]*", name)) {
                        if (Pattern.matches("[a-zA-ZáàâäãåçéèêëíìîïñóòôöõúùûüýÿæœÁÀÂÄÃÅÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸÆŒ'-]*", surname)) {
                            surname = surname.replaceFirst(".", (surname.charAt(0) + "").toUpperCase());
                            // On vérifie qu'une date a bien été sélectionnée
                            birthdate = tvDisplayDate.getText().toString();
                            if (birthdate != null && chosenDate) {
                                texteDate.setError(null);
                                try {
                                    // Ouvrir une boite de dialogue permettant de valider les infos entrées
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                    // set titre
                                    alertDialogBuilder.setTitle("Confirmation des données");
                                    // set dialog message
                                    alertDialogBuilder
                                            .setMessage("Etes-vous certain de vouloir créer un fichier pour le patient suivant : \n\n"
                                                    + " " + name + " " + surname + "\n Né(e) le : " + birthdate)
                                            .setCancelable(false)
                                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, go to next activity
                                                    dialog.cancel();
                                                    // On lance une nouvelle activité : l'interface du choix d'item
                                                    Intent myIntent = new Intent(ouverture_appli.this, choice_item.class);
                                                    myIntent.putExtra("name", name);
                                                    myIntent.putExtra("surname", surname);
                                                    myIntent.putExtra("birthdate", birthdate);
                                                    startActivity(myIntent);
                                                    // On ferme l'activité en cours
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // if this button is clicked, close the dialog box
                                                    dialog.cancel();
                                                }
                                            });
                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    // show it
                                    alertDialog.show();
                                } catch (Exception e) { // Problème inconnu avec la date choisie
                                    Toast.makeText(getApplicationContext(), R.string.internalError, Toast.LENGTH_LONG).show();
                                }
                            } else { // aucune date n'a été choisie
                                Toast.makeText(getApplicationContext(), R.string.errorDate, Toast.LENGTH_LONG).show();
                                texteDate.setError("Veuillez sélectionner une date !");
                                texteDate.requestFocus();
                            }
                        } else { // Champ prénom pas au bon format
                            Toast.makeText(getApplicationContext(), R.string.errorSurname, Toast.LENGTH_LONG).show();
                            prenomEntre.setError("Que des lettres !");
                            prenomEntre.requestFocus();
                        }
                    } else { // Champ nom pas au bon format
                        Toast.makeText(getApplicationContext(), R.string.errorName, Toast.LENGTH_LONG).show();
                        nomEntre.setError("Que des lettres !");
                        nomEntre.requestFocus();
                    }
                } else { // Un des champs de nom ou prénom n'est pas rempli
                    Toast.makeText(getApplicationContext(), R.string.errorVoid, Toast.LENGTH_LONG).show();
                    if (length_name <= 0) {
                        nomEntre.setError("Champ nom vide !");
                        nomEntre.requestFocus();
                    } else if (length_surname <= 0) {
                        prenomEntre.setError("Champ prénom vide !");
                        prenomEntre.requestFocus();
                    }
                }
            }
        });

        // Listener du bouton effacer
        boutonEffacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nomEntre.getText().clear();
                prenomEntre.getText().clear();
                tvDisplayDate.setText("");
                birthdate = null;
                chosenDate = false;
            }
        });

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
                                ouverture_appli.this.finish();
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
    }

    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Êtes-vous certain de vouloir quitter l'application ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            back_answer = true;
                            // On quitte l'application courante
                            ouverture_appli.this.finish();
                            System.exit(0);
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
     * Rétractation du clavier lorsque l'utilisateur touche l'écran hors du clavier.
     *
     * @param view la vue associée
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        // current date = today - 10 years
        DatePickerDialog dialog = new DatePickerDialog(context, R.style.CustomDatePickerDialogTheme, datePickerListener, year - 10, month, day);
        // date max = aujourd'hui, date min = il y a 100 ans
        long now = System.currentTimeMillis() - 1000;
        dialog.getDatePicker().setMaxDate(now);
        cal.set(year - 100, month, day);
        dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getDatePicker().setFirstDayOfWeek(2);
        }
        return dialog;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            tvDisplayDate.setText(selectedDay + " / " + (selectedMonth + 1) + " / " + selectedYear);
            chosenDate = true;
        }
    };

    @Override
    public void onClick(View v) {
        showDialog(0);
    }
}