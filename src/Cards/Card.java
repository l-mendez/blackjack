package Cards;

import javafx.scene.image.Image;

public class Card {
   public Image image;
   public Rank rank;
   public Suit suit;

   public Card(Rank rank, Suit suit) {
       this.rank = rank;
       this.suit = suit;
       try {
       this.image = new Image(getClass().getResourceAsStream("/Cards/resources/PNG/%s%s.png".formatted(rank,suit)));
       } catch (NullPointerException e){
           this.image = new Image(getClass().getResourceAsStream("/Cards/resources/PNG/purple_back.png"));
       }
   }

   public int getValue(){
       return rank.getValue();
   }

   public Rank getRank(){
       return rank;
   }
}
