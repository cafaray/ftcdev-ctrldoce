package com.ftc.gedoc.utiles;

public class UtilDAO {

    public static String coverDoubleTildes(String value){
        return "\"".concat(value).concat("\"");
    }
    
    public static String coverSimpleTildes(String value){
        return "\'".concat(value).concat("\'");
    }
    
    
}
