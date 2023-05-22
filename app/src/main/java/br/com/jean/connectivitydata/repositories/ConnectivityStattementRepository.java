package br.com.jean.connectivitydata.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import br.com.jean.connectivitydata.repositories.db.DbHelper;

public class ConnectivityStattementRepository {
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    public ConnectivityStattementRepository(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private String convertNetworkType(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "Desconhecido";
        }
    }

    public long insertConnectivityData(ConnectivityStattement connectivity) {
        try {
            open();

            ContentValues values = new ContentValues();
            values.put("wifi", connectivity.getWifi());
            values.put("movel", connectivity.getMovel());
            values.put("latitude", connectivity.getLatitude());
            values.put("longitude", connectivity.getLongitude());
            values.put("network_type", connectivity.getNetworkType());
            values.put("level", connectivity.getLevel());

            return database.insert("connectivity", null, values);
        } catch (Exception exception) {
            throw exception;
        } finally {
            close();
        }
    }

    public List<ConnectivityStattementDto> getAllConnectivityData() {
        try {
            open();

            String[] allColumns = {
                    "id",
                    "wifi",
                    "movel",
                    "latitude",
                    "longitude",
                    "network_type",
                    "level"
            };

            Cursor cursor = database.query(
                    "connectivity",
                    allColumns,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            List<ConnectivityStattementDto> connectivityStattementDtos = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int latitudeIndex = cursor.getColumnIndex("latitude");
                    int longitudeIndex = cursor.getColumnIndex("longitude");
                    int wifiIndex = cursor.getColumnIndex("wifi");
                    int movelIndex = cursor.getColumnIndex("movel");
                    int levelIndex = cursor.getColumnIndex("level");
                    int networkTypeIndex = cursor.getColumnIndex("network_type");

                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    double wifi = cursor.getDouble(wifiIndex);
                    double movel = cursor.getDouble(movelIndex);
                    int level = cursor.getInt(levelIndex);
                    String networkType = convertNetworkType(cursor.getInt(networkTypeIndex));

                    connectivityStattementDtos.add(new ConnectivityStattementDto(latitude, longitude, wifi, movel, level, networkType));
                } while (cursor.moveToNext());
            }

            return connectivityStattementDtos;

        } catch (Exception exception) {
            throw exception;
        } finally {
            close();
        }
    }

}
