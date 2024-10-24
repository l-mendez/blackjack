import Cards.Card;
import Cards.Deck;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class Main extends Application {

    /* Backend */
    boolean hasInsurance = false;
    double balance = 500, bet = 5;
    Player player = new Player();
    Deck deck = new Deck();
    CrupierHand crupier = new CrupierHand(deck.dealCard());
    PauseTransition CrupierPause = new PauseTransition(Duration.seconds(3));

    /* Frontend */
    ArrayList<ImageView> CardArrayList = new ArrayList<>();
    double cardHeight = 115, cardWidth = 75;
    double buttonsHeight, buttonsWidth;
    Button HitButton = new Button("Hit");
    Button StandButton = new Button("Stand");
    Button InsuranceButton = new Button("Insurance");
    Button DoubleButton = new Button("Double");
    Button SplitButton = new Button("Split");
    Button RestartButton = new Button("Restart");
    Button ChangeBetButton = new Button("Change Bet");
    Button playButton = new Button("Play");

    HBox ButtonsHBox = new HBox(5, HitButton, StandButton, InsuranceButton, DoubleButton, SplitButton);
    HBox CrupierCardsHBox = new HBox();
    HBox OptionsHBox = new HBox(5, RestartButton, ChangeBetButton);

    ImageView coveredCard;
    ImageView Logo = new ImageView(new Image(Main.class.getResourceAsStream("logo.png")));

    Label crupierNameLabel = new Label("Crupier");
    Label playerNameLabel = new Label("Player");
    Label balanceLabel = new Label();
    Label crupierLabel = new Label();
    Label insuranceLabel = new Label("Insurance");
    Label valueLabel = new Label("Bet: $" + bet);
    Label LoseLabel = new Label("You lose!");

    Slider slider = new Slider();

    VBox PlayerHandsBox = new VBox();
    VBox MainMenu = new VBox(10, valueLabel, slider, playButton);
    VBox LogoLayout = new VBox(Logo);
    VBox layout = new VBox(10, balanceLabel, insuranceLabel, crupierNameLabel, CrupierCardsHBox, playerNameLabel, PlayerHandsBox,ButtonsHBox, OptionsHBox);

    /* Structure */
    Scene MainScene = new Scene(layout, 700, 700);
    Scene MenuScene  = new Scene(MainMenu, 700, 700);;
    Scene LogoScene = new Scene(LogoLayout, 700, 700);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
        MainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        MenuScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        MainMenu.setAlignment(Pos.CENTER);
        primaryStage.setScene(LogoScene);
        primaryStage.setResizable(true);

        // The window is always square
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double newWidth = newVal.doubleValue();
            //layout.setPrefWidth(newWidth);
            primaryStage.setHeight(newWidth);
            double ratio = newVal.doubleValue()/oldVal.doubleValue();
            cardHeight*=ratio;
            cardWidth*=ratio;
            for (ImageView iv : CardArrayList){
                iv.setFitHeight(cardHeight);
                iv.setFitWidth(cardWidth);
            }

        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double newHeight = newVal.doubleValue();
            primaryStage.setWidth(newHeight);
            double ratio = newHeight/oldVal.doubleValue();
            cardHeight*=ratio;
            cardWidth*=ratio;
            resize();
        });

        Logo.setFitHeight(LogoScene.getHeight());
        Logo.setFitWidth(LogoScene.getWidth());
        Logo.preserveRatioProperty();
        primaryStage.setTitle("Blackjack by Lolo");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        pause.setOnFinished(event -> {
            primaryStage.setScene(MenuScene);
            layout.setAlignment(Pos.CENTER);
        });

        pause.play();

        slider.setMin(0);     // Minimum value
        slider.setMax(balance);   // Maximum value
        slider.setValue(bet);  // Initial value
        slider.setMajorTickUnit(10);    // Major tick unit
        slider.setMinorTickCount(5);    // Minor tick count
        slider.setBlockIncrement(1);
        slider.setLayoutY(primaryStage.getHeight()/2);
        slider.setMaxWidth(400);

        playButton.setOnAction(event -> {
            primaryStage.setScene(MainScene);
            bet=(int)slider.getValue();
            balance -= bet;
            layout.getChildren().addAll(slider, valueLabel);
            slider.setVisible(false);
            valueLabel.setVisible(false);
            player.newHand(deck.dealCard(),bet);
            player.hands.get(player.currentHand).add(deck.dealCard());
            updateUI();
            if (player.hands.get(player.currentHand).blackjack){
                endGame();
            }
        });

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueLabel.setText("Bet: $" + newValue.intValue());
        });

        LoseLabel.getStyleClass().add("curved-box");
        valueLabel.getStyleClass().add("curved-box");
        crupierLabel.getStyleClass().add("curved-box");
        balanceLabel.getStyleClass().add("curved-box");
        balanceLabel.setAlignment(Pos.TOP_LEFT);
        crupierNameLabel.getStyleClass().add("curved-box");
        crupierNameLabel.setAlignment(Pos.CENTER);
        playerNameLabel.getStyleClass().add("curved-box");
        playerNameLabel.setAlignment(Pos.CENTER);
        CrupierCardsHBox.setAlignment(Pos.CENTER);
        PlayerHandsBox.setAlignment(Pos.CENTER);
        ButtonsHBox.setAlignment(Pos.BASELINE_CENTER);
        OptionsHBox.setVisible(false);
        OptionsHBox.setAlignment(Pos.BASELINE_CENTER);
        CrupierCardsHBox.getChildren().clear();
        for (Card card : crupier.cards){
            ImageView newCard = new ImageView(card.image);
            newCard.setFitHeight(cardHeight);
            newCard.setFitWidth(cardWidth);
            CardArrayList.add(newCard);
            CrupierCardsHBox.getChildren().add(newCard);
        }
        coveredCard = new ImageView(new Image(getClass().getResourceAsStream("/Cards/resources/PNG/purple_back.png")));
        CardArrayList.add(coveredCard);
        coveredCard.setFitHeight(cardHeight);
        coveredCard.setFitWidth(cardWidth);
        CrupierCardsHBox.getChildren().add(1, coveredCard);
        CrupierCardsHBox.getChildren().add(2, crupierLabel);


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
            CardArrayList = new ArrayList<>();
            if (!OptionsHBox.getChildren().contains(ChangeBetButton)) OptionsHBox.getChildren().add(ChangeBetButton);
            hasInsurance=false;
            bet =(int)slider.getValue();
            slider.setVisible(false);
            valueLabel.setVisible(false);
            player = new Player();
            deck = new Deck();
            player.newHand(deck.dealCard(),bet);
            player.hands.get(player.currentHand).add(deck.dealCard());
            crupier = new CrupierHand(deck.dealCard());
            balance -= bet;
            CrupierCardsHBox.getChildren().clear();
            for (Card card : crupier.cards){
                ImageView newCard = new ImageView(card.image);
                newCard.setFitHeight(cardHeight);
                newCard.setFitWidth(cardWidth);
                CardArrayList.add(newCard);
                CrupierCardsHBox.getChildren().add(newCard);
            }
            coveredCard = new ImageView(new Image(getClass().getResourceAsStream("/Cards/resources/PNG/purple_back.png")));
            coveredCard.setFitHeight(cardHeight);
            coveredCard.setFitWidth(cardWidth);
            CardArrayList.add(coveredCard);
            CrupierCardsHBox.getChildren().add(coveredCard);
            CrupierCardsHBox.getChildren().add(crupierLabel);
            if (player.hands.get(player.currentHand).blackjack){
                updateUI();
                endGame();
            }
            else{
                OptionsHBox.setVisible(false);
                ChangeBetButton.setVisible(true);
                updateUI();
            }
        });

        ChangeBetButton.setOnAction(e -> {
            slider.setVisible(true);
            valueLabel.setVisible(true);
            slider.setMax(balance);
            slider.setValue(bet);
            OptionsHBox.getChildren().remove(ChangeBetButton);
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
               newCard.setFitHeight(cardHeight);
               newCard.setFitWidth(cardWidth);
               hBox.getChildren().add(newCard);
               CardArrayList.add(newCard);
            }
            Label sumLabel = new Label(pH.sum+ (pH.aceAmount>0? "/%d".formatted(pH.sum-10) : ""));
            sumLabel.getStyleClass().add("curved-box");
            hBox.getChildren().add(sumLabel);
            hBox.setAlignment(Pos.CENTER);
            PlayerHandsBox.getChildren().add(hBox);
        }

        insuranceLabel.setVisible(hasInsurance);

        // Update the player's balance
        balanceLabel.setText("Balance: $" + (balance>=0 ? (int)balance : 0));

        // Update the crupier's sum
        crupierLabel.setText(""+crupier.sum);

        // Check if the player can keep hitting
        if (player.hands.get(player.currentHand).keepsPlaying()){
            HitButton.setDisable(false);
        }

        // Check for insurance
        if (crupier.sum==11 && !hasInsurance && balance >= 2*bet){
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
        resize();
    }

    public void endGame(){
        StandButton.setDisable(true);
        InsuranceButton.setDisable(true);
        DoubleButton.setDisable(true);
        SplitButton.setDisable(true);
        HitButton.setDisable(true);

        CrupierCardsHBox.getChildren().remove(coveredCard);
        int i = 1;

        while (crupier.keepsPlaying()){
            CrupierPause.play();
            Card newCard = deck.dealCard();
            crupier.add(newCard);
            ImageView newImageCard = new ImageView(newCard.image);
            resize();
//            newImageCard.setFitHeight(2 * cardHeight/(player.hands.size()+1));
//            newImageCard.setFitWidth(2 * cardWidth/(player.hands.size()+1));
            CardArrayList.add(newImageCard);
            CrupierCardsHBox.getChildren().add(i++, newImageCard);
            crupierLabel.setText("" + crupier.sum);
        }
        resize();
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
        }
        if (hasInsurance && crupier.blackjack){
            balance += bet;
        }
        balanceLabel.setText("Balance: $" + (balance>=0 ? (int)balance : 0));
        if (balance<=0){
            layout.getChildren().add(LoseLabel);
        }
        else {
            OptionsHBox.setVisible(true);
        }
    }

    public void resize(){
        int maxCardAmount = 1;
        for (PlayerHand pH : player.hands){
            maxCardAmount = Math.max(maxCardAmount, pH.amountCards);
        }
        maxCardAmount = Math.max(maxCardAmount, crupier.amountCards);

        double newWidth = Math.min  (2 * cardWidth/(player.hands.size()+1),(layout.getWidth()-100)/maxCardAmount);
        System.out.println((layout.getWidth()-15)/(maxCardAmount));
        for (ImageView iv : CardArrayList){
            iv.setFitHeight(newWidth * 1.53);
            iv.setFitWidth(newWidth);
        }
    }

// VBox layout = new VBox(10, balanceLabel, insuranceLabel, crupierNameLabel, CrupierCardsHBox, playerNameLabel, PlayerHandsBox,ButtonsHBox, OptionsHBox);
    public static void main(String[] args) {launch(args);}
}
