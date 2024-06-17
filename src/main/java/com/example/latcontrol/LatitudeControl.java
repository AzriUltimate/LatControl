package com.example.latcontrol;

import static java.lang.Math.abs;

public class LatitudeControl extends LatLongAbsControl{
    @Override
    public String getOutput() {
        updateTextfield();
        if (validateValues()){
            return "Долгота: "+getDegrees()+"° "+getMinutes()+"' "+getSeconds()+"\"";
        }
        else return "Значение долготы указано не корректно";
    }

    @Override
    protected boolean validateValues() {
        if (abs(getDegrees()) > 90) return false;
        if (getMinutes() >= 60||getSeconds()>=60) return false;
        if ((abs(getDegrees()) == 90) && (getMinutes() != 0||getSeconds()!=0)) return false;
        return true;
    }
}
