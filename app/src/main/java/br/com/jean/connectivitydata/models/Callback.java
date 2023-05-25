package br.com.jean.connectivitydata.models;

public interface Callback<T> {
    void onSuccess(T result);

    void onFailure(String errorMessage);
}

