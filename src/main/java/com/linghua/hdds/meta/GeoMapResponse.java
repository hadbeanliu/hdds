package com.linghua.hdds.meta;

import java.util.List;

public class GeoMapResponse {

    private String info;
    private int count;
    private int status;
    private List<Geocode> geocodes;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Geocode> getGeocodes() {
        return geocodes;
    }

    public void setGeocodes(List<Geocode> geocodes) {
        this.geocodes = geocodes;
    }
    public String getLocation(){
        if(this.getGeocodes()==null||this.getGeocodes().size()==0)
            return null;
        return this.getGeocodes().get(0).getLocation();
    }
}
class Geocode{
    private String formatted_address;
    private String province;
    private String citycode;
    private String city;
    private String location;
    private String level;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
