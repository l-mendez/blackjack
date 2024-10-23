import Cards.Card;

import java.util.ArrayList;

public abstract class Hand {
    ArrayList<Card> cards = new ArrayList<>();
    protected int sum;
    protected int amountCards = 0;
    boolean blackjack = false;
    int aceAmount = 0;

    public Hand (Card init){
        add(init);
    }

    abstract public boolean keepsPlaying();

    /* Adds @card to @hand */
    public void add(Card card){
        cards.add(card);
        sum += card.getValue();
        amountCards++;
        blackjack = sum == 21 && amountCards == 2;
        if (card.getValue()==11){
            aceAmount++;
        }
        if (sum>21 && aceAmount>0){
            sum-=10;
            aceAmount--;
        }
    }
}
