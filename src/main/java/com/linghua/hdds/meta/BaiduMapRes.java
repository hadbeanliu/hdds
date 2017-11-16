package com.linghua.hdds.meta;

public class BaiduMapRes {

    private int status;
    private String level;
    private int precise;
    private int confidence;
    private Result result;

    public void setStatus(int status){
        this.status=status;
    }

    public int getStatus(){
        return status;
    }

    public void setLevel(String level){
        this.level=level;
    }
    public String getLevel(){
        return level;
    }
    public void setPrecise(int precise){
        this.precise=precise;
    }

    public int getPrecise(){
        return precise;
    }
    public void setConfidence(int confidence){
        this.confidence=confidence;
    }

    public int getConfidence(){
        return confidence;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
    //    private

    private class Result{
        private String lng;
        private String lat;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
    }
}
