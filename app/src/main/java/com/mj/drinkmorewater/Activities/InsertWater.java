package com.mj.drinkmorewater.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;

import com.mj.drinkmorewater.R;
import com.mj.drinkmorewater.db.DatabaseHandler;
import com.mj.drinkmorewater.db.Water;

public class InsertWater extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    SeekBar seekBar;
    EditText editTextComment;
    TextView txtMl;
    Button btnInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_water);

        seekBar=(SeekBar)findViewById(R.id.seekBarWaterAmount);
        editTextComment=(EditText)findViewById(R.id.editTextComment);
        txtMl=(TextView)findViewById(R.id.txtMl);
        btnInsert=(Button)findViewById(R.id.btnInsert);

        btnInsert.setOnClickListener(saveWaterButtonClicked);
        seekBar.setOnSeekBarChangeListener(this);

        seekBar.setMax(3000);




    }

    View.OnClickListener saveWaterButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editTextComment.getText().length() != 0) {
                saveWater();
                finish();
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setTitle(R.string.errorTitle);
//                builder.setMessage(R.string.errorMessage);
//                builder.setPositiveButton(R.string.errorButton, null);
//                builder.show();
//            }
        }
    };



    private void saveWater() {
        DatabaseHandler databaseHandler= new DatabaseHandler(getApplicationContext());

        if (getIntent().getExtras() == null) {
            int amount= seekBar.getProgress();
            String comment=editTextComment.getText().toString();
            Water water=new Water(amount,comment);
            databaseHandler.insertWater(water);

        }
//        else {
//            databaseConnector.updateContact(rowID, nameEditText.getText().toString(), emailEditText
//                    .getText().toString(), phoneEditText.getText().toString(), streetEditText
//                    .getText().toString(), cityEditText.getText().toString(),commentEditText.getText().toString());
//        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        txtMl.setText(Integer.toString((int) seekBar.getProgress()) + " ml");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


}
