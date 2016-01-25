package org.sacids.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.odk.collect.android.R;
import org.sacids.android.activities.MainMenuActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_start_odk_forms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            }
        });
    }
}
