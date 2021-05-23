package main;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class Main {
 
    /*
     * Массив правил 
     * Правила класс <String, String[]>
     */     
    static List<Rule> rules = new ArrayList<>();
    static List<Production> productions = new ArrayList<>();
    static List<Item> items = new ArrayList<>();
    static List<String> noterminal = new ArrayList<>();                // Массив нетерминальных символов
    static List<String> terminal = new ArrayList<>();              // Массив терминальных символов
    static String[] strings;
    
    public static void main(String[] args) {
        String filename = "grammars/g1";                                       // Имя файла с грамматикой заданной формате БНФ     
        File file = new File(filename);     
        String date = Read(file);
        
        /*
         * Регулярное выражение
         * Для захвата левой части правила состоящего из символов английского алфавита 
         * и присваивание этой группе имя left
         * Для захвата правой части правила состоящего из любых символов 
         * и присваивания этой группе имя rigth
         */
        String regex = "(?<left>.+)→(?<right>.+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        int i = 0;
        int j = 0;
        while(matcher.find()) {
            Symbols.addNoTerminals(matcher.group("left"));                          // Помещяет
            Symbols.addSymbols(matcher.group("left") + "|");
            String regex_right = "\\|";                                     // Разделитель правой части правила
            Pattern pattern_right = Pattern.compile(regex_right);
            strings = pattern_right.split(matcher.group("right"));  		// Разделяет правую часть правила по заданному разделителю
            rules.add(i, new Rule(matcher.group("left"), strings));
            for(String s: strings) {
            	productions.add(j, new Production(matcher.group("left"), s));
            	j++;
            }
            i++;
        }
        
        for(i = 0; i < rules.size(); i++) {
            for(String s: rules.get(i).getRigth()) {
                pattern = Pattern.compile("[^" + Arrays.toString(Symbols.getNoTerminals().toArray()) + "]");
                matcher = pattern.matcher(s);
                while(matcher.find()) {
                    String ter = s.substring(matcher.start(), matcher.end());// Найденный терминал
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
            }
        };
        
        
        /*
         * Забиваю массив пунктов пунктами
         * По типу (Правило (String), Пункты (item.getMarker (Номер маркера (int)))
         */
        
//      pattern = Pattern.compile(regex);
//      matcher = pattern.matcher(date);
//      i = 0;
//      while(matcher.find()) {
//          String regex_right = "\\|";                             // Разделитель правой части правила
//          Pattern pattern_right = Pattern.compile(regex_right);
//          strings = pattern_right.split(matcher.group("right"));  // Разделяет правую часть правила по заданному разделителю
//          rules.add(i, new rule(matcher.group("left"), strings));
//          for(int j = 0; j < strings.length; j++) {
//              Pattern pattern_symbol = Pattern.compile(symbol);
//              matcher = pattern_symbol.matcher(strings[j]);
//              ArrayList<String> item = new ArrayList<String>((int)matcher.results().count());
//              int q = 0;
//              while(matcher.find()) {
//                  item.add(strings[j].substring(matcher.start(), matcher.end()));
//                  q++;
//              }
//              items.put(matcher.group("left") + "→"+ strings[j], new item(item));
//          }
//          i++;
//      }
        
        System.out.println(firstTerminal("E"));
        System.out.println(nextTerminal("id"));
        List<Item> a = new ArrayList<>();
        a.add(new Item(productions.get(0),0));
        for(Item item: closeItem(a)) {
        	System.out.println("Left: " + item.getProduction().getLeft() + "\n" 
        			+ "Rigth: " + item.getProduction().getRigth() + "\n"
        			+ "Marker: " + item.getMarker() + "\n"
        			+ "Marker Type: " + item.getMarkerType());
        }
    }
    
    static List<Item> closeItem(List<Item> I){
    	List<Item> J = new ArrayList<>(I);
    	for(Item item: I) {
    		if(item.getMarkerType().equals("NoTerminal")) {
    			for(Production production: productions) {
    				if(production.getLeft().equals(item.getMarker())) {
    					J.add(new Item(production, 0));
    				}
    			}
    		}
    	}
    	return J;
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
        for(int i = 0; i < rules.size(); i++) {
            if(rules.get(i).getLeft().equals(X)) {
                for(int j = 0; j < rules.get(i).getRigth().length;j++) {
                    Pattern pattern = Pattern.compile(Symbols.getSymbols());
                    Matcher matcher = pattern.matcher(rules.get(i).getRigth()[j]);
                    matcher.find();
                    if(!rules.get(i).getRigth()[j].substring(matcher.start(), matcher.end()).equals(X)) {
                        result.addAll(firstTerminal(rules.get(i).getRigth()[j].substring(matcher.start(), matcher.end())));
                    }
                }
            }
        }
        return result;
    }
    
    static ArrayList<String> nextTerminal(String X){
        ArrayList<String> result = new ArrayList<>();
        for(int i = 0; i < rules.size(); i++) {
            for(int j = 0; j < rules.get(i).getRigth().length; j++) {
                int flag = 0;
                int mstart = 0;
                int mend = 0;
                Pattern pattern = Pattern.compile(Symbols.getSymbols());
                Matcher matcher = pattern.matcher(rules.get(i).getRigth()[j]);
                String s = null;
                while(matcher.find()) {
                    s = rules.get(i).getRigth()[j].substring(matcher.start(), matcher.end());
                }
                if(s.equals(X)) {
                    if(result.equals(nextTerminal(rules.get(i).getLeft()))) {
                        result.addAll(new ArrayList<>());
                    }else {
                        result.addAll(nextTerminal(rules.get(i).getLeft())); 
                    }
                }else {
                    matcher = pattern.matcher(rules.get(i).getRigth()[j]);
                    while(matcher.find()) {
                        if(rules.get(i).getRigth()[j].substring(matcher.start(), matcher.end()).equals(X)) {
                            flag = 1;
                        } else if (flag == 1){
                            mstart = matcher.start();
                            mend = matcher.end();
                            flag = 0;
                        }
                    }
                    if(mstart != 0 && mend != 0) {
                        result.addAll(firstTerminal(rules.get(i).getRigth()[j].substring(mstart, mend)));
                    }
                }
 
            }
        }
        return result;
        
    }
    
    static String Read(File file) {
        
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
