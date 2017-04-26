package id.co.unila.adi.laundryapp.rest_api;

import com.google.gson.JsonElement;

import java.util.List;

import id.co.unila.adi.laundryapp.model.OrderForm;
import id.co.unila.adi.laundryapp.model.Person;
import id.co.unila.adi.laundryapp.model.PersonOrder;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by japra_awok on 13/03/2017.
 */

public interface PersonEndpoint {
    @POST("mst-person")
    Call<Person> register(@Body Person person);

    @GET("mst-person/login")
    Call<Person> login(
            @Query("email") String email,
            @Query("hp") String hp
    );

    @GET("trn-person-order")
    Call<List<PersonOrder>> getOrders(@Query("TrnPersonOrderSearch[person_id]") String personId);

    @POST("trn-person-order")
    Call<PersonOrder> order(@Body OrderForm orderForm);
}
