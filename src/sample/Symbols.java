package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Symbols {
    String symbols = "";
    ArrayList<String> terminals = new ArrayList<>();
    ArrayList<String> noTerminals = new ArrayList<>();

    void addSymbols(String symbols) {
        this.symbols += symbols;
    }

    String getSymbols() {
        if (!symbols.endsWith("|")) return symbols;
        return symbols.substring(0, symbols.length() - 1);
    }

    List<String> getSymbolsList() {
        var array = new ArrayList<String>(noTerminals);
        array.addAll(terminals);
        return array;
    }

    void addTerminals(String terminal) {
        terminals.add(terminal);
    }

    List<String> getTerminals() {
        return terminals;
    }

    void addNoTerminals(String noTerminal) {
        noTerminals.add(noTerminal);
    }

    String getNoTerminals() {
        return Arrays.toString(noTerminals.toArray());
    }
}
