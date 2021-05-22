package main;

public class Item {

	private Production production;
	private int marker;

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
	
	String getMarker1(){
		
		String Rigth = this.production.getRigth();
		
		//return Rigth.substring(beginmarker, endmarker);
		return null;
	}

	/**
	 * @param marker the marker to set
	 */
	public void setMarker(int marker) {
		this.marker = marker;
	}

}
