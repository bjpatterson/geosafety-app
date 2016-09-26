package edu.bsu.cs639.safety_alert_entry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Alert_Entry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert__entry);

        Button button= (Button) findViewById(R.id.SubmitAlertButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm(v);
            }
        });
    }

    protected void submitForm(View v){
        TextView tv = (TextView) findViewById(R.id.ResponseTextView);

        if(!validateForm()) {
            tv.setText("Bad Inputs! (Try again)");
        }
        else{
            tv.setText("Inputs OK");
            GeoPoint point = AlertGeoCoder.getLocationFromAddress(
                    ((EditText) findViewById(R.id.LocationText)).getText().toString()
            );
            if(point != null) {

            } else {
                tv.setText("There was a problem locating the address. Try to be more specific.");
            }
        }
    }

    protected boolean validateForm(){
        EditText titleText = (EditText) findViewById(R.id.TitleText);
        EditText descText = (EditText) findViewById(R.id.DescText);
        EditText addrText = (EditText) findViewById(R.id.LocationText);
        EditText alertLevelText = (EditText) findViewById(R.id.ThreatLevelText);
        EditText alertRadiusText = (EditText) findViewById(R.id.AlertDistanceText);

        if (titleText.getText().toString().equals("")) return false;

        if (descText.getText().toString().equals("")) return false;

        if (addrText.getText().toString().equals("")) return false;

        if(
                alertLevelText.getText().toString().equals("")
                || Integer.parseInt(alertLevelText.getText().toString()) > 5
                || Integer.parseInt(alertLevelText.getText().toString()) < 0
        ) return false;

        if(
                alertRadiusText.getText().toString().equals("")
                || Integer.parseInt(alertRadiusText.getText().toString()) < 1
        ) return false;

        return true;
    }
}
