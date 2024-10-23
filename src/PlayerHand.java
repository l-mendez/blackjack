import Cards.Card;

public class PlayerHand extends Hand{
    double bet;

    public PlayerHand(Card init, Double bet){
        super(init);
        this.bet = bet;
    }

    /* returns true if he can keep playing
     * returns false if he can't */
    @Override
    public boolean keepsPlaying(){
        return super.sum<21;
    }

    @Override
    public String toString(){
        return "Bet: $" + bet + " Sum: " + super.sum;
    }
}
