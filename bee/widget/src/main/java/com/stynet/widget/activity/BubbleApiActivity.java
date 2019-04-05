package com.stynet.widget.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.stynet.widget.BubbleDialog;
import com.stynet.widget.BubbleLayout;
import com.stynet.widget.R;
import com.stynet.widget.helper.UnitConvert;

/**
 * Created by xx shuiDianBing, 2018/08/30-15:39:15:39.Refer to the website: nullptr
 **/

public class BubbleApiActivity extends AppCompatActivity {
    private BubbleLayout bubbleLayout;
    private View dialogTop;
    private View csPurple;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        bubbleLayout = findViewById(R.id.bubbleLayout);
        View.OnClickListener clickListener = getClickListener();
        findViewById(R.id.rbLeft).setOnClickListener(clickListener);
        findViewById(R.id.rbTop).setOnClickListener(clickListener);
        findViewById(R.id.rbRight).setOnClickListener(clickListener);
        findViewById(R.id.rbBottom).setOnClickListener(clickListener);
        findViewById(R.id.cbWhite).setOnClickListener(clickListener);
        findViewById(R.id.cbGrey).setOnClickListener(clickListener);
        findViewById(R.id.cbBlack).setOnClickListener(clickListener);
        findViewById(R.id.cbRed).setOnClickListener(clickListener);
        findViewById(R.id.cbOrange).setOnClickListener(clickListener);
        findViewById(R.id.cbBlue).setOnClickListener(clickListener);
        findViewById(R.id.cbGreen).setOnClickListener(clickListener);
        findViewById(R.id.cbPurple).setOnClickListener(clickListener);
        findViewById(R.id.csWhite).setOnClickListener(clickListener);
        findViewById(R.id.csGrey).setOnClickListener(clickListener);
        findViewById(R.id.csBlack).setOnClickListener(clickListener);
        findViewById(R.id.csRed).setOnClickListener(clickListener);
        findViewById(R.id.csOrange).setOnClickListener(clickListener);
        findViewById(R.id.csBlue).setOnClickListener(clickListener);
        findViewById(R.id.csGreen).setOnClickListener(clickListener);
        findViewById(R.id.tvNextPage).setOnClickListener(clickListener);
        csPurple = findViewById(R.id.csPurple);
        csPurple.setOnClickListener(clickListener);
        dialogTop = findViewById(R.id.btnDialogTop);
        dialogTop.setOnClickListener(clickListener);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = getSeekBarChangeListener();
        ((SeekBar) findViewById(R.id.sbBubbleRadius)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbLookPosition)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbLookWidth)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbLookLength)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbShadowRadius)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbShadowX)).setOnSeekBarChangeListener(seekBarChangeListener);
        ((SeekBar) findViewById(R.id.sbShadowY)).setOnSeekBarChangeListener(seekBarChangeListener);
    }
    private View.OnClickListener getClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = view.getId();
                if (i == R.id.tvNextPage)
                    startActivity(new Intent(BubbleApiActivity.this, TestDialogActivity.class));
                else if (i == R.id.rbLeft)
                    bubbleLayout.setLook(BubbleLayout.Look.LEFT);
                else if (i == R.id.rbTop)
                    bubbleLayout.setLook(BubbleLayout.Look.TOP);
                else if (i == R.id.rbRight)
                    bubbleLayout.setLook(BubbleLayout.Look.RIGHT);
                else if (i == R.id.rbBottom)
                    bubbleLayout.setLook(BubbleLayout.Look.BOTTOM);
                else if (i == R.id.cbWhite)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.white));
                else if (i == R.id.cbGrey)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.darker_gray));
                else if (i == R.id.cbBlack)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.black));
                else if (i == R.id.cbRed)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.holo_red_light));
                else if (i == R.id.cbOrange)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.holo_orange_light));
                else if (i == R.id.cbBlue)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.holo_blue_light));
                else if (i == R.id.cbGreen)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.holo_green_light));
                else if (i == R.id.cbPurple)
                    bubbleLayout.setBubbleColor(getResources().getColor(android.R.color.holo_purple));
                else if (i == R.id.csWhite) {
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.white));
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.darker_gray));
                } else if (i == R.id.csGrey)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.darker_gray));
                else if (i == R.id.csBlack)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.black));
                else if (i == R.id.csRed)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.holo_red_light));
                else if (i == R.id.csOrange)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.holo_orange_light));
                else if (i == R.id.csBlue)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.holo_blue_light));
                else if (i == R.id.csGreen)
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.holo_green_light));
                else if (i == R.id.csPurple) {
                    bubbleLayout.setShadowColor(getResources().getColor(android.R.color.holo_purple));
                    new BubbleDialog(BubbleApiActivity.this).addContentView(LayoutInflater.from(BubbleApiActivity.this).inflate(R.layout.test, null))
                            .setClickedView(csPurple).calBar(true).setTransParentBackground().show();
                } else if (i == R.id.btnDialogTop) {
                    new BubbleDialog(BubbleApiActivity.this).addContentView(LayoutInflater.from(BubbleApiActivity.this).inflate(R.layout.test, null))
                            .setClickedView(dialogTop).setTransParentBackground().calBar(true).show();
                }
                bubbleLayout.invalidate();
            }
        };
    }
    private SeekBar.OnSeekBarChangeListener getSeekBarChangeListener(){
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int i = seekBar.getId();
                if (i == R.id.sbBubbleRadius)
                    bubbleLayout.setBubbleRadius(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbLookPosition)
                    bubbleLayout.setLookPosition(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbLookWidth)
                    bubbleLayout.setLookWidth(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbLookLength)
                    bubbleLayout.setLookLength(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbShadowRadius)
                    bubbleLayout.setShadowRadius(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbShadowX)
                    bubbleLayout.setShadowX(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                else if (i == R.id.sbShadowY)
                    bubbleLayout.setShadowY(UnitConvert.dip2px(BubbleApiActivity.this, progress));
                bubbleLayout.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }
}
