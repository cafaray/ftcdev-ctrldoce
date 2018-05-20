package com.ftc.gedoc.utiles.comparators;

import com.ftc.modelo.CEPArchivo;
import java.util.Comparator;

public class CEPComparatorPorDocumento implements Comparator<CEPArchivo>{
    
    @Override
    public int compare(CEPArchivo o1, CEPArchivo o2) {
        return o1.getUuid().compareToIgnoreCase(o2.getUuid());
    }
}
