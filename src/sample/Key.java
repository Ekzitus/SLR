package sample;

import java.util.Arrays;

public record Key(String[] values) {

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

    @Override
    public String toString() {
        return "Key[" +
                "values=" + values + ']';
    }

}
