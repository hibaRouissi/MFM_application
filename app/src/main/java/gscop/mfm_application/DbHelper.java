package gscop.mfm_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alison.rl on 08/09/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String NOME_BASE = "PatientBiblio";
    private static final int VERSION_BASE = 1;

    public DbHelper(Context context) {
        super(context,NOME_BASE, null, VERSION_BASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateTabPatient = " CREATE TABLE Patient(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "name TEXT,"+
                "surname TEXT,"+
                "birthdate TEXT"+
                ")";
        db.execSQL(sqlCreateTabPatient);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String sqlUpgradeTabPatient = " DROP TABLE Patient";
        db.execSQL(sqlUpgradeTabPatient);
        onCreate(db);
    }

    public void insertPatient(Patient patient){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name",patient.getName());
        cv.put("surname",patient.getSurname());
        cv.put("birthdate",patient.getBirthdate());
        db.insert("Patient",null,cv);
        db.close();
    }

    public List<Patient> selectAllPatients(){
        List<Patient> listPatient = new ArrayList<Patient>();
        SQLiteDatabase db = getReadableDatabase();
        String sqlSelectAllPatients = "SELECT * FROM Patient";
        Cursor c = db.rawQuery(sqlSelectAllPatients,null);
        if(c.moveToFirst()){
            do{
            Patient patient = new Patient();
            patient.setId(c.getInt(0));
            patient.setName(c.getString(1));
            patient.setSurname(c.getString(2));
            patient.setBirthdate(c.getString(3));
            listPatient.add(patient);}
            while (c.moveToNext());
        }
        db.close();
        return listPatient;
    }
}
