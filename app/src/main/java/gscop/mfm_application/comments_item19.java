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
    private RadioButton boutonCotationNSPTablet;
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
    private Long durationTime;
    private ArrayList isPalm;
    private ProgressBar progressbar;
    Boolean bmergePdf = false;

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
            isPalm = (ArrayList) intent.getSerializableExtra("isPalm");
            durationTime = intent.getLongExtra("durationTime",0);
            varRandom = intent.getIntExtra("varRandom", -1); // -1 par défaut
            try {
                File f = new File(path, "cartographie.png");
                cartoBitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.errorCarto, Toast.LENGTH_LONG).show();
            }
            checkBoxAppuiPaume = (CheckBox) findViewById(R.id.checkBoxAppuiPaume);
            checkBoxPause = (CheckBox) findViewById(R.id.checkBoxPause);
            checkBoxChange = (CheckBox) findViewById(R.id.checkBoxChange);
            checkBoxCompens = (CheckBox) findViewById(R.id.checkBoxCompens);
        }

        pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , "patient_" + name + "_" + surname);

        radioGroupCotationTablet = (RadioGroup) findViewById(R.id.radioGroupCotationTablet);
        textCotationTablet = (TextView) findViewById(R.id.textCotationTablet);
        boutonCotation0Tablet = (RadioButton) findViewById(R.id.radioButton0Tablet);
        boutonCotation1Tablet = (RadioButton) findViewById(R.id.radioButton1Tablet);
        boutonCotation2Tablet = (RadioButton) findViewById(R.id.radioButton2Tablet);
        boutonCotation3Tablet = (RadioButton) findViewById(R.id.radioButton3Tablet);
        boutonCotationNSPTablet = (RadioButton) findViewById(R.id.radioButtonNSPTablet);

        radioGroupCotationPaper = (RadioGroup) findViewById(R.id.radioGroupCotationPaper);
        textCotationPaper = (TextView) findViewById(R.id.textCotationPaper);
        boutonCotation0Paper = (RadioButton) findViewById(R.id.radioButton0Paper);
        boutonCotation1Paper = (RadioButton) findViewById(R.id.radioButton1Paper);
        boutonCotation2Paper = (RadioButton) findViewById(R.id.radioButton2Paper);
        boutonCotation3Paper = (RadioButton) findViewById(R.id.radioButton3Paper);
        boutonCotationNSPPaper = (RadioButton) findViewById(R.id.radioButtonNSPPaper);
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
        infosPatient.setText("Patient : " + name + " " + surname + " \nNé(e) le : " + birthdate);
        progressbar = (ProgressBar) findViewById(R.id.enregistBar);

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
                promptForNextAction();
            }
            progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void preCreatePdf(){
        // On crée un dossier NOM_prenom du patient s'il n'existe pas déjà
        boolean isDirectoryCreated = pdfFolder.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pdfFolder.mkdir();
            Log.d(TAG," Creating folder ");
        }
        if(isDirectoryCreated){
            Log.d(TAG," Folder created ");
        }


        // See the last pdf created in the actual day
        while(!lastPdf){
            String filePath = pdfFolder.toString() + "/" + name + "_" + surname + "_" + timeStampSimple + "_" + "item19" + "_" + docsCount + ".pdf";
            myFile = new File(filePath);
            boolean isFile = myFile.exists();
            if(isFile){
                Log.d(TAG, " File exist ");
                docsCount++;
            }
            else{
                Log.d(TAG, " Last pdf is : " + (docsCount-1));
                lastPdf = true;
            }
        }

        if(docsCount == 1){
            Log.d(TAG," First pdf of the day ");
            bmergePdf = false;
            actionEnregistrer();
        }
        else {
            // Make dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Est-ce que vous voulez enregistre dans le dernier pdf")
                    .setCancelable(false)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bmergePdf = true;
                            actionEnregistrer();

                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bmergePdf = false;
                            actionEnregistrer();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
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
        if (boutonCotation0Paper.isChecked() || boutonCotation1Paper.isChecked() || boutonCotation2Paper.isChecked() || boutonCotation3Paper.isChecked() || boutonCotationNSPPaper.isChecked()) {
            textCotationPaper.setError(null);
            // radioGroup : cotation tablette
            if (boutonCotation0Tablet.isChecked() || boutonCotation1Tablet.isChecked() || boutonCotation2Tablet.isChecked() || boutonCotation3Tablet.isChecked() || boutonCotationNSPTablet.isChecked()) {
                textCotationTablet.setError(null);
                // --------------------- on récupère les commentaires du kiné -------------------
                // ------- COTATION PAPIER
                int radioButtonSelectedID = radioGroupCotationPaper.getCheckedRadioButtonId();
                View radioButtonSelected = radioGroupCotationPaper.findViewById(radioButtonSelectedID);
                int index = radioGroupCotationPaper.indexOfChild(radioButtonSelected);
                RadioButton r = (RadioButton) radioGroupCotationPaper.getChildAt(index);
                cotationPaper = r.getText().toString();
                // ------- COTATION TABLETTE
                radioButtonSelectedID = radioGroupCotationTablet.getCheckedRadioButtonId();
                radioButtonSelected = radioGroupCotationTablet.findViewById(radioButtonSelectedID);
                index = radioGroupCotationTablet.indexOfChild(radioButtonSelected);
                r = (RadioButton) radioGroupCotationTablet.getChildAt(index);
                cotationTablet = r.getText().toString();
                // ------- COMMENTAIRES
                if (checkBoxCompens.isChecked())
                    listeComm = listeComm + checkBoxCompens.getText() + " \n ";
                if (checkBoxChange.isChecked())
                    listeComm = listeComm + checkBoxChange.getText() + " \n ";
                if (checkBoxPause.isChecked())
                    listeComm = listeComm + checkBoxChange.getText() + " \n ";
                if (checkBoxAppuiPaume.isChecked())
                    listeComm = listeComm + checkBoxAppuiPaume.getText() + " \n ";
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
        } else {
            boutonEnregistrer.setBackgroundColor(getResources().getColor(R.color.myBlue));
            boutonEnregistrer.setClickable(true);
            textStateSaving.setText(R.string.errorCotationPaper);
            textCotationPaper.setError("Choisir cotation !");
            textCotationPaper.requestFocus();
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

        String filePath = pdfFolder.toString() + "/" + name + "_" + surname + "_" + timeStampSimple + "_" + "item19" + "_" + docsCount + ".pdf";
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

        String strText = " Patient : " + name + " " + surname + "\n Date de naissance : " + birthdate + "\n \n";
        Paragraph paragraphInfos = new Paragraph();
        paragraphInfos.add(strText);
        document.add(paragraphInfos);

        // INFOS ITEM
        Paragraph paragraphInfosItemTitre = new Paragraph();
        paragraphInfosItemTitre.setFont(myFontTitre);
        paragraphInfosItemTitre.add("\n ITEM 19 :");
        document.add(paragraphInfosItemTitre);

        strText = "réalisé le : " + timeStamp + "\n" + "cotation sur papier : " + cotationPaper + "\n" + "cotation sur tablette : " + cotationTablet + "\n \n";
        Paragraph paragraphInfosItem = new Paragraph();
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

      /*  // TABLEAU DES COORDONNEES
        // On change de page
        document.newPage();
        // 2 colonnes, une pour chaque tableau
        Log.d(TAG," SIZE : " + tableauX.size());
        for( int j = 0; j < tableauX.size(); j++) {
            document.add( Chunk.NEWLINE );
            PdfPTable table = new PdfPTable(2);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            // Titres : colonne 1 = coordonnées en X , colonne 2 = coordonnées en Y
            table.addCell("Coordonnées en X");
            table.addCell("Coordonnées en Y");
            table.setHeaderRows(1);
            // On met les cellules titre en gris
            PdfPCell[] cells = table.getRow(0).getCells();
            for (PdfPCell cell : cells) {
                cell.setBackgroundColor(BaseColor.GRAY);
            }
            // On parcourt les coordonnées en X et on les ajoute en colonne 1
            for (int k = 0; k < tableauX.get(j).size(); k++) {
                table.addCell(tableauX.get(j).get(k).toString());
                table.addCell(tableauY.get(j).get(k).toString());
            }
            // On ajoute le tableau au document
            document.add(table);
        }*/

        //Step 5: Close the document
        document.close();

        if(bmergePdf) {
            try {
                String lastFilePath = pdfFolder.toString() + "/" + name + "_" + surname + "_" + timeStampSimple + "_" + "item19" + "_" + (docsCount-1) + ".pdf";
                String newFilePath = pdfFolder.toString() + "/" + name + "_" + surname + "_" + timeStampSimple + "_" + "item19" + "_" + (docsCount+1) + ".pdf";

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


    /**
     * Demande à l'utilisateur ce qu'il veut faire après avoir enregistré la cartographie, en lui donnant trois choix : continuer la MFM, prévisualiser le PDF ou quitter l'application.
     */
    private void promptForNextAction() {
        final String[] options = {getString(R.string.label_continue), getString(R.string.label_preview), getString(R.string.label_quit)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PDF enregistré, que voulez-vous faire ?")
                .setCancelable(false)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(getString(R.string.label_continue))) {
                            // on renvoie alors vers l'interface de choix d'item
                            Intent myIntent = new Intent(comments_item19.this, choice_item.class);
                            myIntent.putExtra("name", name);
                            myIntent.putExtra("surname", surname);
                            myIntent.putExtra("birthdate", birthdate);
                            startActivity(myIntent);
                            // on ferme l'activité en cours
                            finish();
                        } else if (options[which].equals(getString(R.string.label_preview))) {
                            try {
                                // on renvoie alors vers l'interface de choix d'item
                                Intent myIntent = new Intent(comments_item19.this, choice_item.class);
                                myIntent.putExtra("name", name);
                                myIntent.putExtra("surname", surname);
                                myIntent.putExtra("birthdate", birthdate);
                                startActivity(myIntent);
                                // on ferme l'activité en cours
                                finish();
                                // on ouvre le pdf
                                viewPdf();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), R.string.viewPB, Toast.LENGTH_LONG).show();
                            }
                        } else if (options[which].equals(getString(R.string.label_quit))) {
                            comments_item19.this.finish();
                            System.exit(0);
                        }
                    }
                });
        progressbar.setVisibility(View.GONE);
        builder.show();
    }
}

