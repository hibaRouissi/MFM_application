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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.hyphenation.TernaryTree;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class comments_item19 extends Activity {

    private boolean visibility_cotation_automatique=true; // digite false no lugar de true para apagar a cot. autom.

    private static final String TAG = "www.gscop.app" ;
    private String name = "";
    private String surname = "";
    private String birthdate = "";
    private Button boutonEnregistrer;
    private final Context context = this;
    private RadioGroup radioGroupCotationPaper;
    private TextView textCotationPaper;
    private RadioButton boutonCotation0Paper;
    private RadioButton boutonCotation1Paper;
    private RadioButton boutonCotation2Paper;
    private RadioButton boutonCotation3Paper;
    private RadioButton boutonCotationNSPPaper;
    private RadioGroup radioGroupCotationTablet;
    private TextView textCotationTablet;
    private RadioButton boutonCotation0Tablet;
    private RadioButton boutonCotation1Tablet;
    private RadioButton boutonCotation2Tablet;
    private RadioButton boutonCotation3Tablet;
    private  Button buttonDessin_19;

    private RadioGroup radioGroupCotationautomatique;
    private RadioButton boutonCotation0automatique;
    private RadioButton boutonCotation1automatique;
    private RadioButton boutonCotation2automatique;
    private RadioButton boutonCotation3automatique;
    private TextView infotellwaytext;

    private String cotationPaper = "cotation papier inconnue";
    private String cotationTablet = "cotation tablette inconnue";
    private String commentaire = "aucun commentaire";
    private EditText comments;
    private CheckBox checkBoxAppuiPaume;
    private CheckBox checkBoxPause;
    private CheckBox checkBoxChange;
    private CheckBox checkBoxCompens;
    private TextView infosPatient;
    private String path = "";
    private ArrayList<ArrayList<Float>> tableauX;
    private ArrayList<ArrayList<Float>> tableauY;
    private Bitmap cartoBitmap;
    private File myFile;
    private String listeComm = " ";
    private int varRandom;
    private TextView textStateSaving;
    private boolean handledClick = false;
    private ArrayList eventUpTimes;
    private ArrayList eventDownTimes;
    private Float mImageX,mImageY;
    //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
    private Float mImage2X,mImage2Y;
    private Long durationTime;
    private ArrayList isPalm;
    private ProgressBar progressbar;
    Boolean bmergePdf = false;

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
    public int missed_turns;
    private int number_of_traces;
    private int resultat_cotation_therapeute;



    // Pdf folder
    File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            , "Error");

    // Stamp Pdf
    String timeStamp = new SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(new Date());
    String timeStampSimple = new SimpleDateFormat("dd_MM_yyyy", Locale.FRANCE).format(new Date());


    private int docsCount = 1;
    private boolean lastPdf = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_item19);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // On récupère les infos de l'intent de l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            surname = intent.getStringExtra("surname");
            birthdate = intent.getStringExtra("birthdate");
            path = intent.getStringExtra("path");
            tableauX = (ArrayList) intent.getSerializableExtra("tableauX");
            tableauY = (ArrayList) intent.getSerializableExtra("tableauY");
            eventUpTimes = (ArrayList) intent.getSerializableExtra("eventUpTimes");
            eventDownTimes = (ArrayList) intent.getSerializableExtra("eventDownTimes");
            mImageX = intent.getFloatExtra("mImageX",0f);
            mImageY = intent.getFloatExtra("mImageY",0f);
            //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
            mImage2X = intent.getFloatExtra("mImage2X",0f);
            mImage2Y = intent.getFloatExtra("mImage2Y",0f);
            isPalm = (ArrayList) intent.getSerializableExtra("isPalm");
            durationTime = intent.getLongExtra("durationTime",0);
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut

            touched_inside = intent.getBooleanExtra("touched_inside", false);
            demi_boucles = intent.getIntExtra("demi_boucles",0);
            max_within_range= intent.getIntExtra("max_within_range",0);
            min_within_range= intent.getIntExtra("min_within_range",0);
            max_outside_window= intent.getIntExtra("max_outside_window",0);
            min_outside_window= intent.getIntExtra("min_outside_window",0);
            missed_turns= intent.getIntExtra("missed_turns",0);
            max_x = intent.getIntegerArrayListExtra("max_x");
            min_x = intent.getIntegerArrayListExtra("min_x");
            cotation_3 = intent.getBooleanExtra("cotation_3", false);
            last_touch_ok = intent.getBooleanExtra("last_touch_ok", false);
            first_touch_ok = intent.getBooleanExtra("first_touch_ok", false);
            firsttouch_in_tray=intent.getBooleanExtra("firsttouch_in_tray",false);
            lasttouch_in_tray=intent.getBooleanExtra("lasttouch_in_tray",false);

            my_times=(ArrayList) intent.getSerializableExtra("my_times");
            my_X=(ArrayList) intent.getSerializableExtra("my_X");
            my_Y=(ArrayList) intent.getSerializableExtra("my_Y");
            test_velocity=intent.getBooleanExtra("test_velocity",false);
            number_of_traces= intent.getIntExtra("number_of_traces",0);;
            resultat_cotation_therapeute=intent.getIntExtra("resultat_cotation_therapeute",4);


            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.errorCarto, Toast.LENGTH_LONG).show();
            }

            // Commented by Adriana 05/03/2018
            /* Commented by Adriana 05/03/2018
            checkBoxAppuiPaume = (CheckBox) findViewById(R.id.checkBoxAppuiPaume);
            checkBoxPause = (CheckBox) findViewById(R.id.checkBoxPause);
            checkBoxChange = (CheckBox) findViewById(R.id.checkBoxChange);
            checkBoxCompens = (CheckBox) findViewById(R.id.checkBoxCompens);
            */
        }

        pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , "patient_" + name );

        radioGroupCotationTablet = (RadioGroup) findViewById(R.id.radioGroupCotationTablet);
        textCotationTablet = (TextView) findViewById(R.id.textCotationTablet);
        boutonCotation0Tablet = (RadioButton) findViewById(R.id.radioButton0Tablet);
        boutonCotation1Tablet = (RadioButton) findViewById(R.id.radioButton1Tablet);
        boutonCotation2Tablet = (RadioButton) findViewById(R.id.radioButton2Tablet);
        boutonCotation3Tablet = (RadioButton) findViewById(R.id.radioButton3Tablet);

        // para a cotação automática
        radioGroupCotationautomatique   = (RadioGroup)  findViewById(R.id.radioGroupCotationTabletAutomatique);
        boutonCotation0automatique      = (RadioButton) findViewById(R.id.radioButtonCotationAutomatiquet0);
        boutonCotation1automatique      = (RadioButton) findViewById(R.id.radioButtonCotationAutomatiquet1);
        boutonCotation2automatique      = (RadioButton) findViewById(R.id.radioButtonCotationAutomatiquet2);
        boutonCotation3automatique      = (RadioButton) findViewById(R.id.radioButtonCotationAutomatiquet3);

        if (resultat_cotation_therapeute>=0 && resultat_cotation_therapeute<=3) {
            if (resultat_cotation_therapeute==0) boutonCotation0Tablet.setChecked(true);
            if (resultat_cotation_therapeute==1) boutonCotation1Tablet.setChecked(true);
            if (resultat_cotation_therapeute==2) boutonCotation2Tablet.setChecked(true);
            if (resultat_cotation_therapeute==3) boutonCotation3Tablet.setChecked(true);
        }



        infotellwaytext = (TextView) findViewById(R.id.tellwaytext);

        if (!touched_inside) {
            boutonCotation0automatique.setChecked(true);
            infotellwaytext.setText("Il n´y a pas de tracé dans le carré");
            resultat_cotation_automatique=0;
        } else {
            if (( demi_boucles>=7 ) && (max_outside_window==0) && cotation_3 && (min_outside_window==0)
                    && (max_within_range>=((int) ((float)(0.5*demi_boucles))-1))
                    && (min_within_range>=((int) ((float)(0.5*demi_boucles))-1))) {
                boutonCotation3automatique.setChecked(true);
                infotellwaytext.setText("Boucles complètes tout au long du carré, sans arrêt");
                resultat_cotation_automatique=3;
            } else if (  ((demi_boucles==1 || demi_boucles==2) && first_touch_ok && last_touch_ok && cotation_3)
                      || (( demi_boucles>=3 ) && (max_outside_window<=demi_boucles/2-1) && (min_outside_window<=demi_boucles/2-1) && (max_within_range>=1) && (min_within_range>=1))
                      ||  ((demi_boucles<=2) && firsttouch_in_tray && lasttouch_in_tray && (missed_turns==0))
                      ||  ((demi_boucles==2) && firsttouch_in_tray && (missed_turns==0)) ) {
                if (number_of_traces>1){
                    infotellwaytext.setText(" Des boucles complètes, mais en plusieurs tracées");
                } else if (((demi_boucles<=2) && firsttouch_in_tray && lasttouch_in_tray) ||((demi_boucles<=2) && firsttouch_in_tray && (missed_turns==0))) {
                    infotellwaytext.setText(" Seulement une boucle complète");
                } else if ((max_outside_window!=0)|| (min_outside_window!=0)) {
                    infotellwaytext.setText(" Des boucles complètes, mais avec des points dépassant le carré");
                } else if (!test_velocity) {
                    infotellwaytext.setText(" Des boucles complètes, mais avec arrêt");
                } else if (!first_touch_ok || !last_touch_ok){
                    infotellwaytext.setText(" Des boucles complètes, mais sans toucher les extremités horizontales");
                } else {
                    infotellwaytext.setText(" Des boucles complètes, mais quelques unes ne pas touchant les extremités verticales");
                }
                boutonCotation2automatique.setChecked(true);
                resultat_cotation_automatique=2;
            } else {
                boutonCotation1automatique.setChecked(true);
                infotellwaytext.setText("Il n´y a pas de boucle complète qui touches les bordes du carré");
                resultat_cotation_automatique=1;
            }
        }

        if (!visibility_cotation_automatique) {
            TextView titi = (TextView) findViewById(R.id.textCotationTablette);
            titi.setVisibility(View.INVISIBLE);
            TextView tata = (TextView) findViewById(R.id.myx);
            tata.setVisibility(View.INVISIBLE);
            infotellwaytext.setVisibility(View.INVISIBLE);
            boutonCotation0automatique.setVisibility(View.INVISIBLE);
            boutonCotation1automatique.setVisibility(View.INVISIBLE);
            boutonCotation2automatique.setVisibility(View.INVISIBLE);
            boutonCotation3automatique.setVisibility(View.INVISIBLE);
        }


        textStateSaving = (TextView) findViewById(R.id.textStateSaving);
        comments = (EditText) findViewById(R.id.editTextComments);
        comments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        infosPatient = (TextView) findViewById(R.id.PatientName);
        infosPatient.setText("Patient : " + name + " \nNé(e) le : " + birthdate);
        progressbar = (ProgressBar) findViewById(R.id.enregistBar);

        // Pour Le bouton " buttonDessin_18"
        buttonDessin_19 = (Button) findViewById(R.id.buttonDessin_19);
        buttonDessin_19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!handledClick) {
                    handledClick = true;
                    buttonDessin_19.setClickable(false);
                    buttonDessin_19.setBackgroundColor(Color.GRAY);
                    Intent myIntent = new Intent(comments_item19.this, carto_item19.class);
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
                    myIntent.putExtra("last_touch_ok",last_touch_ok);
                    myIntent.putExtra("first_touch_ok",first_touch_ok);
                    myIntent.putExtra("firsttouch_in_tray",firsttouch_in_tray);
                    myIntent.putExtra("lasttouch_in_tray",lasttouch_in_tray);

                    myIntent.putExtra("my_times",my_times);
                    myIntent.putExtra("my_X",my_X);
                    myIntent.putExtra("my_Y",my_Y);
                    myIntent.putExtra("test_velocity",test_velocity);
                    myIntent.putExtra("missed_turns",missed_turns);
                    myIntent.putExtra("number_of_traces",number_of_traces);

                    if (boutonCotation0Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",0);
                    if (boutonCotation1Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",1);
                    if (boutonCotation2Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",2);
                    if (boutonCotation3Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",3);

                    startActivity(myIntent);

                    // On ferme l'activité en cours
                    finish();
                }
            }

        });


        // Pour le bouton "Enregistrer"
        boutonEnregistrer = (Button) findViewById(R.id.buttonSave);
        boutonEnregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On s'assure de ne pouvoir cliquer qu'une seule fois sur le bouton
                if (!handledClick) {
                    handledClick = true;
                    textStateSaving.setText(R.string.checkData);
                    boutonEnregistrer.setClickable(false);
                    boutonEnregistrer.setBackgroundColor(Color.GRAY);
                    preCreatePdf();
                }
            }
        });
    }

    private class createPdfTask extends AsyncTask<Void,Integer,Void>{

        Boolean problemPdf = false;

        @Override
        protected void onPreExecute(){
            textStateSaving.setText(R.string.pdfsaving);
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                createPdf();
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
                problemPdf = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Void){
            boutonEnregistrer.setBackgroundColor(getResources().getColor(R.color.myBlue));
            if (problemPdf){
                textStateSaving.setText(R.string.pbPDF);
            }
            else{
                textStateSaving.setText(R.string.savedOK);
                saveSQL();

                // Commented by Adriana 05/03/2018 (to remove the call:"que voulez-vous faires"
                //promptForNextAction();

                //inserted by Adriana 05/03/2018
                Intent myIntent = new Intent(comments_item19.this, choice_item.class);
                myIntent.putExtra("name", name);
                myIntent.putExtra("surname", surname);
                myIntent.putExtra("birthdate", birthdate);
                myIntent.putExtra("varRandom", varRandom); // included in 12/03
                startActivity(myIntent);
                // on ferme l'activité en cours
                finish();

            }
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void preCreatePdf() {
        // On crée un dossier NOM_prenom du patient s'il n'existe pas déjà
        boolean isDirectoryCreated = pdfFolder.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pdfFolder.mkdir();
            Log.d(TAG, " Creating folder ");
        }
        if (isDirectoryCreated) {
            Log.d(TAG, " Folder created ");
        }


        // See the last pdf created in the actual day
        while (!lastPdf) {
            String filePath = pdfFolder.toString() + "/" + name + "_" + timeStampSimple + "_" + "item19" + "_" + docsCount + ".pdf";
            myFile = new File(filePath);
            boolean isFile = myFile.exists();
            if (isFile) {
                Log.d(TAG, " File exist ");
                docsCount++;
            } else {
                Log.d(TAG, " Last pdf is : " + (docsCount - 1));
                lastPdf = true;
            }
        }

        if (docsCount == 1) {
            Log.d(TAG, " First pdf of the day ");
            bmergePdf = false;
            actionEnregistrer();
        } else {
            // Inserted by Adriana 05/03/2018 (To remove the choice to register in another PDF)
                        bmergePdf = true;
                        actionEnregistrer();

        }
    }

    private void saveSQL(){
        boolean first = true;
        Log.d(TAG, " SAVE SQL ");
        DbHelper dbHelper = new DbHelper(this);
        Patient patient = new Patient(0,name,surname,birthdate);
        List<Patient> listPatient = dbHelper.selectAllPatients();
        for(Iterator iterator = listPatient.iterator(); iterator.hasNext();){
            Patient p = (Patient) iterator.next();
            Log.d(TAG, p.toString());
            Log.d(TAG, " ID : " + p.getId());
            if(p == patient){
                first = false;
            }
        }
        if(first){
            dbHelper.insertPatient(patient);
        }
        else{
            Log.d(TAG, " Patient already created in database ");
        }
    }

    /**
     * Donne les actions à réaliser lorsque l'utilisateur clique sur le bouton "Enregistrer"
     */
    private void actionEnregistrer() {
        textStateSaving.setText("");
        // On vérifie qu'au moins un radioButton a été sélectionné dans chaque radioGroup
        // radioGroup : cotation papier

            // radioGroup : cotation tablette
            if (boutonCotation0Tablet.isChecked() || boutonCotation1Tablet.isChecked() || boutonCotation2Tablet.isChecked() || boutonCotation3Tablet.isChecked() ) {
                textCotationTablet.setError(null);
                // --------------------- on récupère les commentaires du kiné -------------------
                // ------- COTATION TABLETTE
                int radioButtonSelectedID = radioGroupCotationTablet.getCheckedRadioButtonId();
                View radioButtonSelected = radioGroupCotationTablet.findViewById(radioButtonSelectedID);
                int index = radioGroupCotationTablet.indexOfChild(radioButtonSelected);
                RadioButton r = (RadioButton) radioGroupCotationTablet.getChildAt(index);
                cotationTablet = r.getText().toString();

                radioButtonSelectedID = radioGroupCotationTablet.getCheckedRadioButtonId();
                radioButtonSelected = radioGroupCotationTablet.findViewById(radioButtonSelectedID);
                index = radioGroupCotationTablet.indexOfChild(radioButtonSelected);
                r = (RadioButton) radioGroupCotationTablet.getChildAt(index);
                cotationTablet = r.getText().toString();
                // ------- COMMENTAIRES

                commentaire = comments.getText().toString();
                // ----------- CREATION DU PDF -------------
                new createPdfTask().execute();
            } else {
                boutonEnregistrer.setBackgroundColor(getResources().getColor(R.color.myBlue));
                boutonEnregistrer.setClickable(true);
                textStateSaving.setText(R.string.errorCotationTablet);
                textCotationTablet.setError("Choisir cotation !");
                textCotationTablet.requestFocus();
                handledClick = false;
            }

    }

    // Quand on appuie sur la touche retour de la tablette
    private boolean back_answer = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back_answer = true;
            // On revient à l'écran d'affichage de cartographie de l'item 19
            Intent myIntent = new Intent(comments_item19.this, carto_item19.class);
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
            myIntent.putExtra("last_touch_ok",last_touch_ok);
            myIntent.putExtra("first_touch_ok",first_touch_ok);
            myIntent.putExtra("firsttouch_in_tray",firsttouch_in_tray);
            myIntent.putExtra("lasttouch_in_tray",lasttouch_in_tray);

            myIntent.putExtra("my_times",my_times);
            myIntent.putExtra("my_X",my_X);
            myIntent.putExtra("my_Y",my_Y);
            myIntent.putExtra("test_velocity",test_velocity);
            myIntent.putExtra("missed_turns",missed_turns);
            myIntent.putExtra("number_of_traces",number_of_traces);

            if (boutonCotation0Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",0);
            if (boutonCotation1Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",1);
            if (boutonCotation2Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",2);
            if (boutonCotation3Tablet.isChecked()) myIntent.putExtra("resultat_cotation_therapeute",3);

            startActivity(myIntent);
            // On ferme l'activité en cours
            finish();
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

    /**
     * Création du fichier PDF contenant la cartographie et les commentaires.
     *
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    private void createPdf() throws FileNotFoundException, DocumentException {

        String filePath = pdfFolder.toString() + "/" + name + "_" + timeStampSimple + "_" + "item19" + "_" + docsCount + ".pdf";
        myFile = new File(filePath);
        OutputStream output = new FileOutputStream(myFile);

        // Step 1 : on crée le document
        Document document = new Document(PageSize.LETTER);
        document.setMarginMirroring(true);
        document.setMarginMirroringTopBottom(true);


        // Step 2 : on instantie le PdfWriter
        PdfWriter.getInstance(document, output);

        // Step 3 : ouverture du document
        document.open();

        // Step 4 : Add content
        // Choix des polices
        Font myFontTitre = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        // TITRE
        Paragraph paragraphTitre = new Paragraph();
        paragraphTitre.setAlignment(Element.ALIGN_CENTER);
        paragraphTitre.setFont(myFontTitre);
        paragraphTitre.add("Fiche récapitulative \n \n \n");
        document.add(paragraphTitre);

        // INFOS PATIENT
        Paragraph paragraphInfosTitre = new Paragraph();
        paragraphInfosTitre.setFont(myFontTitre);
        paragraphInfosTitre.add("\n\n INFORMATIONS PATIENT : \n");
        document.add(paragraphInfosTitre);

        String strText = " Patient : " + name + " " + "\n Date de naissance : " + birthdate + "\n \n";
        Paragraph paragraphInfos = new Paragraph();
        paragraphInfos.add(strText);
        document.add(paragraphInfos);

        // INFOS ITEM
        Paragraph paragraphInfosItemTitre = new Paragraph();
        paragraphInfosItemTitre.setFont(myFontTitre);
        paragraphInfosItemTitre.add("\n ITEM 19 :");
        document.add(paragraphInfosItemTitre);

        strText = "réalisé le : " + timeStamp + "\n" +  "cotation sur tablette : " + cotationTablet + "\n" +  "cotation automatique : " + resultat_cotation_automatique + "\n \n";
        Paragraph paragraphInfosItem = new Paragraph();
        paragraphInfosItem.add(strText);
        document.add(paragraphInfosItem);

        strText = infotellwaytext.getText().toString();
        paragraphInfosItem = new Paragraph();
        paragraphInfosItem.add(strText);
        document.add(paragraphInfosItem);


        // COMMENTAIRES KINE
        Paragraph paragraphCommKineTitre = new Paragraph();
        paragraphCommKineTitre.setFont(myFontTitre);
        paragraphCommKineTitre.add("\n COMMENTAIRES : \n");
        document.add(paragraphCommKineTitre);
        strText = listeComm + "\n" + commentaire + "\n \n";
        Paragraph paragraphCommKine = new Paragraph();
        paragraphCommKine.add(strText);
        document.add(paragraphCommKine);

        // CARTOGRAPHIE
        // On change de page
        document.newPage();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cartoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image trueImage = null;
        try {
            trueImage = Image.getInstance(stream.toByteArray());
            // On redimensionne l'image pour qu'elle rentre dans la page
            float leftMargin = document.leftMargin();
            float rightMargin = document.rightMargin();
            float pageSize = document.getPageSize().getWidth();
            float usablePageSize = pageSize - (rightMargin + leftMargin);
            float imageWidth = trueImage.getPlainWidth();
            if (imageWidth > usablePageSize) {
                float reduceWidth = imageWidth - usablePageSize;
                float reducePercent = 100f - ((reduceWidth * 100f) / imageWidth);
                trueImage.scalePercent(reducePercent);
            }
            trueImage.setAlignment(Image.MIDDLE);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Paragraph paragraphCarto = new Paragraph();
        paragraphCarto.add(trueImage);
        document.add(paragraphCarto);



        // TABLEAU DES COORDONNEES
        //On change de page
        if (my_X.size()!=0) {
            document.newPage();
            // 2 colonnes, une pour chaque tableau
            Log.d(TAG," SIZE : " + tableauX.size());
            for( int j = 0; j < 1; j++) {
                Paragraph paragraphTab = new Paragraph();
                paragraphTab.setFont(myFontTitre);
                paragraphTab.add("\n Tableau " + (j+1) + "\n");
                document.add(paragraphTab);
                document.add( Chunk.NEWLINE );
                PdfPTable table = new PdfPTable(3);
                table.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                // Titres : colonne 1 = coordonnées en X , colonne 2 = coordonnées en Y
                table.addCell("Coordonnées en X");
                table.addCell("Coordonnées en Y");
                table.addCell("Temps en millisecondes");

                table.setHeaderRows(1);
                // On met les cellules titre en gris
                PdfPCell[] cells = table.getRow(0).getCells();
                for (PdfPCell cell : cells) {
                    cell.setBackgroundColor(BaseColor.GRAY);
                }
                // On parcourt les coordonnées en X et on les ajoute en colonne 1
                long timezero= my_times.get(0);
                for (int k = 0; k < my_X.size(); k++) {
                    table.addCell(my_X.get(k).toString());
                    table.addCell(my_Y.get(k).toString());
                    Long timedif=my_times.get(k)-timezero;
                    table.addCell(timedif.toString());
                }
                // On ajoute le tableau au document
                document.add(table);
            }
        }



        //Step 5: Close the document
        document.close();

        if(bmergePdf) {
            try {
                String lastFilePath = pdfFolder.toString() + "/" + name + "_" + timeStampSimple + "_" + "item19" + "_" + (docsCount-1) + ".pdf";
                String newFilePath = pdfFolder.toString() + "/" + name + "_" + timeStampSimple + "_" + "item19" + "_" + (docsCount+1) + ".pdf";

                File lastFile = new File(lastFilePath);
                File newFile = new File(newFilePath);

                PdfReader reader = new PdfReader(filePath);
                PdfReader cover = new PdfReader(lastFilePath);
                Document mergeDocument = new Document();
                PdfCopy copy = new PdfCopy(mergeDocument, new FileOutputStream(newFile));
                mergeDocument.open();
                copy.addDocument(cover);
                copy.addDocument(reader);
                mergeDocument.close();
                cover.close();
                reader.close();
                lastFile.delete();
                myFile.delete();

                PdfReader make = new PdfReader(newFilePath);
                Document merge = new Document();
                myFile = new File(lastFilePath);
                PdfCopy copyMerge = new PdfCopy(merge, new FileOutputStream(myFile));
                merge.open();
                copyMerge.addDocument(make);
                merge.close();
                make.close();
                newFile.delete();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *  Permet la prévisualisation du PDF.
     */
    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}

