package com.ftc.gedoc.utiles.comparators;

import com.ftc.modelo.Documento;

import java.util.Comparator;

public class DocumentoComparatorPorEmpresa implements Comparator<Documento>{
    
    @Override
    public int compare(Documento o1, Documento o2) {
        return o1.getEmpresa().compareToIgnoreCase(o2.getEmpresa());
    }
    
}
