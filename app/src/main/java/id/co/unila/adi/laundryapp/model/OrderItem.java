package id.co.unila.adi.laundryapp.model;

import org.parceler.Parcel;

/**
 * Created by japra_awok on 14/03/2017.
 */

@Parcel
public class OrderItem {
    public String nama, quantity, harga, sub_total;

    public OrderItem() {
    }

    public OrderItem(String nama, String quantity, String harga, String sub_total) {
        this.nama = nama;
        this.quantity = quantity;
        this.harga = harga;
        this.sub_total = sub_total;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }
}
