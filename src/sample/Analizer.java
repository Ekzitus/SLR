package sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analizer {
    List<Production> productions = new ArrayList<>();
    Map<Key, FSM> fsm = new HashMap<>();
    FSM state;
    int reduceCount;
    String simvoli = "";
    String _START_ = "";
    Pattern patternInput;
    Matcher matcherInput;
    String term;
    boolean accepted;
    Stack<String> stack;
    Symbols symbols = new Symbols();

    Analizer(String[] args){
        String filename = args[0];							// Имя файла с грамматикой заданной формате БНФ
        String grammar = Read(filename);
        genSymbols(grammar);
        buildFSM();
        patternInput = Pattern.compile(symbols.getSymbols());
        matcherInput = patternInput.matcher(args[1]);
        term = "";
        if(matcherInput.find()) term = args[1].substring(matcherInput.start(), matcherInput.end());
        accepted = false;
        stack = new Stack<>();
        stack.push("0");
    }

    boolean step(String str){
        reduceCount = 0;
        String[] s = new String[]{stack.peek(), term};
        state = fsm.get(new Key(s));
        switch (state.operation) {
            case "Success" : return true;
            case "Shift" : stack.push(Integer.toString(state.index));
                simvoli += term;
                term = matcherInput.find() ? str.substring(matcherInput.start(), matcherInput.end()) : "";
                break;
            case "Reduce" : Pattern patternReduce = Pattern.compile(symbols.getSymbols());
                Matcher matcherReduce = patternReduce.matcher(state.production.getRigth());
                while (matcherReduce.find()) {
                    reduceCount++;
                    simvoli = simvoli.substring(0, simvoli.length() - (matcherReduce.end() - matcherReduce.start()));
                    stack.pop();
                }
                s = new String[]{stack.peek(), state.production.getLeft()};
                simvoli += state.production.getLeft();
                stack.push(Integer.toString(fsm.get(new Key(s)).index));
                break;
            default : System.out.println("Error");
        }
        return false;
    }

    void genSymbols(String grammar) {
        /*
         * Регулярное выражение
         * Для захвата левой части правила
         * и присваивание этой группе имя left
         * Для захвата правой части правила
         * и присваивания этой группе имя right
         */
        String regexRules = "(?<left>.+):=(?<right>.+)";
        Pattern patternRules = Pattern.compile(regexRules);
        Matcher matcherRules = patternRules.matcher(grammar);
        int j = 0;
        boolean c = true;
        while(matcherRules.find()) {
            if(c) {
                _START_ = matcherRules.group("left");
                c = false;
            }
            symbols.addNoTerminals(matcherRules.group("left"));                          // Помещяет
            symbols.addSymbols(matcherRules.group("left") + "|");
            String regex_right = "\\|";                                     // Разделитель правой части правила
            Pattern pattern_right = Pattern.compile(regex_right);
            String[] strings = pattern_right.split(matcherRules.group("right"));  		// Разделяет правую часть правила по заданному разделителю
            for(String s: strings) {
                productions.add(j, new Production(matcherRules.group("left"), s));
                j++;
            }
        }

        for (Production production : productions) {
            Pattern patternTerminal = Pattern.compile("[^" + symbols.getNoTerminals() + "]");
            Matcher matcherTerminal = patternTerminal.matcher(production.getRigth());
            while (matcherTerminal.find()) {
                String ter = production.getRigth().substring(matcherTerminal.start(), matcherTerminal.end());// Найденный терминал
                symbols.addTerminals(ter);
                if(ter.equals("+") | ter.equals("(") | ter.equals(")") | ter.equals("*"))
                    symbols.addSymbols("\\" + ter + "|");
                else
                    symbols.addSymbols(ter + "|");
            }
        }
    }

    ArrayList<String> firstTerminal(String X) {

        boolean flag = false;
        for(int i = 0; i < symbols.getTerminals().size(); i++) {
            if (X.equals(symbols.getTerminals().get(i))) {
                flag = true;
                break;
            }
        }

        if(flag) {
            ArrayList<String> result = new ArrayList<>();
            result.add(X);
            return result;
        }

        ArrayList<String> result = new ArrayList<>();
        for (Production production : productions) {
            if (production.getLeft().equals(X)) {
                Pattern pattern = Pattern.compile(symbols.getSymbols());
                Matcher matcher = pattern.matcher(production.getRigth());
                if(matcher.find())
                    if (!production.getRigth().substring(matcher.start(), matcher.end()).equals(X)) {
                        result.addAll(firstTerminal(production.getRigth().substring(matcher.start(), matcher.end())));
                    }
            }
        }
        return result;
    }

    void buildFSM() {
        List<Item> itemStart = new ArrayList<>(1);
        itemStart.add(new Item(productions.get(0), 0, symbols));

        List<List<Item>> states = new ArrayList<>();
        states.add(closeItem(itemStart));

        for(int i = 0; i < states.size(); i++) {
            addReducing(states.get(i), i);
            for(String x: symbols.getSymbolsList()) {
                List<Item> shiftStateReturn = shiftState(states.get(i), x);
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
                    String[] s = {Integer.toString(i), x};
                    Key key = new Key(s);
                    if(flag)
                    {
                        states.add(shiftStateReturn);
                        fsm.put(key, new FSM(i, x));
                        fsm.get(key).setShift(states.size()-1);
                    }else {
                        fsm.put(key, new FSM(i, x));
                        fsm.get(key).setShift(indexBack);
                    }
                }
            }
        }

    }

    ArrayList<String> nextTerminal(String X){
        ArrayList<String> result = new ArrayList<>();
        for (Production production : productions) {
            int flag = 0;
            int mstart = 0;
            int mend = 0;
            Pattern pattern = Pattern.compile(symbols.getSymbols());
            Matcher matcher = pattern.matcher(production.getRigth());
            String s = null;
            while (matcher.find()) {
                s = production.getRigth().substring(matcher.start(), matcher.end());
            }
            assert s != null;
            if (Objects.equals(s, X)) {
                if (result.equals(nextTerminal(production.getLeft()))) {
                    result.addAll(new ArrayList<>());
                } else {
                    result.addAll(nextTerminal(production.getLeft()));
                }
            } else {
                matcher = pattern.matcher(production.getRigth());
                while (matcher.find()) {
                    if (production.getRigth().substring(matcher.start(), matcher.end()).equals(X)) {
                        flag = 1;
                    } else if (flag == 1) {
                        mstart = matcher.start();
                        mend = matcher.end();
                        flag = 0;
                    }
                }
                if (mstart != 0 && mend != 0) {
                    result.addAll(firstTerminal(production.getRigth().substring(mstart, mend)));
                }
            }
        }
        return result;

    }

    List<Item> closeItem(List<Item> i){
        for(int k = 0; k < i.size(); k++) {
            if(i.get(k).getMarkeredType().equals("NoTerminal")) {
                for(Production production: productions) {
                    if(production.getLeft().equals(i.get(k).getMarkered())) {
                        boolean flag = true;
                        for (Item item : i) {
                            if (item.getProduction().getLeft().equals(production.getLeft()) &&
                                    item.getProduction().getRigth().equals(production.getRigth()) &&
                                    item.getMarker() == 0) {
                                flag = false;
                                break;
                            }
                        }
                        if(flag) {
                            i.add(new Item(production, 0, symbols));
                        }
                    }
                }
            }
        }

        return i;
    }

    void addReducing(List<Item> i, int index) {
        for(Item item: i) {
            if(item.getMarkered().equals("")) {
                if(item.getProduction().getLeft().equals(_START_)) {			// Видим стартовый символ
                    String[] s = {Integer.toString(index), ""};
                    Key key = new Key(s);
                    fsm.put(key, new FSM(index, ""));	                    // Записываем событие "УСПЕХ"
                    fsm.get(key).setSuccess();			                        // для поступаемой на вход пустой строки
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

    List<Item> shiftState(List<Item> i, String x){
        List<Item> result = new ArrayList<>();
        for(Item item: i) {
            if(item.getMarkered().equals(x)) {
                result.add(new Item(item.getProduction(), item.getMarker() + 1, symbols));
            }
        }

        return closeItem(result);
    }

    String Read(String filename) {
        File file = new File(filename);
        try (FileInputStream reader = new FileInputStream(file)){
            byte[] bbuf = new byte[(int)file.length()];
            reader.read(bbuf);
            String data = new String(bbuf);
            reader.close();
            return data;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
