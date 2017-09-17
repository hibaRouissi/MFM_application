package gscop.mfm_application;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Patients extends AppCompatActivity {

    private ListView ListPatients;
    private Button refresh;
    private Button add;
    private DbHelper dbHelper;
    private static final String TAG = "www.gscope.patient";
    private List<Patient> listPatients = null;

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
                    startActivity(intent);
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
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Patients.this,ouverture_appli.class);
                startActivity(intent);
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
