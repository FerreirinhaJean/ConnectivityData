package br.com.jean.connectivitydata.services;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import br.com.jean.connectivitydata.repositories.ConnectivityStattementRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectivityService {

    private final String URL = "https://apiconectividadeunisc--lucasfreitag.repl.co/list";

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

    public void enviarObjetoAPI(List<ConnectivityStattement> objeto, ConnectivityStattementRepository repository) {
        new Thread(() -> {
            List<ConnectivityStattementDto> connectivityStattementDtos = new ArrayList<>();
            for (ConnectivityStattement obj : objeto) {
                connectivityStattementDtos.add(new ConnectivityStattementDto(obj.getLatitude(), obj.getLongitude(), obj.getWifi(), obj.getMovel(),
                        obj.getLevel(), convertNetworkType(obj.getNetworkType())));
            }

            Gson gson = new Gson();
            String json = gson.toJson(connectivityStattementDtos);
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(URL)
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String resposta = response.body().string();
                    Log.d("enviarObjetoAPI", "DEU CERTO: " + resposta);

                    for (ConnectivityStattement obj : objeto) {
                        repository.updateIsSynchronized(obj.getId());
                    }
                } else {
                    Log.d("enviarObjetoAPI", "DEU ERRO: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
