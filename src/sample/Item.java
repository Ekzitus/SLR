package sample;

import java.util.regex.Pattern;

public class Item {

	private Production 	production;
	private String 		markered;
	private int 		marker;
	Symbols symbols;

	Item(Production production, int marker, Symbols symbols){
		this.symbols = symbols;
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
			var patternNoTerminals = Pattern.compile(symbols.getNoTerminals());
			var matcherNoTerminals = patternNoTerminals.matcher(markered);
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
		var rigth = this.production.getRigth();
		var patternSymbols = Pattern.compile(symbols.getSymbols());
		var matcherSymbols = patternSymbols.matcher(rigth);
		var flag = false;
		for(int i = 0; i <= marker; i++) {
				flag = matcherSymbols.find();
		}
		if(flag) {
			this.markered = rigth.substring(matcherSymbols.start(), matcherSymbols.end());
		}else {
			this.markered = "";
		}
		
	}
}
