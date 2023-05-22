package br.com.jean.connectivitydata.services;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectivityService {

    private final String URL = "https://apiconectividadeunisc--lucasfreitag.repl.co/list";

    public void enviarObjetoAPI(List<ConnectivityStattementDto> objeto) {
        new Thread(() -> {
            Gson gson = new Gson();
            String json = gson.toJson(objeto);
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
                } else {
                    Log.d("enviarObjetoAPI", "DEU ERRO: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
