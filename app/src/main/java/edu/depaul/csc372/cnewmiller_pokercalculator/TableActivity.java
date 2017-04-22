/*
* Code by Clayton Newmiller
*
* */

package edu.depaul.csc372.cnewmiller_pokercalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class TableActivity extends AppCompatActivity {

    private final Card.CardSuit SUITS[] = Card.CardSuit.values();

    private final Card.CardVal VALUES[] = Card.CardVal.values();

    private Card DECK[] = Card.getDeck();
    private Card HAND[] = new Card[6];
    private Card condensedHand[]=null;
    Thread calculatorThread=null;
    Probabilities currentProbabilities=null;

    private ScaleGestureDetector detector;
    private Spinner suit;
    private Spinner value;
    private RadioGroup tableOfCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        this.detector = new ScaleGestureDetector(this, new tableListener());
        suit = (Spinner) findViewById(R.id.spinnerSuit);
        ArrayAdapter<Card.CardSuit> suitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SUITS);
        suitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suit.setAdapter(suitAdapter);

        value = (Spinner) findViewById(R.id.spinnerValue);
        ArrayAdapter<Card.CardVal> valueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, VALUES);
        valueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        value.setAdapter(valueAdapter);

        tableOfCards = (RadioGroup) findViewById(R.id.tableButtons);
        Button addToTableButton = (Button) findViewById(R.id.addButton);
        addToTableButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                RadioButton t = (RadioButton) findViewById(tableOfCards.getCheckedRadioButtonId());
                int index = tableOfCards.indexOfChild(t);
                Card selectedCard = new Card((Card.CardSuit)suit.getSelectedItem(), (Card.CardVal)value.getSelectedItem());

                if (selectedCard.isIn(DECK)){
                    if (HAND[index]!=null){
                        DECK = Card.addCard(DECK, HAND[index]);
                        HAND[index]=null;
                    }
                    HAND[index] = selectedCard;
                    DECK = Card.removeCard(DECK, HAND[index]);
                    condensedHand = Card.addCard(condensedHand, HAND[index]);
                    t.setText(HAND[index].toString());
                    //start thread here
                    currentProbabilities = new Probabilities(condensedHand, DECK);
                    calculatorThread = new Thread(currentProbabilities);
                    calculatorThread.start();
                }
            }
        });


        Button calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    if (calculatorThread!=null){
                        calculatorThread.join();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                Intent intent = new Intent(TableActivity.this, calculatorActivity.class);
                intent.putExtra("Probabilities", currentProbabilities);
                startActivity(intent);

            }
        });


    }
    private void recoverSavedTable(){
        resetTable();
        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        for (int i=0;i<this.HAND.length;i++){
            if (settings.getBoolean("hand_slot_present_"+i, false)){
                HAND[i]=new Card(
                        Card.CardSuit.values()[settings.getInt("current_hand_slot_suit_"+i, 0)],
                        Card.CardVal.values()[settings.getInt("current_hand_slot_value_"+i, 0)]);
                DECK = Card.removeCard(DECK, HAND[i]);
                condensedHand = Card.addCard(condensedHand, HAND[i]);
            }
            else{
                HAND[i]=null;
            }
        }
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton1)).setText(
                settings.getString(getString(R.string.settings_pocket_card_1), getString(R.string.pocket_card_1)));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton2)).setText(
                settings.getString(getString(R.string.settings_pocket_card_2), getString(R.string.pocket_card_2)));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton3)).setText(
                settings.getString(getString(R.string.settings_flop1), getString(R.string.flop)));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton4)).setText(
                settings.getString(getString(R.string.settings_flop2), getString(R.string.flop)));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton5)).setText(
                settings.getString(getString(R.string.settings_flop3), getString(R.string.flop)));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton6)).setText(
                settings.getString(getString(R.string.settings_river), getString(R.string.river)));
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences globalSettings = this.getSharedPreferences(
                getString(R.string.global_settings), Context.MODE_PRIVATE);
        boolean doLoad = globalSettings.getBoolean(getString(R.string.settings_loadTable), false);
        if (doLoad){
            recoverSavedTable();
        }
        currentProbabilities = new Probabilities(condensedHand, DECK);
        calculatorThread = new Thread(currentProbabilities);
        calculatorThread.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences globalSettings = this.getSharedPreferences(
                getString(R.string.global_settings), Context.MODE_PRIVATE);
        boolean doLoad = globalSettings.getBoolean(getString(R.string.settings_loadTable), false);
        if (!doLoad){
            resetTable();
        }

        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();

        for (int i=0;i<this.HAND.length;i++){
//            System.out.println("putting..."+i+": "+HAND[i]);
            if (HAND[i]!=null){
                settingsEditor.putBoolean("hand_slot_present_"+i, true);
                settingsEditor.putInt("current_hand_slot_suit_"+i, HAND[i].getSuit().ordinal());
                settingsEditor.putInt("current_hand_slot_value_"+i, HAND[i].getValue().ordinal());
            }
            else{
                settingsEditor.putBoolean("hand_slot_present_"+i, false);
            }
        }

        settingsEditor.putString(
                getString(R.string.settings_pocket_card_1),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton1)).getText().toString());
        settingsEditor.putString(getString(R.string.settings_pocket_card_2),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton2)).getText().toString());
        settingsEditor.putString(getString(R.string.settings_flop1),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton3)).getText().toString());
        settingsEditor.putString(getString(R.string.settings_flop2),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton4)).getText().toString());
        settingsEditor.putString(getString(R.string.settings_flop3),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton5)).getText().toString());
        settingsEditor.putString(getString(R.string.settings_river),
                ((RadioButton)tableOfCards.findViewById(R.id.cardButton6)).getText().toString());
        settingsEditor.commit();




    }

    private void resetTable(){
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton1)).setText(getString(R.string.pocket_card_1));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton2)).setText(getString(R.string.pocket_card_2));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton3)).setText(getString(R.string.flop));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton4)).setText(getString(R.string.flop));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton5)).setText(getString(R.string.flop));
        ((RadioButton)tableOfCards.findViewById(R.id.cardButton6)).setText(getString(R.string.river));

        DECK = Card.getDeck();
        HAND = new Card[6];
        condensedHand = null;
        calculatorThread = null;
        currentProbabilities = null;

    }

    public void onCardSlotChanged(View view){
        Spinner suit = (Spinner) findViewById(R.id.spinnerSuit);
        Spinner value = (Spinner) findViewById(R.id.spinnerValue);
        RadioGroup tableOfCards = (RadioGroup) findViewById(R.id.tableButtons);

        RadioButton pressed = (RadioButton) view;
        int index = tableOfCards.indexOfChild(pressed);
        Card taggedCard = this.HAND[index];

        if (taggedCard !=null){

            suit.setSelection(taggedCard.getSuit().ordinal(), true);
            value.setSelection(taggedCard.getValue().ordinal(), true);
        }

    }
    public boolean onTouchEvent(MotionEvent event){ this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private class tableListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale (ScaleGestureDetector detector){
            float scale = detector.getScaleFactor();
            if (scale>1) resetTable();

            return true;
        }

    }

}
