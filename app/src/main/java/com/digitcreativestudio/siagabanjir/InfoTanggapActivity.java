package com.digitcreativestudio.siagabanjir;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;


public class InfoTanggapActivity extends ActionBarActivity {

    private final String MOVE_NEXT = "next";
    private final String MOVE_PREV = "prev";

    // Main's view flippers and buttons
    ViewFlipper titleFlipper, viewFlipper;
    Button next, prev;

    // Banjir's view flippers and buttons
    ViewFlipper titleBanjirFlipper, viewBanjirFlipper;
    Button banjirNext, banjirPrev;

    // Reda's view flippers and buttons
    ViewFlipper titleRedaFlipper, viewRedaFlipper;
    Button redaNext, redaPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tanggap);


        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));

        /* Initialize main's flippers and buttons */

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        titleFlipper = (ViewFlipper) findViewById(R.id.titleFlipper);

        next = (Button) findViewById(R.id.next);
        prev = (Button) findViewById(R.id.prev);

        /* Initialize Banjir's flippers and buttons */

        viewBanjirFlipper = (ViewFlipper) findViewById(R.id.viewBanjirFlipper);
        titleBanjirFlipper = (ViewFlipper) findViewById(R.id.titleBanjirFlipper);

        banjirNext = (Button) findViewById(R.id.banjirNext);
        banjirPrev = (Button) findViewById(R.id.banjirPrev);

        /* Initialize Reda's flippers and buttons */

        viewRedaFlipper = (ViewFlipper) findViewById(R.id.viewRedaFlipper);
        titleRedaFlipper = (ViewFlipper) findViewById(R.id.titleRedaFlipper);

        redaNext = (Button) findViewById(R.id.redaNext);
        redaPrev = (Button) findViewById(R.id.redaPrev);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(next, prev, titleFlipper, viewFlipper, MOVE_NEXT);

                checkPosition(viewFlipper.getDisplayedChild());
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(next, prev, titleFlipper, viewFlipper, MOVE_PREV);

                checkPosition(viewFlipper.getDisplayedChild());
            }
        });


        banjirNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(banjirNext, banjirPrev, titleBanjirFlipper, viewBanjirFlipper, MOVE_NEXT);
            }
        });

        banjirPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(banjirNext, banjirPrev, titleBanjirFlipper, viewBanjirFlipper, MOVE_PREV);
            }
        });


        redaNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(redaNext, redaPrev, titleRedaFlipper, viewRedaFlipper, MOVE_NEXT);
            }
        });

        redaPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipFlipper(redaNext, redaPrev, titleRedaFlipper, viewRedaFlipper, MOVE_PREV);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_info_tanggap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void flipFlipper(Button next, Button prev, ViewFlipper titleFlipper, ViewFlipper viewFlipper, String move){
        int inAnimation, outAnimation;

        if(move.equals(MOVE_NEXT)){
            inAnimation = R.anim.in_from_right;
            outAnimation = R.anim.out_to_left;
        }else{
            inAnimation = R.anim.in_from_left;
            outAnimation = R.anim.out_to_right;
        }

        titleFlipper.setInAnimation(this, inAnimation);
        titleFlipper.setOutAnimation(this, outAnimation);

        viewFlipper.setInAnimation(this, inAnimation);
        viewFlipper.setOutAnimation(this, outAnimation);

        if(move.equals(MOVE_NEXT)){
            titleFlipper.showNext();
            viewFlipper.showNext();

            prev.setEnabled(true);
            if(viewFlipper.getChildCount()-1 == viewFlipper.getDisplayedChild()){
                next.setEnabled(false);
            }
        }else{
            titleFlipper.showPrevious();
            viewFlipper.showPrevious();

            next.setEnabled(true);
            if(viewFlipper.getDisplayedChild() == 0){
                prev.setEnabled(false);
            }
        }

    }
    private void checkPosition(int position){
        if(position == 3){
            //reset banjir's flippers and buttons state
            banjirPrev.setEnabled(false);
            banjirNext.setEnabled(true);

            titleBanjirFlipper.setInAnimation(null);
            titleBanjirFlipper.setOutAnimation(null);

            viewBanjirFlipper.setInAnimation(null);
            viewBanjirFlipper.setOutAnimation(null);

            titleBanjirFlipper.setDisplayedChild(0);
            viewBanjirFlipper.setDisplayedChild(0);

        }else if(position == 4){
            //reset reda's flippers and buttons state
            redaPrev.setEnabled(false);
            redaNext.setEnabled(true);

            titleRedaFlipper.setInAnimation(null);
            titleRedaFlipper.setOutAnimation(null);

            viewRedaFlipper.setInAnimation(null);
            viewRedaFlipper.setOutAnimation(null);

            titleRedaFlipper.setDisplayedChild(0);
            viewRedaFlipper.setDisplayedChild(0);
        }
    }
}
