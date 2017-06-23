package com.admin.claire.garbag_truck;

import android.graphics.Color;

/**
 * Created by claire on 2017/5/16.
 */

public enum Colors {

    BLUE("#2b8ade"),
    LIGHTGRAY("#3d3838"),
    PURPLE("#a519ea"),
    GREEN("#99CC00"),
    ORANGE("#FFBB33"),
    RED("#FF4444");

    private String code;

    private Colors(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public int parseColor(){
        return Color.parseColor(code);
    }
}
