package sample;

import java.util.Arrays;

public final class Key {
    private final String[] values;

    public Key(String[] values) {
        this.values = values;
    }

    public String[] values() {
        return values;
    }

    @Override
    public String toString() {
        return "Key[" +
                "values=" + values + ']';
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }
        if (another == null) {
            return false;
        }
        if (another.getClass() != this.getClass()) {
            return false;
        }
        Key key = (Key) another;
        return Arrays.equals(this.values, key.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

}
