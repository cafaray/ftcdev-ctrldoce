package com.ftc.gedoc.utiles.comparators;

import com.ftc.modelo.Documento;

import java.util.Comparator;

public class DocumentoComparatorPorEstatus  implements Comparator<Documento>{
    @Override
    public int compare(Documento o1, Documento o2) {
        return o1.getDsEstatus().compareTo(o2.getDsEstatus());
    }
}
