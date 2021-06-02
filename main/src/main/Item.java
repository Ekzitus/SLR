package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {

	private Production production;
	private String 		markered;
	private int 		marker;

	Item(Production production, int marker){
		this.setProduction(production);
		this.setMarkered(marker);
		this.setMarker(marker);
	}

	private void setMarker(int marker) {
		this.marker = marker;
	}
	
	public int getMarker() {
		return marker;
	}

	/**
	 * @return the production
	 */
	public Production getProduction() {
		return production;
	}

	/**
	 * @param production the production to set
	 */
	private void setProduction(Production production) {
		this.production = production;
	}
	
	public String getMarkeredType() {
		if(!markered.equals("")) {
			Pattern patternNoTerminals = Pattern.compile(Symbols.getNoTerminals());
			Matcher matcherNoTerminals = patternNoTerminals.matcher(markered);
			if(matcherNoTerminals.lookingAt()) {
				return "NoTerminal";
			}else {
				return "Terminal";
			}
		}
		return "";
	}
	
	public String getMarkered(){
		return markered;
	}

	/**
	 * @param marker the marker to set
	 */
	private void setMarkered(int marker) {
		String rigth = this.production.getRigth();
		Pattern patternSymbols = Pattern.compile(Symbols.getSymbols());
		Matcher matcherSymbols = patternSymbols.matcher(rigth);
		boolean flag = false;
		for(int i = 0; i <= marker; i++) {
				flag = matcherSymbols.find();
		}
		if(flag) {
			this.markered = rigth.substring(matcherSymbols.start(), matcherSymbols.end());
		}else {
			this.markered = "";
		}
		
	}
	
	String getMarkeredEnd(){
		String rigth = this.production.getRigth();
		Pattern patternSymbols = Pattern.compile(Symbols.getSymbols() + "$");
		Matcher matcherSymbols = patternSymbols.matcher(rigth);
		matcherSymbols.find();
		return rigth.substring(matcherSymbols.start(), matcherSymbols.end());
	}

}
