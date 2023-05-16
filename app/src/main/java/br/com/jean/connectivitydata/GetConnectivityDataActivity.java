package br.com.jean.connectivitydata;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
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

import java.util.ArrayList;
import java.util.List;

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
        listAdapter.atualizar();
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

                        if (telephonyManager != null)
                            movel = getInfoTelephony();


                        connectivityStattementsDao.addRegister(new ConnectivityStattement(wifi, movel, latitude, longitude));
                        listAdapter.atualizar();
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


    private int getInfoTelephony() {
        int mobileSignalStrength = 0;
        int mobileSignalLevel = 0;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
            if (cellInfoList != null) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {//sinal é GSM (3G)
                        CellSignalStrengthGsm gsmSignalStrength = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        mobileSignalStrength = gsmSignalStrength.getDbm();
                        break;
                    } else if (cellInfo instanceof CellInfoCdma) {//sinal é CDMA (2G)
                        CellSignalStrengthCdma cdmaSignalStrength = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        mobileSignalStrength = cdmaSignalStrength.getDbm();
                        break;
                    } else if (cellInfo instanceof CellInfoLte) {//sinal é LTE (4G)
                        CellSignalStrengthLte lteSignalStrength = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        mobileSignalStrength = lteSignalStrength.getDbm();
                        break;
                    }
                }
            }
            if (mobileSignalStrength <= -140) {
                mobileSignalLevel = 0;
            } else if (mobileSignalStrength >= -50) {
                mobileSignalLevel = 100;
            } else {
                mobileSignalLevel = (2 * (mobileSignalStrength + 140));
            }
        }

        return mobileSignalLevel;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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