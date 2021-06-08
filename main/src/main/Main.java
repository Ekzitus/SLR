package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	static List<Production> productions = 	new ArrayList<>();
	static Map<Key, FSM>	fsm = 		new HashMap<>();
	static String simvoli = "";

	public static void main(String[] args) throws Exception {
//    	System.out.println("arg0 = srcGrammar; arg1 = Input");
    	String filename = args[0];									// Имя файла с грамматикой заданной формате БНФ
    	String grammar = Read(filename);
    	genSymbols(grammar);
//    	System.out.println(Symbols.getSymbols() + "\n"
//    			+ Symbols.getNoTerminals() + "\n" 
//    			+ Arrays.toString(Symbols.getTerminals().toArray()) + "\n"
//    			+ Arrays.toString(firstTerminal("E").toArray()) + "\n"
//   			+ Arrays.toString(nextTerminal("E").toArray()));
    	buildFSM();
    	
//    	System.out.println(args[1]);
    	Pattern patternInput = Pattern.compile(Symbols.getSymbols());
    	Matcher matcherInput = patternInput.matcher(args[1]);
    	matcherInput.find();
    	String term = args[1].substring(matcherInput.start(),matcherInput.end());
//    	simvoli += term;
//        System.out.println(term);
        boolean accepted = false;
        Stack<String> stack = new Stack<>();
        stack.push("0");
        System.out.println(stack);
        while(!accepted) {
        	String[] s = {stack.peek(), term};
        	FSM state = fsm.get(new Key(s));
        	switch(state.operation) {
        	case "Success":
        		accepted = true;
        		break;
        	case "Shift":
        		stack.push(Integer.toString(state.index));
        		System.out.println(stack);
        		if(matcherInput.find()) {
        			simvoli += term;
        			System.out.println(simvoli);
		    		term = args[1].substring(matcherInput.start(),matcherInput.end());
//		            System.out.println(term);
//		            simvoli += term;
        		}else {
        			simvoli += term;
        			System.out.println(simvoli);
        			term = "";
//		            System.out.println(term);
        		}
        		break;
        	case "Reduce":
        		Pattern patternReduce = Pattern.compile(Symbols.getSymbols());
            	Matcher matcherReduce = patternReduce.matcher(state.production.getRigth());
        		while(matcherReduce.find()) {
        			simvoli = simvoli.substring(0, simvoli.length() - (matcherReduce.end() - matcherReduce.start()));
        			stack.pop();
//        			System.out.println(stack);
        		}
            	s = new String[]{stack.peek(), state.production.getLeft()};
            	simvoli += state.production.getLeft();
            	System.out.println(simvoli);
            	//stack.push(fsm.get(new Key(s)).production.getLeft());
            	stack.push(Integer.toString(fsm.get(new Key(s)).index));
            	System.out.println(stack);
        		break;
        	default:
        		System.out.println("Пук-среньк");;
        	}
        }
    	
    }
	
	static void buildFSM() {
		List<Item> itemStart = new ArrayList<>(1);
		itemStart.add(new Item(productions.get(0), 0));
		
		List<List<Item>> states = new ArrayList<>();
		states.add(closeItem(itemStart));
		
		for(int i = 0; i < states.size(); i++) {
			addReducing(states.get(i), i);
			for(String x: Symbols.getSymbolsList()) {
				List<Item> shiftStateReturn = shiftState(states.get(i), i, x);
				if(!shiftStateReturn.isEmpty()) {
					boolean flag = true;
					int indexBack = 0;
					for(int k = 0; k < states.size(); k++) {
						int count = 0;
						if(states.get(k).size() == shiftStateReturn.size()) {
							for(int j = 0; j < states.get(k).size(); j++) {
								if(states.get(k).get(j).getProduction() == shiftStateReturn.get(j).getProduction() &&
									states.get(k).get(j).getMarker() == shiftStateReturn.get(j).getMarker())
									count++;
							}
							if(count == states.get(k).size()) {
								flag = false;
								indexBack = k;
							}
						}
					}
					if(flag) 
					{
						states.add(shiftStateReturn);
						String[] s = {Integer.toString(i), x};
						Key key = new Key(s);
						fsm.put(key, new FSM(i, x));
						fsm.get(key).setShift(states.size()-1);
					}else {
						String[] s = {Integer.toString(i), x};
						Key key = new Key(s);
						fsm.put(key, new FSM(i, x));
						fsm.get(key).setShift(indexBack);
					}
				}
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
        
        boolean flag = false;
        for(int i = 0; i < Symbols.getTerminals().size(); i++) {
            if(X.equals(Symbols.getTerminals().get(i))) {
                flag = true;
            }
        }
        
        if(flag) {
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
    	for(int k = 0; k < i.size(); k++) {
    		if(i.get(k).getMarkeredType().equals("NoTerminal")) {
    			for(Production production: productions) {
    				if(production.getLeft().equals(i.get(k).getMarkered())) {
    					boolean flag = true;
    					for(int j = 0; j < i.size(); j++) {
    						if(i.get(j).getProduction().getLeft().equals(production.getLeft()) &&
    							i.get(j).getProduction().getRigth().equals(production.getRigth()) &&
    							i.get(j).getMarker() == 0) {
    							flag = false;
    						};
    					}
    					if(flag) {
        					i.add(new Item(production, 0));
    					}
    				}
    			}
    		}
    	}
    	
    	return i;
    }

    static void addReducing(List<Item> i, int index) {
    	for(Item item: i) {
    		if(item.getMarkered().equals("")) {
    			if(item.getProduction().getLeft().equals("E'")) {			// Видим стартовый символ
    				String[] s = {Integer.toString(index), ""};
    				Key key = new Key(s);
    				fsm.put(key, new FSM(index, ""));	// Записываем событие "УСПЕХ"
    				fsm.get(key).setSuccess();			// для поступаемой на вход пустой строки
    			}else {
    				ArrayList<String> array = nextTerminal(item.getProduction().getLeft());
    				array.add("");
    				for(String term: array) {
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
