package br.com.jean.connectivitydata.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.models.ConnectivityStattement;
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
            values.put("is_synchronized", connectivity.isSynchronized());

            return database.insert("connectivity", null, values);
        } catch (Exception exception) {
            throw exception;
        } finally {
            close();
        }
    }

    public List<ConnectivityStattement> getAllConnectivityData() {
        try {
            open();

            String[] allColumns = {
                    "id",
                    "wifi",
                    "movel",
                    "latitude",
                    "longitude",
                    "network_type",
                    "level",
                    "is_synchronized"
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

            List<ConnectivityStattement> connectivityStattements = new ArrayList<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex("id");
                    int latitudeIndex = cursor.getColumnIndex("latitude");
                    int longitudeIndex = cursor.getColumnIndex("longitude");
                    int wifiIndex = cursor.getColumnIndex("wifi");
                    int movelIndex = cursor.getColumnIndex("movel");
                    int levelIndex = cursor.getColumnIndex("level");
                    int networkTypeIndex = cursor.getColumnIndex("network_type");
                    int isSynchronizedIndex = cursor.getColumnIndex("is_synchronized");

                    long id = cursor.getLong(idIndex);
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    double wifi = cursor.getDouble(wifiIndex);
                    double movel = cursor.getDouble(movelIndex);
                    int level = cursor.getInt(levelIndex);
                    int t = cursor.getInt(isSynchronizedIndex);
                    boolean isSynchronized = cursor.getInt(isSynchronizedIndex) != 0;
                    int networkType = cursor.getInt(networkTypeIndex);

                    connectivityStattements.add(new ConnectivityStattement(id, wifi, movel, latitude, longitude, networkType, level, isSynchronized));
                } while (cursor.moveToNext());
            }

            return connectivityStattements;

        } catch (Exception exception) {
            throw exception;
        } finally {
            close();
        }
    }

    public void updateIsSynchronized(Long id) {
        try {
            open();

            ContentValues values = new ContentValues();
            values.put("is_synchronized", true);

            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(id)};

            int rowsAffected = database.update("connectivity", values, whereClause, whereArgs);

            if (rowsAffected > 0) {
                Log.d("DBHelper", "Registrado com sucesso");
            } else {
                Log.d("DBHelper", "Erro no registro");
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
//            close();
        }
    }

    public void deleteAllConnectivityRecords() {
        try {
            open();
            database.delete("connectivity", null, null);
        } catch (Exception exception) {
            throw exception;
        } finally {
            close();
        }

    }

}
