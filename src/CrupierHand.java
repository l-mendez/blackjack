import Cards.Card;

public class CrupierHand extends Hand {

    public CrupierHand(Card init){
        super(init);
    }
    /* returns true if he has to keep playing
     * returns false if he can't */
    @Override
    public boolean keepsPlaying(){
        return super.sum<17;
    }
}
