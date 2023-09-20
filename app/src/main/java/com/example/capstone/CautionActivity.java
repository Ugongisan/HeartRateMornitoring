package com.example.capstone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class CautionActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_caution);

        findViewById(R.id.button2).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button2:
                this.finish();
                break;
        }
    }
}