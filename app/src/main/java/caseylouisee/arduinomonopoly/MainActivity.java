package caseylouisee.arduinomonopoly;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    //Test line github
    Button btnPlay;
    Button btnAddPlayer;
    Button btnRemovePlayer;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    public static String EXTRA_ADDRESS = "device_address";
    public static String PLAYER1 = "Player 1";
    public static String PLAYER2 = "Player 2";
    public static String PLAYER3 = "Player 3";
    public static String PLAYER4 = "Player 4";
    int playerCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnRemovePlayer = (Button) findViewById(R.id.btnRemovePlayer);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        } else if (!myBluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText3 = (EditText) findViewById(R.id.editText3);
                EditText editText4 = (EditText) findViewById(R.id.editText4);
                if(playerCount==2){
                    editText3.setVisibility(View.VISIBLE);
                    playerCount++;
                } else if(playerCount==3){
                    editText4.setVisibility(View.VISIBLE);
                    playerCount++;
                }
            }
        });

        btnRemovePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText3 = (EditText) findViewById(R.id.editText3);
                EditText editText4 = (EditText) findViewById(R.id.editText4);
                if(playerCount==4){
                    editText4.setVisibility(View.INVISIBLE);
                    playerCount--;
                } else if(playerCount==3){
                    editText3.setVisibility(View.INVISIBLE);
                    playerCount--;
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
                ArrayList list = new ArrayList();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice bt : pairedDevices) {
                        // automatically connects to arduino device set up
                        list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                        if ((bt.getName().equals("Arduino")) || (bt.getName().equals("HC-06"))) {
                            // Get the device MAC address, the last 17 chars in the View
                            String info = ("Arduino" + "\n" + bt.getAddress());
                            String address = info.substring(info.length() - 17);

                            // Make an intent to start next activity.
                            Intent i = new Intent(MainActivity.this, GamePlay.class);

                            //Change the activity.
                            i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity

                            EditText p1 = (EditText) findViewById(R.id.editText);
                            String player1 = p1.getText().toString();
                            EditText p2 = (EditText) findViewById(R.id.editText2);
                            String player2 = p2.getText().toString();

                            i.putExtra(PLAYER1, player1);
                            i.putExtra(PLAYER2, player2);

                            EditText p3 = (EditText) findViewById(R.id.editText3);
                            EditText p4 = (EditText) findViewById(R.id.editText4);
                            if(p3.getVisibility()==View.VISIBLE) {
                                String player3 = p3.getText().toString();
                                i.putExtra(PLAYER3, player3);
                            }

                            if(p4.getVisibility()==View.VISIBLE){
                                String player4 = p4.getText().toString();
                                i.putExtra(PLAYER4, player4);
                            }

                            startActivity(i);
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
