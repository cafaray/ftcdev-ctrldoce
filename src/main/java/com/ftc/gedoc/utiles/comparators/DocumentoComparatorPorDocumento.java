package com.ftc.gedoc.utiles.comparators;

import com.ftc.modelo.Documento;

import java.util.Comparator;

public class DocumentoComparatorPorDocumento implements Comparator<Documento>{
    
    @Override
    public int compare(Documento o1, Documento o2) {
        return o1.getObservaciones().concat(o1.getTitulo()).compareTo(o2.getObservaciones().concat(o2.getTitulo()));
    }
    
}
