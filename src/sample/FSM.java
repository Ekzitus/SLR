package sample;

public class FSM {
	
	String		operation;		// Операция выполняемая автоматом
	Production 	production;		
	int			index;			// Индекс массива пунктов
	String		x;				// Входная строка терминал/нетерминал
	
	FSM(int index, String x) {
		this.index = index;
		this.x = x;
	}
	
	void setSuccess() {
		operation = "Success";
	}
	
	void setReduce(Production production) {
		operation = "Reduce";
		this.production = production; 
	}
	
	void setShift(int index) {
		this.operation = "Shift";
		this.index = index;
	}
}
