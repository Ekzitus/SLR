package main;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item {

	private Production production;
	private String marker;

	Item(Production production, int marker){
		this.setProduction(production);
		this.setMarker(marker);

		
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
	public void setProduction(Production production) {
		this.production = production;
	}
	
	String getMarkerType() {
		Pattern patternNoTerminals = Pattern.compile(Arrays.toString(Symbols.getNoTerminals().toArray()));
		Matcher matcherNoTerminals = patternNoTerminals.matcher(marker);
		if(matcherNoTerminals.lookingAt()) {
			return "NoTerminal";
		}else {
			return "Terminal";
		}
	}
	
	String getMarker(){
		return marker;
	}

	/**
	 * @param marker the marker to set
	 */
	public void setMarker(int marker) {
		String rigth = this.production.getRigth();
		Pattern patternSymbols = Pattern.compile(Symbols.getSymbols());
		Matcher matcherSymbols = patternSymbols.matcher(rigth);
		for(int i = 0; i <= marker; i++) {
				matcherSymbols.find();
		}
		this.marker = rigth.substring(matcherSymbols.start(), matcherSymbols.end());
	}
	
	String getEnd(){
		String rigth = this.production.getRigth();
		Pattern patternSymbols = Pattern.compile(Symbols.getSymbols() + "$");
		Matcher matcherSymbols = patternSymbols.matcher(rigth);
		matcherSymbols.find();
		return rigth.substring(matcherSymbols.start(), matcherSymbols.end());
	}

}
