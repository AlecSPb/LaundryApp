package id.co.unila.adi.laundryapp.model;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by japra_awok on 14/03/2017.
 */

@Parcel
public class PersonOrder {
    public int id, person_id;
    public String status;
    public double lat, lng;
    public String created_at;
    public List<OrderItem> items;
    public String total;

    public PersonOrder() {
    }

    public PersonOrder(int id, int person_id, String status, double lat, double lng, String created_at, List<OrderItem> items, String total) {
        this.id = id;
        this.person_id = person_id;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.created_at = created_at;
        this.items = items;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
