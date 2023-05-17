package br.com.jean.connectivitydata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.com.jean.connectivitydata.adapter.ListAdapter;
import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dao.ConnectivityStattementsDao;

public class GetConnectivityDataActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private RecyclerView recyclerView;
    private Button btParar;
    private ListAdapter listAdapter;
    private ConnectivityStattementsDao connectivityStattementsDao = new ConnectivityStattementsDao();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_connectivity_data);

        btParar = findViewById(R.id.btParar);
        recyclerView = findViewById(R.id.rvDados);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        listAdapter = new ListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        btParar.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityStattementsDao.clearAll();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.update();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        ) {
            locationListener = location -> {
                if (hasLocationPermission()) {
                    float speed = location.getSpeed();
                    if (speed > 0.15F) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        int signalStrength = wifiInfo.getRssi();
                        double wifi = WifiManager.calculateSignalLevel(signalStrength, 100);
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        double movel = 0.0;
                        int networkType = getNetworkType();

                        ConnectivityStattement connectivityStattement = new ConnectivityStattement();
                        connectivityStattement.setWifi(wifi);
                        connectivityStattement.setLatitude(latitude);
                        connectivityStattement.setLongitude(longitude);
                        connectivityStattement.setNetworkType(networkType);

                        if (telephonyManager != null)
                          connectivityStattement.setMovel(getInfoTelephony(connectivityStattement));

                        connectivityStattementsDao.addRegister(connectivityStattement);
                        listAdapter.update();
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private int getNetworkType() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            return telephonyManager.getNetworkType();
        }
        return TelephonyManager.NETWORK_TYPE_UNKNOWN;
    }

    private double getInfoTelephony(ConnectivityStattement conn) {
        int signalStrengthValue = 0;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                SignalStrength signalStrength = telephonyManager.getSignalStrength();
                signalStrength.getLevel();
                int networkType = conn.getNetworkType();
                Log.d("SINAL", "networkType: " + networkType);

                if (networkType == TelephonyManager.NETWORK_TYPE_CDMA) {//3G
                    signalStrengthValue = signalStrength.getCdmaDbm();
                }
                if (networkType == TelephonyManager.NETWORK_TYPE_GSM) {//2G
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
                }
                if (networkType == TelephonyManager.NETWORK_TYPE_LTE) {//4G
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        signalStrengthValue = telephonyManager.getAllCellInfo().get(0).getCellSignalStrength().getDbm();
                        Log.d("SINAL", "signalStrengthValue: " + signalStrengthValue);
                    }
                }

                if (signalStrengthValue <= -113 || signalStrengthValue == 0) return 0;
                if (signalStrengthValue >= -51) return 100;

                return (signalStrengthValue + 113) * (100 / 62);

            }
        }
        return signalStrengthValue;
    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                showToast("Permissão de acesso à localização negada.");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}