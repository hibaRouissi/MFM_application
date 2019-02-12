package gscop.mfm_application;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Patients extends Activity {

    private ListView ListPatients;
    private Button refresh;
    private Button add;
    private DbHelper dbHelper;
    private static final String TAG = "www.gscope.patient";
    private List<Patient> listPatients = null;
    private int varRandom=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patients);

        ListPatients = (ListView) findViewById(R.id.ListPatients);
        ListPatients.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listPatients != null){
                    Patient p = listPatients.get(i);
                    Intent intent = new Intent(Patients.this,choice_item.class);
                    intent.putExtra("name",p.getName());
                    intent.putExtra("surname",p.getSurname());
                    intent.putExtra("birthdate",p.getBirthdate());
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
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "something went wrong.. ");
                        e.printStackTrace();
                    }
*/



                    if (varRandom==0){
                        intent.putExtra("varRandom", 0);
                    } else {
                        intent.putExtra("varRandom", 1);
                    }

                    startActivity(intent);
                    //Inserted by Adriana 04/03/2018
                    Patients.this.finish();
                }
            }
        });
        refresh = (Button) findViewById(R.id.refresh);
        add = (Button) findViewById(R.id.retour);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dbHelper != null){
                    dbHelper.onUpgrade(dbHelper.getReadableDatabase(),0,0);
                    ListPatients.setAdapter(null);
                    Intent intent = new Intent(Patients.this,ouverture_appli.class);
                    startActivity(intent);
                    //Inserted by Adriana 04/03/2018
                    Patients.this.finish();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Patients.this,ouverture_appli.class);
                startActivity(intent);
                //Inserted by Adriana 04/03/2018
                Patients.this.finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        dbHelper = new DbHelper(this);
        listPatients = dbHelper.selectAllPatients();
        ArrayAdapter<Patient> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listPatients);
        ListPatients.setAdapter(adp);
    }
}
