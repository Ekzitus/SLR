package main;

import java.util.List;

public class FSM {
	
	String		operation;		// Операция выполняемая автоматом
	Production 	production;
	List<Item>	i;				// Массив пунктов
	int			index;			// Индекс массива пунктов
	String		x;				// Входная строка терминал/нетерминал
	
	FSM(List<Item> i, int index, String x) {
		this.i = i;
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
