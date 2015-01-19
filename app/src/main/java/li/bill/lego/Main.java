package li.bill.lego;

//http://stackoverflow.com/questions/20244337/consumerirmanager-api-19

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;

import android.hardware.ConsumerIrManager;
import android.os.Build;


public class Main extends ActionBarActivity {
    ConsumerIrManager mCIR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the ConsumerIrManager
        mCIR = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);

        Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {





                System.out.println("yo!");
                String something = "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0155 0056 0015 0E45";
                something = "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0040 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e";

                System.out.println(hex2dec(something));
                System.out.println(count2duration(hex2dec(something)));

                if (!mCIR.hasIrEmitter()) {
                    System.out.println("No IR Emitter found");
                    return;
                }
                int SAMSUNG_FREQ = 37683;
                int[] SAMSUNG_POWER_TOGGLE_COUNT = {341, 172, 21, 21, 21, 65, 21, 65, 21, 65, 21, 21, 21, 65, 21, 65, 21, 65, 21, 65, 21, 65, 21, 65, 21, 21, 21, 21, 21, 21, 21, 21, 21, 65, 21, 65, 21, 65, 21, 21, 21, 65, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 65, 21, 65, 21, 21, 21, 1510, 341, 86, 21, 3653};
                int[] SAMSUNG_POWER_TOGGLE_DURATION = {8866, 4472, 546, 546, 546, 1690, 546, 1690, 546, 1690, 546, 546, 546, 1690, 546, 1690, 546, 1690, 546, 1690, 546, 1690, 546, 1690, 546, 546, 546, 546, 546, 546, 546, 546, 546, 1690, 546, 1690, 546, 1690, 546, 546, 546, 1690, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 546, 1690, 546, 1690, 546, 546, 546, 39260, 8866, 2236, 546, 94978};

                if (Build.VERSION.SDK_INT == 19) {
                    int lastIdx = Build.VERSION.RELEASE.lastIndexOf(".");
                    int VERSION_MR = Integer.valueOf(Build.VERSION.RELEASE.substring(lastIdx + 1));
                    if (VERSION_MR < 3) {
                        // Before version of Android 4.4.2
                        mCIR.transmit(SAMSUNG_FREQ, SAMSUNG_POWER_TOGGLE_COUNT);
                    } else {
                        // Later version of Android 4.4.3
                        mCIR.transmit(SAMSUNG_FREQ, SAMSUNG_POWER_TOGGLE_DURATION);
                    }
                }
            }

        });

    }

    protected String count2duration(String countPattern) {
        List<String> list = new ArrayList<String>(Arrays.asList(countPattern.split(",")));
        int frequency = Integer.parseInt(list.get(0));
        int pulses = 1000000 / frequency;
        int count;
        int duration;

        list.remove(0);

        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i));
            duration = count * pulses;
            list.set(i, Integer.toString(duration));
        }

        String durationPattern = "";
        for (String s : list) {
            durationPattern += s + ",";
        }


        return durationPattern;
    }

    protected String hex2dec(String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        for (int i = 0; i < list.size(); i++) {
            list.set(i, Integer.toString(Integer.parseInt(list.get(i), 16)));
        }

        frequency = (int) (1000000 / (frequency * 0.241246));
        list.add(0, Integer.toString(frequency));

        irData = "";
        for (String s : list) {
            irData += s + ",";
        }
        return irData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
