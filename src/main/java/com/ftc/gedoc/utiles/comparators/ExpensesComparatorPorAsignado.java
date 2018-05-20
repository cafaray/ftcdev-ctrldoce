package com.ftc.gedoc.utiles.comparators;

import com.ftc.modelo.PeriodoCabecera;
import java.util.Comparator;

public class ExpensesComparatorPorAsignado implements Comparator<PeriodoCabecera>{
    
    @Override
    public int compare(PeriodoCabecera o1, PeriodoCabecera o2) {
        return o1.getAsociadoA().compareTo(o2.getAsociadoA());
    }

    
}
