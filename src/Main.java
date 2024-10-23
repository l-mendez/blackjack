import Cards.Card;
import Cards.Deck;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {

    /* Structure */
    Scene scene;
    VBox layout;

    /* Backend */
    boolean hasInsurance = false;
    double balance = 500, bet = 5;
    Player player = new Player();
    Deck deck = new Deck();
    CrupierHand crupier = new CrupierHand(deck.dealCard());

    /* Frontend */
    Label balanceLabel = new Label();
    Label crupierLabel = new Label();
    Label insuranceLabel = new Label("Insurance");
    HBox playerHandsHBox = new HBox();
    Button HitButton = new Button("Hit");
    Button StandButton = new Button("Stand");
    Button InsuranceButton = new Button("Insurance");
    Button DoubleButton = new Button("Double");
    Button SplitButton = new Button("Split");
    HBox ButtonsHBox = new HBox(5, HitButton, StandButton, InsuranceButton, DoubleButton, SplitButton);
    VBox resultVBox = new VBox();
    Button RestartButton = new Button("Restart");
    HBox CrupierCardsHBox = new HBox();
    VBox PlayerHandsBox = new VBox();
    ImageView coveredCard;
    ImageView Logo = new ImageView(new Image(Main.class.getResourceAsStream("logo.png")));

    @Override
    public void start(Stage primaryStage) {
        layout = new VBox(Logo);
        scene = new Scene(layout, 700, 700);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            primaryStage.setHeight(newHeight);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            primaryStage.setWidth(newWidth);
        });


        Logo.setFitHeight(scene.getHeight());
        Logo.setFitWidth(scene.getWidth());
        Logo.preserveRatioProperty();
        primaryStage.setTitle("Blackjack");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
            layout = new VBox(balanceLabel,insuranceLabel, playerHandsHBox, crupierLabel,CrupierCardsHBox, PlayerHandsBox,ButtonsHBox, resultVBox, RestartButton);
            scene.setRoot(layout);
        });

        pause.play();

        player.newHand(deck.dealCard(),bet);
        player.hands.get(player.currentHand).add(deck.dealCard());
        balance -= bet;
        RestartButton.setVisible(false);
        CrupierCardsHBox.getChildren().clear();
        for (Card card : crupier.cards){
            ImageView newCard = new ImageView(card.image);
            newCard.setFitHeight(153);
            newCard.setFitWidth(100);
            CrupierCardsHBox.getChildren().add(newCard);
        }
        coveredCard = new ImageView(new Image(getClass().getResourceAsStream("/Cards/resources/PNG/purple_back.png")));
        coveredCard.setFitHeight(153);
        coveredCard.setFitWidth(100);
        CrupierCardsHBox.getChildren().add(coveredCard);
        if (player.hands.get(player.currentHand).blackjack){
            endGame();
        }
        else updateUI();

        HitButton.setOnAction(e -> {
           player.hands.get(player.currentHand).add(deck.dealCard());
           updateUI();
           if (!player.hands.get(player.currentHand).keepsPlaying()){
               player.currentHand++;
               if (player.currentHand >= player.hands.size()) {
                   endGame();
               }
               else updateUI();
           }
        });

        StandButton.setOnAction(e -> {
            player.currentHand++;
            if (player.currentHand >= player.hands.size()) {
                endGame();
            }
            updateUI();
        });

        DoubleButton.setOnAction(e -> {
            balance -= bet;
            // Double the bet
            player.doubl();
            // Get a card
            player.hands.get(player.currentHand).add(deck.dealCard());
            updateUI();
            // Go to the next hand
            player.currentHand++;
            if (player.currentHand >= player.hands.size()) {
                endGame();
            }
        });

        StandButton.setOnAction(e -> {
            player.currentHand++;
            if (player.currentHand >= player.hands.size()) {
                endGame();
            }
        });

        SplitButton.setOnAction(e -> {
            balance -= bet;
            player.split();
            player.hands.get(player.currentHand).add(deck.dealCard());
            player.hands.get(player.currentHand+1).add(deck.dealCard());

            if (!player.hands.get(player.currentHand).keepsPlaying()){
                player.currentHand++;
            }
            updateUI();
            if (!player.hands.get(player.currentHand).keepsPlaying()){
                player.currentHand++;
            }
            if (player.currentHand >= player.hands.size()) {
                endGame();
            }
            else updateUI();
        });

        RestartButton.setOnAction(e -> {
            player = new Player();
            deck = new Deck();
            player.newHand(deck.dealCard(),bet);
            player.hands.get(player.currentHand).add(deck.dealCard());
            crupier = new CrupierHand(deck.dealCard());
            resultVBox.getChildren().clear();
            balance -= bet;
            CrupierCardsHBox.getChildren().clear();
            for (Card card : crupier.cards){
                ImageView newCard = new ImageView(card.image);
                newCard.setFitHeight(153);
                newCard.setFitWidth(100);
                CrupierCardsHBox.getChildren().add(newCard);
            }
            coveredCard = new ImageView(new Image(getClass().getResourceAsStream("/Cards/resources/PNG/purple_back.png")));
            coveredCard.setFitHeight(153);
            coveredCard.setFitWidth(100);
            CrupierCardsHBox.getChildren().add(coveredCard);
            updateUI();
            RestartButton.setVisible(false);
        });

        InsuranceButton.setOnAction(e -> {
            hasInsurance = true;
            balance -= bet * 0.5;
            updateUI();
        });


    }

    public void updateUI(){
        /* Disable all buttons */
        InsuranceButton.setDisable(true);
        DoubleButton.setDisable(true);
        SplitButton.setDisable(true);
        HitButton.setDisable(true);
        StandButton.setDisable(false);


        PlayerHandsBox.getChildren().clear();
        for (PlayerHand pH : player.hands){
            HBox hBox = new HBox();
            for (Card card : pH.cards){
               ImageView newCard = new ImageView(card.image);
               newCard.setFitHeight(153);
               newCard.setFitWidth(100);
               hBox.getChildren().add(newCard);
            }
            PlayerHandsBox.getChildren().add(hBox);
        }

        insuranceLabel.setVisible(hasInsurance);

        // Update the player's balance
        balanceLabel.setText("Balance: " + balance);

        // Update the crupier's sum
        crupierLabel.setText("Crupier Sum: " + crupier.sum);

        // Clear the player's hands and replace them with updated ones
        playerHandsHBox.getChildren().clear();
        for (PlayerHand pH : player.hands){
            playerHandsHBox.getChildren().add(new Label(pH.toString()));
        }

        // Check if the player can keep hitting
        if (player.hands.get(player.currentHand).keepsPlaying()){
            HitButton.setDisable(false);
        }

        // Check for insurance
        if (crupier.sum==11 && !hasInsurance){
            InsuranceButton.setDisable(false);
        }

        // Check for double
        if (player.hands.get(player.currentHand).amountCards==2 && bet*2<=balance){
            DoubleButton.setDisable(false);
            // Check for split
            ArrayList<Card> CurrentCards = player.hands.get(player.currentHand).cards;
            if (CurrentCards.get(0).getRank().equals(CurrentCards.get(1).getRank())){
                SplitButton.setDisable(false);
            }
        }
    }

    public void endGame(){
        StandButton.setDisable(true);
        InsuranceButton.setDisable(true);
        DoubleButton.setDisable(true);
        SplitButton.setDisable(true);
        HitButton.setDisable(true);

        CrupierCardsHBox.getChildren().remove(1);
        while (crupier.keepsPlaying()){
            Card newCard = deck.dealCard();
            crupier.add(newCard);
            ImageView newImageCard = new ImageView(newCard.image);
            newImageCard.setFitHeight(153);
            newImageCard.setFitWidth(100);
            CrupierCardsHBox.getChildren().add(newImageCard);
            crupierLabel.setText("Crupier Sum: " + crupier.sum);
        }

        for (PlayerHand pH : player.hands){
            String resultText;
            if (pH.blackjack && !crupier.blackjack){
                resultText = "Blackjack";
                balance += pH.bet*2.5;
            }
            else if ((crupier.blackjack && !pH.blackjack) || pH.sum>21 || (crupier.sum > pH.sum && crupier.sum<=21)){
                resultText = "Lost";
            }
            else if (pH.sum == crupier.sum){
                resultText = "Draw";
                balance+=pH.bet;
            }
            else{
                resultText = "Win";
                balance += 2 * pH.bet;
            }
            resultVBox.getChildren().add(new Label(pH + " " + resultText));
        }
        if (hasInsurance && crupier.blackjack){
            balance += bet;
        }
        balanceLabel.setText("Balance: " + balance);
        RestartButton.setVisible(true);
    }

    public static void main(String[] args) {launch(args);}
}

