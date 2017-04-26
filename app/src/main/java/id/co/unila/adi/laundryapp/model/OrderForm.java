package id.co.unila.adi.laundryapp.model;

/**
 * Created by japra_awok on 14/03/2017.
 */

public class OrderForm {
    public int person_id;
    public double lat, lng;

    public OrderForm() {
    }

    public OrderForm(int person_id, double lat, double lng) {
        this.person_id = person_id;
        this.lat = lat;
        this.lng = lng;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
