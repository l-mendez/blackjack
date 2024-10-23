package Cards;

public enum Suit {
    SPADE("S"), HEART("H"), CLUB("C"), DIAMOND("D");

    Suit (String firstChar){
        this.firstChar = firstChar;
    }

    public final String firstChar;

    @Override
    public String toString() {
        return firstChar;
    }
}
