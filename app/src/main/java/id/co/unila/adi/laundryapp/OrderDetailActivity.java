package id.co.unila.adi.laundryapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.unila.adi.laundryapp.helpers.SessionManager;
import id.co.unila.adi.laundryapp.model.OrderItem;
import id.co.unila.adi.laundryapp.model.Person;
import id.co.unila.adi.laundryapp.model.PersonOrder;

public class OrderDetailActivity extends AppCompatActivity {

    private SessionManager session;
    private Person person;
    private PersonOrder personOrder;

    @BindView(R.id.tvWaktuOrder) TextView tvWaktuOrder;
    @BindView(R.id.tvNama) TextView tvNama;
    @BindView(R.id.tvHp) TextView tvHp;
    @BindView(R.id.tvAlamat) TextView tvAlamat;
    @BindView(R.id.tvOrderStatus) TextView tvOrderStatus;
    @BindView(R.id.llOrderItems) LinearLayout llOrderItems;
    @BindView(R.id.tvTotal) TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Orderan Anda");

        session = new SessionManager(getApplicationContext());
        person = session.getUserDetails();

        ButterKnife.bind(this);

        personOrder = Parcels.unwrap(getIntent().getParcelableExtra("PERSON_ORDER"));
    }

    @Override
    protected void onStart() {
        tvNama.setText(person.nama);
        tvHp.setText(person.hp);
        tvAlamat.setText(person.alamat);
        tvWaktuOrder.setText(personOrder.created_at);
        tvOrderStatus.setText(personOrder.status);
        tvTotal.setText(personOrder.total);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20,0,0,0);

        int i = 0;
        for(OrderItem item : personOrder.items){
            TextView tvNamaItem = new TextView(this);
            tvNamaItem.setText(item.nama);
            tvNamaItem.setTypeface(null, Typeface.BOLD);
            llOrderItems.addView(tvNamaItem, i);
            i++;



            TextView tvJumlahItem = new TextView(this);
            tvJumlahItem.setLayoutParams(lp);
            tvJumlahItem.setText("Jumlah: " + item.quantity);
            tvJumlahItem.setTypeface(null, Typeface.ITALIC);
            llOrderItems.addView(tvJumlahItem, i);
            i++;

            TextView tvHargaItem = new TextView(this);
            tvHargaItem.setLayoutParams(lp);
            tvHargaItem.setText("Harga: " + item.harga);
            tvHargaItem.setTypeface(null, Typeface.ITALIC);
            llOrderItems.addView(tvHargaItem, i);
            i++;

            LinearLayout.LayoutParams lpend = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpend.setMargins(20,0,0,50);
            TextView tvSubTotal = new TextView(this);
            tvSubTotal.setLayoutParams(lpend);
            tvSubTotal.setText("Subtotal: " + item.sub_total);
            tvSubTotal.setTypeface(null, Typeface.ITALIC);
            llOrderItems.addView(tvSubTotal, i);
            i++;
        }
        super.onStart();
    }
}
