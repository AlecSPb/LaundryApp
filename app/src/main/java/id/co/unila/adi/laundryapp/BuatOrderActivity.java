package id.co.unila.adi.laundryapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.parceler.Parcels;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import id.co.unila.adi.laundryapp.helpers.LocationHelper;
import id.co.unila.adi.laundryapp.helpers.SessionManager;
import id.co.unila.adi.laundryapp.model.OrderForm;
import id.co.unila.adi.laundryapp.model.PersonOrder;
import id.co.unila.adi.laundryapp.rest_api.ErrorMessage;
import id.co.unila.adi.laundryapp.rest_api.MyOkHttpInterceptor;
import id.co.unila.adi.laundryapp.rest_api.PersonEndpoint;
import id.co.unila.adi.laundryapp.rest_api.ServiceGenerator;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuatOrderActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "BuatOrderActivity";

    private SessionManager session;

    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationHelper locationHelper;
    private Geocoder geocoder;

    private TextView tvLokasi;
    private ImageButton ibOrder;
    private MapFragment mapFragment;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buat Orderan Baru");

        session = new SessionManager(getApplicationContext());

        locationHelper = new LocationHelper(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        tvLokasi = (TextView) findViewById(R.id.tvLokasi);

        ibOrder = (ImageButton) findViewById(R.id.ibOrder);
        ibOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BuatOrderActivity.this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Anda yakin akan membuat orderan?");
                builder.setPositiveButton("Ya",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                stopLocationUpdates();
                                sendOrder();
                                //BuatOrderActivity.this.finish();
                            }
                        });
                builder.setNegativeButton("Batal",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        progress = new ProgressDialog(this);
        progress.setTitle("Memuat...");
        progress.setMessage("Menemukan lokasi terkini anda, mohon tunggu.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LocationHelper.MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    BuatOrderActivity.this.finish();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        locationHelper.checkGpsActive();
        mapFragment.getMapAsync(this);
        progress.show();
        super.onStart();
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        progress.dismiss();

        if (!locationHelper.checkLocationPermission()) {
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            LatLng currentLocationPoint = new LatLng(lat, lng);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocationPoint, 15);
            mMap.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    ibOrder.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancel() {

                }
            });
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "onMapReady");
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (locationHelper.checkLocationPermission()) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

        //mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.e("--", "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();

        mLastLocation = location;

        LatLng currPos = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currPos)
                .title("Lokasi anda"));

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            //String country = addresses.get(0).getCountryName();
            //String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            String lokasiAnda = city + "\n" + state + "\n" + knownName;
            tvLokasi.setText(lokasiAnda);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {
        if (!locationHelper.checkLocationPermission()){
        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void sendOrder(){
        double lat = mLastLocation.getLatitude();
        double lng = mLastLocation.getLongitude();
        int personId = session.getUserDetails().id;

        progress.setTitle("Mengirim data...");
        progress.setMessage("Mengirim permintaan order laundry anda, mohon tunggu.");
        progress.show();

        OrderForm order = new OrderForm(personId, lat, lng);

        OkHttpClient httpClient = new MyOkHttpInterceptor().getOkHttpClient();
        PersonEndpoint service = ServiceGenerator.createService(PersonEndpoint.class, httpClient);
        Call<PersonOrder> call = service.order(order);
        call.enqueue(new Callback<PersonOrder>() {
            @Override
            public void onResponse(Call<PersonOrder> call, Response<PersonOrder> response) {
                progress.dismiss();
                if(response.isSuccessful()){
                    Intent intent = new Intent(BuatOrderActivity.this, OrderDetailActivity.class);
                    intent.putExtra("PERSON_ORDER", Parcels.wrap(response.body()));
                    startActivity(intent);
                    BuatOrderActivity.this.finish();
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
                            Toast.makeText(BuatOrderActivity.this, errMsg, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(BuatOrderActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Toast.makeText(BuatOrderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    startLocationUpdates();
                }
            }

            @Override
            public void onFailure(Call<PersonOrder> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(BuatOrderActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                startLocationUpdates();
            }
        });
    }
}
