package id.co.unila.adi.laundryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.unila.adi.laundryapp.helpers.SessionManager;
import id.co.unila.adi.laundryapp.model.Person;
import id.co.unila.adi.laundryapp.rest_api.ErrorMessage;
import id.co.unila.adi.laundryapp.rest_api.MyOkHttpInterceptor;
import id.co.unila.adi.laundryapp.rest_api.PersonEndpoint;
import id.co.unila.adi.laundryapp.rest_api.ServiceGenerator;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog progress;
    SessionManager session;

    @BindView(R.id.etNama) EditText etNama;
    @BindView(R.id.etHp) EditText etHp;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etAlamat) EditText etAlamat;
    @BindView(R.id.btRegister) Button btRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registrasi");

        // Session Manager
        session = new SessionManager(getApplicationContext());

        progress = new ProgressDialog(this);
        progress.setTitle("Mengirim data");
        progress.setMessage("Mohon tunggu......");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btRegister)
    public void register() {
        String nama = etNama.getText().toString();
        String hp = etHp.getText().toString();
        String email = etEmail.getText().toString();
        String alamat = etAlamat.getText().toString();

        Person newPerson = new Person(nama, hp, email, alamat);

        progress.show();

        OkHttpClient httpClient = new MyOkHttpInterceptor().getOkHttpClient();
        PersonEndpoint service = ServiceGenerator.createService(PersonEndpoint.class, httpClient);
        Call<Person> call = service.register(newPerson);
        call.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                progress.dismiss();
                if(response.isSuccessful()){
                    Person person = response.body();
                    session.createLoginSession(person.id, person.nama, person.hp, person.email, person.alamat);

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();

                    Log.e("--", person.nama);
                }else{
                    String failureMessage = response.message();

                    try {
                        String errorBody = response.errorBody().string();

                        if(response.code() == 422){
                            Gson gson = new Gson();
                            Type errorType = new TypeToken<List<ErrorMessage>>(){}.getType();
                            List<ErrorMessage> errorMessages = gson.fromJson(errorBody, errorType);

                            String errMsg = failureMessage + "\n";
                            for(ErrorMessage err : errorMessages){
                                errMsg = errMsg.concat(err.field + ": " + err.message + "\n");
                            }
                            Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
