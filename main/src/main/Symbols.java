package main;

public class Symbols {
	
	static String symbols = "";
	
	void setSymbols(String symbols){
		Symbols.symbols += symbols;
	}
	
	String getSymbols() {
		if(!symbols.substring(symbols.length()-1).equals("|")) {
			return symbols;
		}else {
			return symbols.substring(0, symbols.length()-1);
		}
	}
}
