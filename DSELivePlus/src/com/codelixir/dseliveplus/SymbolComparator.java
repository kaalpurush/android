package com.codelixir.dseliveplus;

import java.util.Comparator;

public class SymbolComparator implements Comparator<Symbol>{
	 
    @Override
    public int compare(Symbol s1, Symbol s2) { 
        return s1.code.compareTo(s2.code);
    }
}