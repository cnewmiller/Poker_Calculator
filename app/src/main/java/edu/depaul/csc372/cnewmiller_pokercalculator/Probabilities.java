package edu.depaul.csc372.cnewmiller_pokercalculator;

import android.os.Parcel;
import android.os.Parcelable;
import java.math.BigInteger;

/**
 * Created by Clay on 11/7/16.
 */

public class Probabilities implements Runnable, Parcelable{

    public Probabilities(Card hand[], Card deck[]){
        this.hand = hand;
        this.deck = deck;
    }

    private Card deck[];
    private Card hand[];
    private double pairProbability;
    private double tripleProbability;
    private double quadProbability;
    private double flushProbability;
    private double fullHouseProbability;
    private double straightProbability;



    @Override
    public void run() {
        if (this.deck==null || this.hand==null)
            return;

        this.calculatePairProbability();
        this.calculateTripleProbability();
        this.calculateQuadProbability();
        this.calculateFlushProbability();
        this.calculateFullHouseProbability();
        this.calculateStraightProbability();
        //add new statements here, in getAll, and IN PARCEL CONSTRUCTOR

    }

    public double[] getAllProbabilities(){
        double allProbabilities[] = new double[6];
        allProbabilities[0] = this.pairProbability;
        allProbabilities[1] = this.tripleProbability;
        allProbabilities[2] = this.quadProbability;
        allProbabilities[3] = this.flushProbability;
        allProbabilities[4] = this.fullHouseProbability;
        allProbabilities[5] = this.straightProbability;
        return allProbabilities;
    }

    //pairs never change depending on what you have showing
    private double precalculatedPairs[] = {0.285234093637455, 0.25255102040816324, 0.21493703864524533,
            0.1717160037002775, 0.12210915818686402,0.06521739130434782};
    public void calculatePairProbability(){

        for (int i=0;i<hand.length;i++){ //for each card showing...
            if (Card.findVals(hand, hand[i].getValue())==2){
                this.pairProbability=1;
                break;
            }
            else{
                pairProbability = precalculatedPairs[i];
            }

        }

    }


    private void calculateTripleProbability(){
        double slotProbability[] = new double[5];

        for (int i=0;i<5;i++){ //for each card showing...
            if (i<hand.length && Card.findVals(hand, hand[i].getValue())==3){
                this.tripleProbability=1;
                break;
            }
            else if (i<hand.length && Card.findVals(hand, hand[i].getValue())==2){

                int remaining = (i<hand.length ? Card.findVals(deck, hand[i].getValue()) : 3), chancesLeft = 7-hand.length;
//                System.out.println("triple not found, pair found, remaining is "+remaining+", i is "+i+", hand length is "+hand.length);

                slotProbability[i] = hypergeoDist(remaining, deck.length, chancesLeft, 1);
            }
            else{
//                System.out.println("triple not found");
                int remaining = (i<hand.length ? Card.findVals(deck, hand[i].getValue()) : 2), chancesLeft = 7-hand.length;
                slotProbability[i] = hypergeoDist(remaining, deck.length, chancesLeft, 2);
            }

        }
        for (int i=0;i<slotProbability.length;i++){
//            System.out.println("slot "+i+" is "+slotProbability[i]);
            this.tripleProbability += slotProbability[i];
        }
        double subtractor=1;
        for (int i=0;i<slotProbability.length;i++){

            subtractor *= slotProbability[i];
//            System.out.println("subtractor at "+i+" is "+subtractor);
        }

        this.tripleProbability -= subtractor;
//        System.out.println("\nFinal probability: "+tripleProbability);
        //0.3590425532-(0.1196808510638298^4)=0.3573283038
    }

    private void calculateQuadProbability(){
        double slotProbability[] = new double[4];

        for (int i=0;i<4;i++){ //for each card showing...
            if (i<hand.length && Card.findVals(hand, hand[i].getValue())==4){ //found quad
                this.quadProbability=1;
                break;
            }
            else if ((hand.length>3 && this.tripleProbability!=1) || (hand.length>4 && this.pairProbability!=1)){
                this.quadProbability=0;
                break;
            }
            else if (i<hand.length && Card.findVals(hand, hand[i].getValue())==3){ //no quad, found triple
                int remaining = (i<hand.length ? Card.findVals(deck, hand[i].getValue()) : 1), chancesLeft = 7-hand.length;
                slotProbability[i] = hypergeoDist(remaining, deck.length, chancesLeft, 1);
            }
            else if (i<hand.length && Card.findVals(hand, hand[i].getValue())==2){ //no quad, found pair
                int remaining = (i<hand.length ? Card.findVals(deck, hand[i].getValue()) : 2), chancesLeft = 7-hand.length;
                slotProbability[i] = hypergeoDist(remaining, deck.length, chancesLeft, 2);
            }
            else{
                int remaining = (i<hand.length ? Card.findVals(deck, hand[i].getValue()) : 3), chancesLeft = 7-hand.length;
                slotProbability[i] = hypergeoDist(remaining, deck.length, chancesLeft, 3);
            }

        }
        for (int i=0;i<slotProbability.length;i++){
            this.quadProbability += slotProbability[i];
        }

        //make it cumulative probability
        double subtractor=1;
        for (int i=0;i<slotProbability.length;i++){
            subtractor *= slotProbability[i];
        }

        this.quadProbability -= subtractor;
    }

    private void calculateFlushProbability(){
        double slotProbability[] = new double[4];

        for (Card.CardSuit flushTarget : Card.CardSuit.values() ){ //for each card showing...
            if (Card.findSuits(hand, flushTarget)==5){ //found flush
                this.flushProbability=1;
                break;
            }
            else{
                int needed = 5-Card.findSuits(hand, flushTarget), remaining = Card.findSuits(deck, flushTarget), chancesLeft = 7-hand.length;
                slotProbability[flushTarget.ordinal()] = hypergeoDist(remaining, deck.length, chancesLeft, needed);

                if (chancesLeft<needed)
                    slotProbability[flushTarget.ordinal()] = 0;
            }

        }
        for (int i=0;i<slotProbability.length;i++){
            this.flushProbability += slotProbability[i];
        }
        //make it cumulative probability
        double subtractor=1;
        for (int i=0;i<slotProbability.length;i++){
            subtractor *= slotProbability[i];
        }
        this.flushProbability -= subtractor;
    }

    private void calculateFullHouseProbability() {

        this.fullHouseProbability = this.pairProbability*this.tripleProbability;
    }


    private Card.CardVal possibleStraights[][] = {
        {Card.CardVal.Ace, Card.CardVal.Two, Card.CardVal.Three, Card.CardVal.Four, Card.CardVal.Five},
            {Card.CardVal.Two, Card.CardVal.Three, Card.CardVal.Four, Card.CardVal.Five, Card.CardVal.Six},
            {Card.CardVal.Three, Card.CardVal.Four, Card.CardVal.Five, Card.CardVal.Six, Card.CardVal.Seven},
            {Card.CardVal.Four, Card.CardVal.Five, Card.CardVal.Six, Card.CardVal.Seven, Card.CardVal.Eight},
            {Card.CardVal.Five, Card.CardVal.Six, Card.CardVal.Seven, Card.CardVal.Eight, Card.CardVal.Nine},
            {Card.CardVal.Six, Card.CardVal.Seven, Card.CardVal.Eight, Card.CardVal.Nine, Card.CardVal.Ten},
            {Card.CardVal.Seven, Card.CardVal.Eight, Card.CardVal.Nine, Card.CardVal.Ten, Card.CardVal.Jack},
            {Card.CardVal.Eight, Card.CardVal.Nine, Card.CardVal.Ten, Card.CardVal.Jack, Card.CardVal.Queen},
            {Card.CardVal.Nine, Card.CardVal.Ten, Card.CardVal.Jack, Card.CardVal.Queen, Card.CardVal.King},
            {Card.CardVal.Ten, Card.CardVal.Jack, Card.CardVal.Queen, Card.CardVal.King, Card.CardVal.Ace},
    };
    private void calculateStraightProbability() {

        double slotProbability[] = new double[10];
        int setCounter=0;
        for (Card.CardVal[] set : possibleStraights){
            int chancesLeft=7-hand.length;
            slotProbability[setCounter]=1;
            for (Card.CardVal compare : set){ //for each of the five values in the straight
                int stillLeftInDeck = Card.findVals(deck, compare);
                if (Card.findVals(hand, compare)==0){
                    slotProbability[setCounter]*=hypergeoDist(stillLeftInDeck, deck.length, chancesLeft, 1);
                }
                else
                    chancesLeft++;
                if (chancesLeft<=0){
                    slotProbability[setCounter]=0;
                    break;
                }
                chancesLeft--;
            }
            setCounter++;
        }

        straightProbability = 0;
        for (int i=0;i<slotProbability.length;i++){
            straightProbability += (slotProbability[i]);
        }
        if (this.straightProbability>1)
            this.straightProbability=1;

    }

    static BigInteger choose(final int N, final int K) {
        BigInteger ret = BigInteger.ONE;
        for (int k = 0; k < K; k++) {
            ret = ret.multiply(BigInteger.valueOf(N-k))
                    .divide(BigInteger.valueOf(k+1));
        }
        return ret;
    }

    // P(B given A) = P(A)+P(B)-P(A & B)

    static double hypergeoDist(int remainingInDeck, int deckSize, int chancesLeft, int howManyDoINeed){
        //http://stattrek.com/probability-distributions/hypergeometric.aspx
        //https://en.wikipedia.org/wiki/Hypergeometric_distribution#Example
	      /*
	       * k = remainingInDeck
	       * N = deckSize
	       * n = chancesLeft
	       * x = howManyDoINeed (getting only one is success for a pair)
	       *
	       * */


        BigInteger top = choose(remainingInDeck, howManyDoINeed).multiply(choose(deckSize-remainingInDeck,(chancesLeft-howManyDoINeed)));
        BigInteger bottom = choose(deckSize, chancesLeft);



        return top.doubleValue()/bottom.doubleValue();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(this.pairProbability);
        parcel.writeDouble(this.tripleProbability);
        parcel.writeDouble(this.quadProbability);
        parcel.writeDouble(this.flushProbability);
        parcel.writeDouble(this.fullHouseProbability);
        parcel.writeDouble(this.straightProbability);
        parcel.writeSerializable(this.hand);
        parcel.writeSerializable(this.deck);
    }

    protected Probabilities(Parcel in) {
        this.pairProbability = in.readDouble();
        this.tripleProbability = in.readDouble();
        this.quadProbability = in.readDouble();
        this.flushProbability = in.readDouble();
        this.fullHouseProbability = in.readDouble();
        this.straightProbability = in.readDouble();
        this.hand = (Card[]) in.readSerializable();
        this.deck = (Card[]) in.readSerializable();
    }



    public static final Creator<Probabilities> CREATOR = new Creator<Probabilities>() {
        @Override
        public Probabilities createFromParcel(Parcel in) {
            return new Probabilities(in);
        }

        @Override
        public Probabilities[] newArray(int size) {
            return new Probabilities[size];
        }
    };
}
