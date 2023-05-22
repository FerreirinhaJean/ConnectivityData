package br.com.jean.connectivitydata.models.dto;

public class ConnectivityStattementDto {
    private double latitude;
    private double longitude;
    private double wifi;
    private double movel;
    private int movelQualidade;
    private String movelTipoConec;

    public ConnectivityStattementDto(double latitude, double longitude, double wifi, double movel, int movelQualidade, String movelTipoConec) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.wifi = wifi;
        this.movel = movel;
        this.movelQualidade = movelQualidade;
        this.movelTipoConec = movelTipoConec;
    }
}
