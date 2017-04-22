package edu.depaul.csc372.cnewmiller_pokercalculator;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class calculatorActivity extends AppCompatActivity {

    private Probabilities baseProbs;
    private double CHANCES[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Intent tableIntent = getIntent();
        if (tableIntent!=null){
            baseProbs = tableIntent.getParcelableExtra("Probabilities");
            if (baseProbs !=null) {
                this.CHANCES = baseProbs.getAllProbabilities();
                oddsView calc = (oddsView) findViewById(R.id.oddsView_pair);
                calc.start(this.CHANCES[0]);
                calc = (oddsView) findViewById(R.id.oddsView_triple);
                calc.start(this.CHANCES[1]);
                calc = (oddsView) findViewById(R.id.oddsView_quad);
                calc.start(this.CHANCES[2]);
                calc = (oddsView) findViewById(R.id.oddsView_flush);
                calc.start(this.CHANCES[3]);
                calc = (oddsView) findViewById(R.id.oddsView_fullHouse);
                calc.start(this.CHANCES[4]);
                calc = (oddsView) findViewById(R.id.oddsView_straight);
                calc.start(this.CHANCES[5]);
            }
        }
        else{
            //calculations failed, do nothing
        }

    }

}
