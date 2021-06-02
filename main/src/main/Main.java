package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	/*
	* Массив правил 
	* Правила класс <String, String[]>
	*/
	static List<Production> productions = 	new ArrayList<>();
	static Map<Key, FSM>	fsm = 		new HashMap<>();

	public static void main(String[] args) {
    	System.out.println("arg0 = srcGrammar; arg1 = Input");
    	String filename = args[0];									// Имя файла с грамматикой заданной формате БНФ
    	String grammar = Read(filename);
    	genSymbols(grammar);
    	buildFSM();
    	
    	System.out.println(args[1]);
    }

	
	
	static void buildFSM() {
		List<Item> itemStart = new ArrayList<>(1);
		itemStart.add(new Item(productions.get(0), 0));
		
		List<List<Item>> states = new ArrayList<>();
		states.add(closeItem(itemStart));
		
		for(int i = 0; i <= states.size(); i++) {
			addReducing(states.get(i), i);
			for(String x: Symbols.getSymbolsList()) {
				states.add(shiftState(states.get(i), i, x));
				String[] s = {Integer.toString(i), x};
				Key key = new Key(s);
				fsm.put(key, new FSM(i, x));
				fsm.get(key).setShift(states.size());;
			}
		}
		
	}
	
	static void genSymbols(String grammar) {
        /*
         * Регулярное выражение
         * Для захвата левой части правила состоящего из символов английского алфавита 
         * и присваивание этой группе имя left
         * Для захвата правой части правила состоящего из любых символов 
         * и присваивания этой группе имя rigth
         */  
        String regexRules = "(?<left>.+)→(?<right>.+)";
        Pattern patternRules = Pattern.compile(regexRules);
        Matcher matcherRules = patternRules.matcher(grammar);
        int j = 0;
        while(matcherRules.find()) {
            Symbols.addNoTerminals(matcherRules.group("left"));                          // Помещяет
            Symbols.addSymbols(matcherRules.group("left") + "|");
            String regex_right = "\\|";                                     // Разделитель правой части правила
            Pattern pattern_right = Pattern.compile(regex_right);
            String[] strings = pattern_right.split(matcherRules.group("right"));  		// Разделяет правую часть правила по заданному разделителю
            for(String s: strings) {
            	productions.add(j, new Production(matcherRules.group("left"), s));
            	j++;
            }
        }
    	
        for(int i = 0; i < productions.size(); i++) {
            Pattern patternTerminal = Pattern.compile("[^" + Symbols.getNoTerminals() + "]");
            Matcher matcherTerminal = patternTerminal.matcher(productions.get(i).getRigth());
            while(matcherTerminal.find()) {
                String ter = productions.get(i).getRigth().substring(matcherTerminal.start(), matcherTerminal.end());// Найденный терминал
                Symbols.addTerminals(ter);
                if(ter.equals("+")) {
                	Symbols.addSymbols("\\" + ter + "|");
                }else if(ter.equals("(")) {
                	Symbols.addSymbols("\\" + ter + "|");
                }else if(ter.equals(")")) {
                	Symbols.addSymbols("\\" + ter + "|");
                }else if(ter.equals("*")) {
                	Symbols.addSymbols("\\" + ter + "|");
                }else {
                	Symbols.addSymbols(ter + "|");
                }
            }
        };
    }

    static ArrayList<String> firstTerminal(String X) {
        
        int flag = 0;
        for(int i = 0; i < Symbols.getTerminals().size(); i++) {
            if(X.equals(Symbols.getTerminals().get(i))) {
                flag = 1;
            }
        }
        
        if(flag == 1) {
            ArrayList<String> result1 = new ArrayList<String>();
            result1.add(X);
            return result1;
        }
 
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i < productions.size(); i++) {
            if(productions.get(i).getLeft().equals(X)) {
                Pattern pattern = Pattern.compile(Symbols.getSymbols());
                Matcher matcher = pattern.matcher(productions.get(i).getRigth());
                matcher.find();
                if(!productions.get(i).getRigth().substring(matcher.start(), matcher.end()).equals(X)) {
                    result.addAll(firstTerminal(productions.get(i).getRigth().substring(matcher.start(), matcher.end())));
                }
            }
        }
        return result;
    }

    static ArrayList<String> nextTerminal(String X){
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < productions.size(); i++) {
            int flag = 0;
            int mstart = 0;
            int mend = 0;
            Pattern pattern = Pattern.compile(Symbols.getSymbols());
            Matcher matcher = pattern.matcher(productions.get(i).getRigth());
            String s = null;
            while(matcher.find()) {
                s = productions.get(i).getRigth().substring(matcher.start(), matcher.end());
            }
            if(s.equals(X)) {
                if(result.equals(nextTerminal(productions.get(i).getLeft()))) {
                    result.addAll(new ArrayList<>());
                }else {
                    result.addAll(nextTerminal(productions.get(i).getLeft())); 
                }
            }else {
                matcher = pattern.matcher(productions.get(i).getRigth());
                while(matcher.find()) {
                    if(productions.get(i).getRigth().substring(matcher.start(), matcher.end()).equals(X)) {
                        flag = 1;
                    } else if (flag == 1){
                        mstart = matcher.start();
                        mend = matcher.end();
                        flag = 0;
                    }
                }
                if(mstart != 0 && mend != 0) {
                    result.addAll(firstTerminal(productions.get(i).getRigth().substring(mstart, mend)));
                }
            }
        }
        return result;
        
    }

    static List<Item> closeItem(List<Item> i){
    	List<Item> j = new ArrayList<>(i);
    	for(Item item: i) {
    		if(item.getMarkeredType().equals("NoTerminal")) {
    			for(Production production: productions) {
    				if(production.getLeft().equals(item.getMarkered())) {
    					j.add(new Item(production, 0));
    				}
    			}
    		}
    	}
    	return j;
    }

    static void addReducing(List<Item> i, int index) {
    	for(Item item: i) {
    		if(item.getMarkered().equals(item.getMarkeredEnd())) {
    			if(item.getProduction().getLeft().equals("E'")) {			// Видим стартовый символ
    				String[] s = {Integer.toString(index), ""};
    				Key key = new Key(s);
    				fsm.put(key, new FSM(index, ""));	// Записываем событие "УСПЕХ"
    				fsm.get(key).setSuccess();			// для поступаемой на вход пустой строки
    			}else {
    				for(String term: nextTerminal(item.getProduction().getLeft())) {
    					String[] s = {Integer.toString(index), term};
        				Key key = new Key(s);
    					fsm.put(key, new FSM(index, term));
    					fsm.get(key).setReduce(item.getProduction());
    				}
    			}
    		}
    	}
    }

    static List<Item> shiftState(List<Item> i, int index, String x){
    	List<Item> result = new ArrayList<>();
    	for(Item item: i) {
    		if(item.getMarkered().equals(x)) {
    			result.add(new Item(item.getProduction(), item.getMarker() + 1));
    		}
    	}

    	return closeItem(result);
    }

    static String Read(String filename) {
    	File file = new File(filename);
        try (FileInputStream reader = new FileInputStream(file)){
            byte[] bbuf = new byte[(int)file.length()]; 
            reader.read(bbuf);
            String data = new String(bbuf);
            reader.close();
            return data;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
