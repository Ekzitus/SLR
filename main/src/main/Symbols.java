package main;

import java.util.ArrayList;
import java.util.List;

public class Symbols {
	
	static String symbols = "EOF|";
	static List<String> terminals = new ArrayList<>();
	static List<String> noTerminals = new ArrayList<>();
	
	final static void addSymbols(String symbols){
		Symbols.symbols += symbols;
	}
	
	final static String getSymbols() {
		if(!symbols.substring(symbols.length()-1).equals("|")) {
			return symbols;
		}else {
			return symbols.substring(0, symbols.length()-1);
		}
	}
	
	final static void addTerminals(String terminal) {
		terminals.add(terminal);
	}
	
	final static List<String> getTerminals() {		
		return terminals;
	}
	
	final static void addNoTerminals(String noTerminal) {
		noTerminals.add(noTerminal);
	}
	
	final static List<String> getNoTerminals() {
		return noTerminals;
	}
}
