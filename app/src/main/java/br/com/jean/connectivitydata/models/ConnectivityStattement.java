package br.com.jean.connectivitydata.models;

public class ConnectivityStattement {
    private Long id;
    private Double wifi;
    private Double movel;
    private Double latitude;
    private Double longitude;
    private int networkType;
    private int level;

    public ConnectivityStattement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getWifi() {
        return wifi;
    }

    public void setWifi(Double wifi) {
        this.wifi = wifi;
    }

    public Double getMovel() {
        return movel;
    }

    public void setMovel(Double movel) {
        this.movel = movel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getNetworkType() {
        return networkType;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
