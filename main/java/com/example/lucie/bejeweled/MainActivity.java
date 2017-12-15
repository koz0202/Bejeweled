package com.example.lucie.bejeweled;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import static com.example.lucie.bejeweled.R.menu.main_menu;

public class MainActivity extends Activity {
    BejeweledMain bejeweledMain = new BejeweledMain();

    Boolean checked;

    SharedPreferences.Editor mySharedEditor;
    SharedPreferences mySharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*CheckBox checkBox = findViewById(R.id.checkBox);
        Button button = findViewById(R.id.novaHra);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                startNewBej();
            }
        });


*/

        Button button = findViewById(R.id.novaHra);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                startNewBej();
            }
        });


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        final CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);

        if(preferences.contains("checked") && preferences.getBoolean("checked",false) == true) {
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);

        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.remove("checked");
                if(checkBox.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                }else{
                    editor.putBoolean("checked", false);
                    editor.apply();
                }
            }
        });


    }

    //@Override
   // public void onCheckedChange(){

  //  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item){
      int id = item.getItemId();

      if(id == R.id.menu_about){
          Intent intent = new Intent(this, About.class);
          startActivity(intent);
      }
      return super.onOptionsItemSelected(item);
  }

    private void startNewBej(){
        Intent intent = new Intent(this, BejeweledMain.class);
        intent.putExtra("check", ((CheckBox)findViewById(R.id.checkBox)).isChecked());
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(main_menu, menu);
        return true;
    }
}
