package id.co.unila.adi.laundryapp.model;

/**
 * Created by japra_awok on 13/03/2017.
 */

public class Person {
    public int id;
    public String nama;
    public String hp;
    public String email;
    public String alamat;

    public Person() {
    }

    public Person(String nama, String hp, String email, String alamat){
        this.nama = nama;
        this.hp = hp;
        this.email = email;
        this.alamat = alamat;
    }

    public Person(int id, String nama, String hp, String email, String alamat){
        this.id = id;
        this.nama = nama;
        this.hp = hp;
        this.email = email;
        this.alamat = alamat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
