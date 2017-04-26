package id.co.unila.adi.laundryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
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

public class LoginActivity extends AppCompatActivity {

    ProgressDialog progress;
    SessionManager session;

    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etHp) EditText etHp;
    @BindView(R.id.btLogin) Button btLogin;
    @BindView(R.id.btDaftar) Button btDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Masuk");

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

    @OnClick(R.id.btLogin)
    public void login() {
        String email = etEmail.getText().toString();
        String hp = etHp.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email harus diisi", Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(hp)){
            Toast.makeText(this, "HP harus diisi", Toast.LENGTH_LONG).show();
        }else{
            progress.show();

            OkHttpClient httpClient = new MyOkHttpInterceptor().getOkHttpClient();
            PersonEndpoint service = ServiceGenerator.createService(PersonEndpoint.class, httpClient);
            Call<Person> call = service.login(email, hp);

            call.enqueue(new Callback<Person>() {
                @Override
                public void onResponse(Call<Person> call, Response<Person> response) {
                    progress.dismiss();
                    if(response.isSuccessful()){
                        Person person = response.body();
                        session.createLoginSession(person.id, person.nama, person.hp, person.email, person.alamat);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();

                        Log.e("--", person.nama);
                    }else{
                        Gson gson = new Gson();
                        String failureMessage = response.message();

                        try {
                            String errorBody = response.errorBody().string();

                            if(response.code() == 422){
                                Type errorType = new TypeToken<List<ErrorMessage>>(){}.getType();
                                List<ErrorMessage> errorMessages = gson.fromJson(errorBody, errorType);

                                String errMsg = failureMessage + "\n";
                                for(ErrorMessage err : errorMessages){
                                    errMsg = errMsg.concat(err.field + ": " + err.message + "\n");
                                }
                                Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
                            }else{
                                JsonObject errObj = gson.fromJson(errorBody, JsonObject.class);
                                Toast.makeText(LoginActivity.this, errObj.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Person> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @OnClick(R.id.btDaftar)
    public void daftar() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }
}
