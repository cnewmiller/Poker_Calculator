package edu.depaul.csc372.cnewmiller_pokercalculator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Clayton Newmiller on 11/5/16.
 */

public class Card{

    public CardVal getValue() {
        return value;
    }

    public CardSuit getSuit() {
        return suit;
    }

    enum CardVal {Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace}
    enum CardSuit {Spades, Hearts, Diamonds, Clubs}

    private CardVal value;
    private CardSuit suit;

    public boolean equals(Card b){
        return ((this.getSuit()==b.getSuit()) && (this.getValue() == b.getValue()) );
    }

    public Card(CardSuit suit, CardVal value){
        this.value = value;
        this.suit = suit;
    }

    public static Card[] getDeck(){
        Card deck[] = new Card[52];
        int counter = 0;
        for (CardSuit suit : Card.CardSuit.values()){
            for (CardVal val : Card.CardVal.values()){
                deck[counter++] = new Card(suit, val);
            }
        }
        return deck;
    }
    public static Card[] removeCard(Card[] deck, Card target){
        ArrayList<Card> newDeck = new ArrayList<>();

        for (Card c : deck){
            if (!c.equals(target)){
                newDeck.add(c);
            }

        }
        Card ret[] = new Card[newDeck.toArray().length];
        for (int i = 0 ; i<ret.length ; i++){
            ret[i]=newDeck.get(i);
        }

        return ret;
    }

    public static Card[] addCard(Card[] deck, Card target) {
        if (deck==null){
            Card newDeck[] = {target};
            return newDeck;
        }

        ArrayList<Card> newDeck = new ArrayList<>();
        for (int i = 0; i < deck.length; i++) {
            newDeck.add(deck[i]);
        }
        newDeck.add(target);
        Card ret[] = new Card[newDeck.toArray().length];
        for (int i = 0 ; i<ret.length ; i++){
            ret[i]=newDeck.get(i);
        }

        return ret;
    }

    public boolean isIn(Card deck[]){
        for (Card c : deck){
            if (c.equals(this)) return true;
        }
        return false;
    }

    public String toString(){
        return (this.value+" of "+this.suit);
    }


    public static int findVals(Card[] deck, CardVal targetValue){//returns how many of a value are left
        int count=0;
        for (int i=0;i<deck.length;i++){
            if (deck[i].getValue()==targetValue)
                count++;
        }
        return count;
    }

    public static int findSuits(Card[] deck, CardSuit targetSuit) {//returns how many of a suit are left in the deck
        int count = 0;
        for (int i = 0; i < deck.length; i++) {
            if (deck[i].getSuit() == targetSuit)
                count++;
        }
        return count;
    }

}
