package br.com.jean.connectivitydata.services;

import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.models.Callback;
import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import br.com.jean.connectivitydata.repositories.ConnectivityStattementRepository;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectivityService {

    private static final String URL = "https://apiconectividadeunisc--lucasfreitag.repl.co/list";
    private static final String NETWORK_TYPE_2G = "2G";
    private static final String NETWORK_TYPE_3G = "3G";
    private static final String NETWORK_TYPE_4G = "4G";
    private static final String NETWORK_TYPE_5G = "5G";
    private static final String NETWORK_TYPE_UNKNOWN = "Desconhecido";


    private String convertNetworkType(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return NETWORK_TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_TYPE_4G;
            case TelephonyManager.NETWORK_TYPE_NR:
                return NETWORK_TYPE_5G;
            default:
                return NETWORK_TYPE_UNKNOWN;
        }
    }

    public void enviarObjetoAPI(List<ConnectivityStattement> objeto, ConnectivityStattementRepository repository, Callback<String> callback) {
        new Thread(() -> {
            try {
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

                Response response = client.newCall(request).execute();
                Log.d("enviarObjetoAPI", "response: " + response);
                if (response.isSuccessful()) {
                    for (ConnectivityStattement obj : objeto) {
                        repository.updateIsSynchronized(obj.getId());
                    }
                    callback.onSuccess("Dados enviados com sucesso!");
                } else {
                    callback.onFailure("Falha ao enviar os dados");
                }
            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure("Falha ao enviar os dados");
            }
        }).start();
    }
}
