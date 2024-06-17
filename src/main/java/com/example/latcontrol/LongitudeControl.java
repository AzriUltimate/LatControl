package com.example.latcontrol;

import static java.lang.Math.abs;

public class LongitudeControl extends LatLongAbsControl{
    @Override
    public String getOutput() {
        updateTextfield();
        if (validateValues()){
            return "Широта: "+getDegrees()+"° "+getMinutes()+"' "+getSeconds()+"\"";
        }
        else return "Значение широты указано не корректно";
    }

    @Override
    protected boolean validateValues() {
        if (abs(getDegrees()) > 180) return false;
        if (getMinutes() >= 60||getSeconds()>=60) return false;
        if ((abs(getDegrees()) == 180) && (getMinutes() != 0||getSeconds()!=0)) return false;
        return true;
    }
}
