package gscop.mfm_application;

/**
 * Created by Alison.rl on 08/09/2017.
 */

public class Patient {
    private int id;
    private String name;
    private String surname;
    private String birthdate;

    public Patient(){}

    public Patient(int id,String name, String surname, String birthdate){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthdate = birthdate;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return " Nom = " + name +
                "          Prénom = " + surname +
                "          Date de Naissance = " + birthdate ;
    }
}
