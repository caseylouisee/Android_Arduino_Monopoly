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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * MainActivity class extends AppCompatActivity
 * Created by Casey Denner
 */
public class MainActivity extends AppCompatActivity {

    /**
     * "Play" button on the application
     */
    Button btnPlay;

    /**
     * "Add Player" button on the application
     */
    Button btnAddPlayer;

    /**
     * "Remove Player" button on the application
     */
    Button btnRemovePlayer;

    /**
     * CheckBox that decides whether the application manages player's funds
     */
    CheckBox cbxAppManagesFunds;

    /**
     * Bluetooth Adapter
     */
    private BluetoothAdapter m_Bluetooth = null;

    /**
     * String that holds the device mac address. Static to send through intent.
     */
    public static String EXTRA_ADDRESS = "device_address";

    /**
     * Boolean that tells app to manage funds or not
     */
    public static String MANAGE_FUNDS = "manage_funds_bool";

    /**
     * String that holds player 1's name. Static to send through intent.
     */
    public static String PLAYER1 = "Player 1";

    /**
     * String that holds player 2's name. Static to send through intent.
     */
    public static String PLAYER2 = "Player 2";

    /**
     * String that holds player 3's name. Static to send through intent.
     */
    public static String PLAYER3 = "Player 3";

    /**
     * String that holds player 4's name. Static to send through intent.
     */
    public static String PLAYER4 = "Player 4";

    /**
     * Boolean used to check if input is valid
     */
    private Boolean m_validInput = true;

    /**
     * int representing the number of players, initialised to 2
     */
    int m_playerCount = 2;

    /**
     * The first method that is called within the application.
     * This method manages the buttons on the screen and holds the onClickListeners for them.
     * @param savedInstanceState state of the application on last usage
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnAddPlayer = (Button) findViewById(R.id.btnAddPlayer);
        btnRemovePlayer = (Button) findViewById(R.id.btnRemovePlayer);
        cbxAppManagesFunds = (CheckBox) findViewById(R.id.cbxAppManagesFunds);

        //if the device has bluetooth
        m_Bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (m_Bluetooth == null) {
            //Show a message. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        } else if (!m_Bluetooth.isEnabled()) {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText3 = (EditText) findViewById(R.id.editText3);
                EditText editText4 = (EditText) findViewById(R.id.editText4);
                if(m_playerCount==2){
                    editText3.setVisibility(View.VISIBLE);
                    m_playerCount++;
                } else if(m_playerCount==3){
                    editText4.setVisibility(View.VISIBLE);
                    m_playerCount++;
                }
            }
        });

        btnRemovePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText3 = (EditText) findViewById(R.id.editText3);
                EditText editText4 = (EditText) findViewById(R.id.editText4);
                if(m_playerCount==4){
                    editText4.setVisibility(View.INVISIBLE);
                    m_playerCount--;
                } else if(m_playerCount==3){
                    editText3.setVisibility(View.INVISIBLE);
                    m_playerCount--;
                }
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pairedDevices = m_Bluetooth.getBondedDevices();
                ArrayList list = new ArrayList();

                // automatically connects to arduino device set up
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice bt : pairedDevices) {
                        //Get the device's name and the address
                        list.add(bt.getName() + "\n" + bt.getAddress());
                        if ((bt.getName().equals("Arduino")) || (bt.getName().equals("HC-06"))) {
                            // Get the device MAC address, the last 17 chars in the View
                            String info = ("Arduino" + "\n" + bt.getAddress());
                            String address = info.substring(info.length() - 17);

                            // Make an intent to start next activity.
                            Intent i = new Intent(MainActivity.this, GamePlay.class);

                            //Change the activity.
                            //this will be received at ledControl (class) Activity
                            i.putExtra(EXTRA_ADDRESS, address);

                            Boolean manageFunds;

                            if (cbxAppManagesFunds.isChecked()) {
                                manageFunds = true;
                            } else {
                                manageFunds = false;
                            }

                            i.putExtra(MANAGE_FUNDS, manageFunds);

                            validateInput();

                            if (m_validInput) {
                                EditText p1 = (EditText) findViewById(R.id.editText);
                                String player1 = p1.getText().toString();
                                EditText p2 = (EditText) findViewById(R.id.editText2);
                                String player2 = p2.getText().toString();

                                i.putExtra(PLAYER1, player1);
                                i.putExtra(PLAYER2, player2);

                                EditText p3 = (EditText) findViewById(R.id.editText3);
                                EditText p4 = (EditText) findViewById(R.id.editText4);
                                if (p3.getVisibility() == View.VISIBLE) {
                                    String player3 = p3.getText().toString();
                                    i.putExtra(PLAYER3, player3);
                                }

                                if (p4.getVisibility() == View.VISIBLE) {
                                    String player4 = p4.getText().toString();
                                    i.putExtra(PLAYER4, player4);
                                }

                                startActivity(i);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid input, names must be more than 2 " +
                                                "and less than 15 characters",
                                        Toast.LENGTH_LONG).show();
                                m_validInput = true;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * This method ensures the names input are valid. i.e. less than 10 characters
     * @return m_validInput boolean representing if the data is valid or not
     */
    private Boolean validateInput(){
        int playerCount = 0;

        EditText p1 = (EditText) findViewById(R.id.editText);
        String player1 = p1.getText().toString();
        playerCount++;

        EditText p2 = (EditText) findViewById(R.id.editText2);
        String player2 = p2.getText().toString();
        playerCount++;

        if(player1.length()>=15 || player2.length()>=15 ||
                player1.length()<2 || player2.length()<2){
            m_validInput=false;
        } else {
            EditText p3 = (EditText) findViewById(R.id.editText3);
            EditText p4 = (EditText) findViewById(R.id.editText4);
            if (p3.getVisibility() == View.VISIBLE) {
                String player3 = p3.getText().toString();
                playerCount++;
                if(player3.length()>=15 || player3.length()<2){
                    m_validInput = false;
                } else {
                    if (p4.getVisibility() == View.VISIBLE) {
                        String player4 = p4.getText().toString();
                        playerCount++;
                        if(player4.length()>=15 || player4.length()<2){
                            m_validInput=false;
                        }
                    }
                }
            }
        }
        return m_validInput;
    }

    /**
     * Method that creates the menu options
     * @param menu
     * @return boolean true
     */
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
