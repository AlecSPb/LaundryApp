package id.co.unila.adi.laundryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.unila.adi.laundryapp.adapter.OrderRecyclerAdapter;
import id.co.unila.adi.laundryapp.helpers.OnItemClickListener;
import id.co.unila.adi.laundryapp.helpers.SessionManager;
import id.co.unila.adi.laundryapp.model.PersonOrder;
import id.co.unila.adi.laundryapp.rest_api.MyOkHttpInterceptor;
import id.co.unila.adi.laundryapp.rest_api.PersonEndpoint;
import id.co.unila.adi.laundryapp.rest_api.ServiceGenerator;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity{

    private SessionManager session;
    private ProgressDialog progress;
    private LinearLayoutManager mLinearLayoutManager;
    private OrderRecyclerAdapter mAdapter;

    private List<PersonOrder> personOrders = new ArrayList<>();

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orderan Anda");

        // Session Manager
        session = new SessionManager(getApplicationContext());

        progress = new ProgressDialog(this);
        progress.setTitle("Memuat data");
        progress.setMessage("Mohon tunggu......");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.setCancelable(false);

        ButterKnife.bind(this);

        mAdapter = new OrderRecyclerAdapter(this, personOrders);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                PersonOrder personOrder = personOrders.get(position);

                Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                intent.putExtra("PERSON_ORDER", Parcels.wrap(personOrder));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, BuatOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        getOrderData();
        super.onStart();
    }

    private void getOrderData(){
        personOrders.clear();

        progress.show();

        String personId = String.valueOf(session.getUserDetails().id);

        OkHttpClient httpClient = new MyOkHttpInterceptor().getOkHttpClient();
        PersonEndpoint service = ServiceGenerator.createService(PersonEndpoint.class, httpClient);
        Call<List<PersonOrder>> call = service.getOrders(personId);

        call.enqueue(new Callback<List<PersonOrder>>() {
            @Override
            public void onResponse(Call<List<PersonOrder>> call, Response<List<PersonOrder>> response) {
                progress.dismiss();
                if(response.isSuccessful()){
                    personOrders.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
                    Log.e("--", String.valueOf(session.getUserDetails().id));
                }else{
                    String failureMessage = response.message();
                    Toast.makeText(OrderActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PersonOrder>> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(OrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
