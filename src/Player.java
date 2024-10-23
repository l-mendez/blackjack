import Cards.Card;

import java.util.ArrayList;

public class Player {
    ArrayList<PlayerHand> hands = new ArrayList<>();
    int currentHand = 0;

    public void newHand(Card init, Double bet){
        hands.add(new PlayerHand(init, bet));
    }

    public void split(){
        Card firstCard = hands.get(currentHand).cards.get(0);
        Card secondCard = hands.get(currentHand).cards.get(1);
        double bet = hands.get(currentHand).bet;
        hands.remove(currentHand);
        newHand(firstCard, bet);
        newHand(secondCard, bet);
    }

    public void doubl(){
        hands.get(currentHand).bet*=2;
    }
}
