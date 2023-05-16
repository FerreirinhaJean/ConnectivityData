package br.com.jean.connectivitydata.models.dao;

import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.models.ConnectivityStattement;

public class ConnectivityStattementsDao {
    private static final List<ConnectivityStattement> connectivityStattements = new ArrayList<>();

    public void addRegister(ConnectivityStattement connectivityStattement) {
        this.connectivityStattements.add(0, connectivityStattement);
    }

    public List<ConnectivityStattement> getAll() {
        return new ArrayList<>(connectivityStattements);
    }

    public void clearAll() {
        this.connectivityStattements.clear();
    }

}
