package com.linghua.hdds.meta;

public enum ActionType {
    COLLECT(1,"clt"),DISCOLLECT(-1,"clt"),VIEW(1,"v"),LIKE(1,"lk"),DISLIKE(-1,"lk"),SHARE(1,"sh"),FOCUS(1,"fc");

    private String name;
    private int index;
    ActionType(int index,String name){
        this.index =index;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
