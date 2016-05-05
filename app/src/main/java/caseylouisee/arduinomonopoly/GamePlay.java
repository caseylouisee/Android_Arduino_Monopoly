package caseylouisee.arduinomonopoly;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import us.dicepl.android.sdk.BluetoothManipulator;
import us.dicepl.android.sdk.DiceConnectionListener;
import us.dicepl.android.sdk.DiceController;
import us.dicepl.android.sdk.DiceResponseAdapter;
import us.dicepl.android.sdk.DiceResponseListener;
import us.dicepl.android.sdk.DiceScanningListener;
import us.dicepl.android.sdk.Die;
import us.dicepl.android.sdk.responsedata.RollData;

/**
 * GamePlay class extends AppCompatActivity
 * implements TextToSpeech.OnInitListener and RecognitionListener
 * Created by Casey Denner
 */
public class GamePlay extends AppCompatActivity implements TextToSpeech.OnInitListener, RecognitionListener {

    /**
     * "End Game" button on the application
     */
    Button btnEnd;

    /**
     * String holding the connected device's mac address
     */
    String address = null;

    /**
     * ProgressDialog
     */
    private ProgressDialog progress;

    /**
     * Bluetooth adapter
     */
    BluetoothAdapter m_Bluetooth = null;

    /**
     * Bluetooth socket
     */
    BluetoothSocket btSocket = null;

    /**
     * Boolean to check whether bluetooth is connected to a device
     */
    private boolean isBtConnected = false;

    /**
     * SPP UUID. Look for it
     */
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * Dice+ developer key needed to make connection to dice
     */
    private static final int[] developerKey = new int[]
            {0x5e, 0x77, 0x68, 0xd3, 0xc6, 0xa0, 0x17, 0x0a};

    /**
     * String CONSTANT Tag used for logs and error checking
     */
    private static final String TAG = "DICEPlus";

    /**
     * Die object containing the Dice+
     */
    private Die dicePlus;

    /**
     * TextView on the application containing the player's name
     */
    TextView player;

    /**
     * TextView on the application containing the dice roll
     */
    TextView rollResult;

    /**
     * TextView on the application containing the player's current position details
     */
    TextView currentPosition;

    /**
     * TextView on the application containing the player's updated position after the dice roll
     */
    TextView updatedPosition;

    /**
     * TextView on the application containing the type of square the player landed on
     */
    TextView locationType;

    /**
     * TextView on the application containing the recognized speech spoken by the user after
     * using speech recognition
     */
    TextView recognizedSpeech;

    /**
     * TextView on the application containing the player's funds
     */
    TextView funds;

    /**
     * int representing the current player's turn
     */
    private int m_currentTurn;

    /**
     * ArrayList containing all Players
     */
    private ArrayList<Player> players;

    /**
     * int holding the number of players in the game
     */
    private int m_numPlayers;

    /**
     * Board object used within the game
     */
    private Board m_board;

    /**
     * Boolean about manageFunds, true means the application manages the funds, false means the
     * application doesn't manage the funds
     */
    private Boolean m_manageFunds;

    /**
     * int representing the number of rolls a player has had whilst in jail
     */
    private int m_jailCount;

    /**
     * int containing the freeParking funds collected through taxes on the board
     */
    private int m_freeParking;

    /**
     * TextToSpeech object used to translate text to speech using the Android Speech Engine
     */
    private TextToSpeech m_tts;

    /**
     * SpeechRecognizer object used to recognize input speech from players during play
     */
    private SpeechRecognizer speech=null;

    /**
     * This method is the board accessor method
     * @return m_board which is the board initialized for game play
     */
    public Board getBoard(){
        return m_board;
    }

    /**
     * This method is the numPlayers accessor
     * @return m_numPlayers which is the number of players in the game
     */
    public int getNumPlayers(){
        return m_numPlayers;
    }

    /**
     * This method returns who's current turn it is, represented by an int in the players array
     * @return m_currentTurn an int representing the players position in the array
     */
    public int getCurrentTurn(){
        return m_currentTurn;
    }

    /**
     * This method is the jailCount accessor
     * @return m_jailCount which monitors how many turns the player has been in jail for
     */
    public int getJailCount(){
        return m_jailCount;
    }

    /**
     * Method that converts text to speech given text using the Android TextToSpeech function.
     * @param text that is used to turn into speech.
     */
    private void convertTextToSpeech(String text) {
        if (null == text || "".equals(text)) {
            text = "Please give some input.";
        }
        m_tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    /**
     * DiceScanningListener that scans for the Dice+ to connect
     */
    DiceScanningListener scanningListener = new DiceScanningListener() {

        /**
         * Method called when a Dice+ is found
         * @param die
         */
        @Override
        public void onNewDie(Die die) {
            Log.d(TAG, "New DICE+ found");
            dicePlus = die;
            DiceController.connect(dicePlus);
        }

        /**
         * Method called when a scan is started
         */
        @Override
        public void onScanStarted() {
            Log.d(TAG, "Scan Started");
        }

        /**
         * Method called when a scan for a Dice+ fails.
         * Scan is then restarted if the initial one failed.
         */
        @Override
        public void onScanFailed() {
            Log.d(TAG, "Scan Failed");
            BluetoothManipulator.startScan();
        }

        /**
         * Method called when the scan is complete.
         * If no Dice+ has been located then another scan is initialised.
         */
        @Override
        public void onScanFinished() {
            Log.d(TAG, "Scan Finished");
            if(dicePlus == null){
                BluetoothManipulator.startScan();
            }
        }
    };

    /**
     * DiceConnectionListener that calls methods once a connection has been established/failed
     */
    DiceConnectionListener connectionListener = new DiceConnectionListener() {

        /**
         * Method called when a connection to the Dice+ has been established.
         * This method subscribes to the updates when the dice+ is rolled.
         * @param die The Dice+ object that is connected
         */
        @Override
        public void onConnectionEstablished(Die die) {
            Log.d(TAG, "DICE+ Connected");
            convertTextToSpeech("Dice+ Connected");
            DiceController.subscribeRolls(dicePlus);
        }

        /**
         * Method called when the connection to the Dice+ fails.
         * This method restarts the scan for a Dice+
         * @param die the Dice+ object that failed to connect
         * @param e Exception from the failed connection
         */
        @Override
        public void onConnectionFailed(Die die, Exception e) {
            Log.d(TAG, "Connection failed", e);
            dicePlus = null;
            BluetoothManipulator.startScan();
        }

        /**
         * Method called if the connection to the Dice+ is lost.
         * The dicePlus object is set to null and a scan is started.
         * @param die the Dice+ object that has lost it's connection
         */
        @Override
        public void onConnectionLost(Die die) {
            Log.d(TAG, "Connection lost");
            convertTextToSpeech("Dice connection lost");
            dicePlus = null;
            BluetoothManipulator.startScan();
        }
    };

    /*
    Here you can use the DiceResponseAdapter as it already has methods implemented unlike the
    DiceResponseListener Class.
    */
    DiceResponseListener responseListener = new DiceResponseAdapter(){

        /**
         * Method called every time the dice is rolled
         * @param die the Dice+ that the application is connected to
         * @param rolls the roll data produced by the dice
         * @param exception an exception if anything goes wrong during the roll
         */
        @Override
        public void onRoll(Die die, RollData rolls, Exception exception) {
            super.onRoll(die, rolls, exception);

            Log.d(TAG, "Roll: " + rolls.face);

            final int face = rolls.face;

            Player currentPlayer = players.get(m_currentTurn);
            int pos = currentPlayer.getCurrentPosition();

            if(pos+face <= 8){
                String number = String.valueOf(pos+face);
                moveMotor(number);
            }

            player = (TextView) findViewById(R.id.player);
            if(m_manageFunds) {
                funds = (TextView) findViewById(R.id.funds);
            }
            currentPosition = (TextView) findViewById(R.id.currentPosition);
            rollResult = (TextView) findViewById(R.id.rollResult);
            updatedPosition = (TextView) findViewById(R.id.updatedPosition);
            locationType = (TextView) findViewById(R.id.locationType);
            recognizedSpeech = (TextView) findViewById(R.id.recognizedSpeech);

            GamePlay.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String method = "onRoll";

                    Player currentPlayer = players.get(m_currentTurn);
                    int pos = currentPlayer.getCurrentPosition();
                    player.setText(currentPlayer.getName());

                    Log.d(method, "*****IT IS " + currentPlayer.getName().toUpperCase()
                            + "'S TURN*****");
                    Log.d(method, "Current Position:" + pos + ", " +
                            m_board.getSquare(pos).getName());

                    if(m_manageFunds) {
                        Log.d(method, "Current Funds: " + String.valueOf(currentPlayer.getMoney()));
                        funds.setText(String.valueOf(currentPlayer.getMoney()));
                    }

                    currentPosition.setText("Position before dice roll " + pos + ", " +
                            m_board.getSquare(pos).getName());

                    rollResult.setText("" + face);

                    updatedPosition.setText("Updated Position:");

                    locationType.setText("");

                    recognizedSpeech.setText("");

                    if (currentPlayer.getJail()) {
                        jailRoll(face, currentPlayer);
                    } else {
                        normalRoll(face, pos, currentPlayer);
                    }
                }
            });
        }
    };

    /**
     * This method is called once the dice has been rolled. It calls the correct method in the
     * Arduino code, parsing the number the player rolled.
     * @param num the number rolled
     */
    private void moveMotor(String num){
        Log.d("moveMotor", "TO" + num);
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write(("TO" + num).getBytes());
            }
            catch (IOException ex) {
                msg("Error");
            }
        }
    }

    /**
     * Method called if the player has resulted in a normal roll, i.e. the player is not in jail.
     * This method updates the textviews on the screen to represent the current player's
     * information.
     * @param face the number on the dice that has been rolled
     * @param pos the position of the player before the dice roll
     * @param currentPlayer the player that is currently in play
     */
    private void normalRoll(int face, int pos, Player currentPlayer){

        convertTextToSpeech("Position before dice roll" + pos +
                m_board.getSquare(pos).getName());

        int newPos = pos + face;
        if (newPos >= 40) {
            convertTextToSpeech("You passed go and collected 200");
            if(m_manageFunds){
                currentPlayer.addMoney(200);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
            }
            newPos = newPos - 40;
        }

        currentPlayer.setCurrentPosition(newPos);

        //updatedPosition = (TextView) findViewById(R.id.updatedPosition);
        updatedPosition.setText("Updated Position:" + newPos + ", " +
                m_board.getSquare(newPos).getName());
        convertTextToSpeech("You rolled a" + face);
        convertTextToSpeech("Position after dice roll" + newPos +
                m_board.getSquare(newPos).getName());

        locationType = (TextView) findViewById(R.id.locationType);

        Square location = m_board.getSquare(newPos);
        String locName = location.getName();

        if (location instanceof PropertySquare) {
            locationType.setText("Landed on property");
            buyProperty(currentPlayer, location);
        } else if (location instanceof CardSquare) {
            locationType.setText("Take a Card");
            convertTextToSpeech("Take a card");
            if(locName.equals("Chance")){
                chance(currentPlayer);
            } else {
                communityChest(currentPlayer);
            }
            nextTurnRoll();
        } else if (location instanceof SpecialSquare) {
            locationType.setText("Landed on special square");
            specialSquare(locName, currentPlayer);
        }
    }

    /**
     * This is the method that is called if the current player is in jail.
     * @param face the number rolled on the dice
     * @param currentPlayer the player who is currently in play
     */
    private void jailRoll(int face, Player currentPlayer){
        if (face == 6) {
            convertTextToSpeech("You rolled a" + face);
            convertTextToSpeech("You are now free, resume normal play on next turn");
            currentPlayer.setJail(false);
            nextTurnRoll();
        } else {
            convertTextToSpeech("You rolled a" + face);
            convertTextToSpeech("You did not roll a 6, serve your sentence");
            m_jailCount++;
            if(m_jailCount==3){
                currentPlayer.setJail(false);
                convertTextToSpeech("You have served your sentence. " +
                        "Resume normal play on next turn");
            }
            nextTurnRoll();
        }
    }

    /**
     * Method to set the player to be in jail.
     * @param currentPlayer the current player
     */
    private void setJail(Player currentPlayer){
        currentPlayer.setCurrentPosition(10);
        currentPlayer.setJail(true);
    }

    /**
     * Method called if the player lands on a special square. These include go to jail,
     * income tax, jail and super tax.
     * @param locName the square's name
     * @param currentPlayer the player who is currently playing
     */
    private void specialSquare(String locName, Player currentPlayer){
        if(locName.equals("Go To Jail")){
            setJail(currentPlayer);
            nextTurnRoll();
        } else if (locName.equals("Income Tax")){
            if(m_manageFunds) {
                currentPlayer.subtractMoney(100);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                convertTextToSpeech("100 has been deducted from your funds");
                Log.d("income tax subtract 100", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                m_freeParking += 100;
                Log.d("income tax", "Free parking:" + String.valueOf(m_freeParking));
            } else {
                convertTextToSpeech("You must pay the bank 100 income tax");
            }
            nextTurnRoll();
        } else if (locName.equals("Jail")){
            convertTextToSpeech("Just visiting");
            nextTurnRoll();
        } else if (locName.equals("Super Tax")){
            if(m_manageFunds) {
                convertTextToSpeech("300 has been deducted from your funds");
                currentPlayer.subtractMoney(300);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                Log.d("super tax subtract 300", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                m_freeParking += 300;
                Log.d("super tax", "Free parking:" + String.valueOf(m_freeParking));
            } else {
                convertTextToSpeech("You must pay the bank 300 super tax");
            }
            nextTurnRoll();
        } else {
            convertTextToSpeech("Special square");
            nextTurnRoll();
        }
    }

    /**
     * This method is called if the player lands on a chance square
     * @param currentPlayer the player currently in play
     */
    private void chance(Player currentPlayer) {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;

        if(randomNum == 1){
            convertTextToSpeech("Advance to Bond Street");
            currentPlayer.setCurrentPosition(34);
        }
        if(randomNum == 2){
            convertTextToSpeech("Unpaid charges. Go to jail");
            setJail(currentPlayer);
        }
        if(randomNum == 3){
            convertTextToSpeech("Build a rooftop swimming pool on your apartment, pay 300");
            if(m_manageFunds) {
                currentPlayer.subtractMoney(300);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                m_freeParking += 300;
                Log.d("chance subtract 300", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                Log.d("chance free parking", String.valueOf(m_freeParking));
            }
        }
    }

    /**
     * This method is called if the player lands on a community chest square
     * @param currentPlayer the player currently in play
     */
    private void communityChest(Player currentPlayer){
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;
        int nextPlayer;
        if(m_currentTurn >= m_numPlayers){
            nextPlayer = 0;
        } else {
            nextPlayer = m_currentTurn+1;
        }

        if(randomNum == 1){
            convertTextToSpeech("Your new business takes off, collect 200");
            if(m_manageFunds) {
                Log.d("chance add 200", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                currentPlayer.addMoney(200);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                Log.d("chance add 200", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
            }
        }
        if(randomNum == 2){
            convertTextToSpeech("Your friend hires your villa for a week, " +
                    "collect 100 off the next player");
            if(m_manageFunds) {
                Log.d("chance add 100", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                currentPlayer.addMoney(100);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                Log.d("chance add 100", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));

                Log.d("chance subtract 100", players.get(nextPlayer).getName() +
                        String.valueOf(players.get(nextPlayer).getMoney()));
                players.get(nextPlayer).subtractMoney(100);
                Log.d("chance subtract 100", players.get(nextPlayer).getName() +
                        String.valueOf(players.get(nextPlayer).getMoney()));
            }
        }
        if(randomNum == 3){
            convertTextToSpeech("You receive a tax rebate, collect 300");
            if(m_manageFunds) {
                Log.d("chance add 300", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                currentPlayer.addMoney(300);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                Log.d("chance add 300", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
            }
        }
    }

    /**
     * This method is called if a player lands on a property.
     * First it checks if the property is already owned. If it is the player who owns the property
     * is told to call rent.
     * If the property is not owned the player is asked if they would like to purchase the property.
     * @param currentPlayer the player that is currently in play
     * @param location the square the player has landed on
     */
    private void buyProperty(Player currentPlayer, Square location) {
        String method = "buyProperty";
        PropertySquare square = (PropertySquare)location;

        recognizedSpeech.setText("");
        if(square.getOwned()){
            convertTextToSpeech(square.getOwnedBy() + "call rent");
            //rent is subtracted and added

            if(m_manageFunds) {
                currentPlayer.subtractMoney(100);
                funds.setText(String.valueOf(currentPlayer.getMoney()));
                Log.d(method + "rent subtract money", currentPlayer.getName() +
                        String.valueOf(currentPlayer.getMoney()));
                String owner = square.getOwnedBy();
                for (int i = 0; i < m_numPlayers; i++) {
                    if (players.get(i).getName().equals(owner)) {
                        players.get(i).addMoney(100);
                        Log.d(method + "rent add money", players.get(i).getName() +
                                 String.valueOf(players.get(i).getMoney()));
                    }
                }
            }
            nextTurnRoll();

        } else {
            int price = ((PropertySquare) location).getPrice();
            convertTextToSpeech("Would you like to buy" + square.getName() + "for" + price);

            while (m_tts.isSpeaking()) {
                speech = null;
            }

            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
            speech.startListening(getIntent());

        }
    }

    /**
     * This method is called to prompt the next player to roll
     */
    private void nextTurnRoll() {
        Log.d("nextTurnRoll", "next player please roll");
        m_currentTurn++;
        if(m_currentTurn >= m_numPlayers){
            m_currentTurn = 0;
        }
        convertTextToSpeech(players.get(m_currentTurn).getName() + "please roll the dice");
    }

    /**
     * This method is called when the GamePlay screen is initialised from the intent on the
     * main menu screen.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_tts = new TextToSpeech(this, this);

        Intent newint = getIntent();
        //receive the address of the bluetooth device
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS);

        //view of the ledControl
        setContentView(R.layout.activity_game_play);

        btnEnd = (Button)findViewById(R.id.btnEnd);

        new ConnectBT().execute(); //Call the class to connect

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

    }

    /**
     * This is the method that initialises the games with the players and the board.
     */
    public void game() {

        String method = "game";
        m_board = new Board();
        players = new ArrayList<>();
        m_currentTurn = 0;

        Intent intent = getIntent();

        m_manageFunds = intent.getBooleanExtra(MainActivity.MANAGE_FUNDS,false);

        if(m_manageFunds){
            Log.d(method, "Application is managing funds");
        } else {
            funds = (TextView) findViewById(R.id.funds);
            funds.setVisibility(View.INVISIBLE);
        }

        String player1name = intent.getStringExtra(MainActivity.PLAYER1);
        String player2name = intent.getStringExtra(MainActivity.PLAYER2);
        Player player1 = new Player(player1name);
        Player player2 = new Player(player2name);
        players.add(player1);
        players.add(player2);

        if(intent.getExtras().size()>4){
            String player3name = intent.getStringExtra(MainActivity.PLAYER3);
            Player player3 = new Player(player3name);
            players.add(player3);
        }
        if(intent.getExtras().size()>5){
            String player4name = intent.getStringExtra(MainActivity.PLAYER4);
            Player player4 = new Player(player4name);
            players.add(player4);
        }

        m_numPlayers = players.size();

        Log.d(method, "*****START GAME*****");
        Log.d(method, "numPlayers: " + m_numPlayers);

        Player current = players.get(m_currentTurn);
        int pos = current.getCurrentPosition();
        player = (TextView) findViewById(R.id.player);
        player.setText(current.getName());
        Log.d(method, "*****IT IS " + current.getName().toUpperCase() + "'S TURN*****");
        Log.d(method, "Current Position:" + pos + ", " + m_board.getSquare(pos).getName());

        currentPosition = (TextView) findViewById(R.id.currentPosition);
        currentPosition.setText("Current Position: " + pos + ", " +
                m_board.getSquare(pos).getName());
        convertTextToSpeech("Start Game" + players.get(m_currentTurn).getName() +
                "please roll the dice");

        // Initiating
        BluetoothManipulator.initiate(this);
        DiceController.initiate(developerKey);

        // Every time the application starts it will search for a dice
        BluetoothManipulator.registerDiceScanningListener(scanningListener);
        DiceController.registerDiceConnectionListener(connectionListener);
        DiceController.registerDiceResponseListener(responseListener);

        BluetoothManipulator.startScan();

    }

    /**
     * This method is called when the TextToSpeech Engine is initialised
     * @param code
     */
    @Override
    public void onInit(int code) {
        if (code==TextToSpeech.SUCCESS) {
            m_tts.setLanguage(Locale.US);
        } else {
            m_tts = null;
            msg("Failed to initialize TTS engine");
        }
    }

    /**
     * This method is called when the TextToSpeech Engine is shut down
     */
    @Override
    protected void onDestroy() {
        if (m_tts!=null) {
            m_tts.stop();
            m_tts.shutdown();
        }
        super.onDestroy();
    }

    /**
     * This method is called when the Speech Recognizer starts to listen for speech input
     */
    @Override
    public void onBeginningOfSpeech() {
        Log.i("SRL", "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i("SRL", "onBufferReceived: " + buffer);
    }

    /**
     * This method is called after the speech input has been completed.
     */
    @Override
    public void onEndOfSpeech() {
        Log.i("SRL", "onEndOfSpeech");
    }

    /**
     * This method is called if there has been an error during speech input
     * @param errorCode
     */
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d("SRL", "FAILED " + errorMessage);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        speech.startListening(getIntent());
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i("SRL", "onEvent");
    }

    /**
     * This method is called if the speech recognizer thinks only partial speech was
     * input/recognized
     * @param arg0
     */
    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i("SRL", "onPartialResults");
    }

    /**
     * This method is called when the speech recognizer is ready for input
     * @param arg0
     */
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i("SRL", "onReadyForSpeech");
    }

    /**
     * This method is called when the speech recognizer has recieved input and recognized it.
     * It updates the recognized speech text view on the screen to show users what they have input.
     * @param results the text that has been input
     */
    @Override
    public void onResults(Bundle results) {
        recognizedSpeech = (TextView) findViewById(R.id.recognizedSpeech);
        Log.i("SRL", "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        recognizedSpeech.setText(text);

        if(!(recognizedSpeech.getText().toString().contains("yes")) &&
                !(recognizedSpeech.getText().toString().contains("no"))){
            convertTextToSpeech("A valid response was not detected, please try again.");

            while (m_tts.isSpeaking()) {
                speech = null;
            }

            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
            speech.startListening(getIntent());

        } else if(recognizedSpeech.getText().toString().contains("yes") &&
                recognizedSpeech.getText().toString().contains("no")){
            convertTextToSpeech("Both yes and no were detected, please only specify one decision");

            while (m_tts.isSpeaking()) {
                speech = null;
            }

            speech = SpeechRecognizer.createSpeechRecognizer(this);
            speech.setRecognitionListener(this);
            speech.startListening(getIntent());

        } else if (recognizedSpeech.getText().toString().contains("yes")) {
            PropertySquare square = (PropertySquare) (m_board.getSquare(
                    players.get(m_currentTurn).getCurrentPosition()));
            square.setOwnedBy(players.get(m_currentTurn).getName());
            convertTextToSpeech("You now own" + square.getName());
            Log.d("buyProperty yes", square.getOwnedBy());

            if (m_manageFunds) {
                players.get(m_currentTurn).subtractMoney(square.getPrice());
                Log.d("buyProperty yes", players.get(m_currentTurn).getName() +
                        String.valueOf(players.get(m_currentTurn).getMoney()));
                 funds.setText(String.valueOf(players.get(m_currentTurn).getMoney()));
            }
            nextTurnRoll();
        } else if (recognizedSpeech.getText().toString().contains("no")) {
            nextTurnRoll();
        }
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i("SRL", "onRmsChanged: " + rmsdB);
    }

    /**
     * This method returns what error was caused if the speech recgonizer throws an error code
     * @param errorCode int representing the error thrown
     * @return log message including error details
     */
    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    /**
     * This method is launched when the application is resumed from previous gameplay
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        game();
    }

    /**
     * This method is called when the application is closed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        BluetoothManipulator.unregisterDiceScanningListener(scanningListener);
        DiceController.unregisterDiceConnectionListener(connectionListener);
        DiceController.unregisterDiceResponseListener(responseListener);

        DiceController.disconnectDie(dicePlus);
        dicePlus = null;
    }

    /**
     * This method is called to disconnect from the bluetooth devices connected.
     */
    private void Disconnect() {
        //If the btSocket is busy
        if (btSocket!=null) {
            try {
                btSocket.close(); //close connection
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }

    /**
     * This method provides a quicker way to create Toasts on the screen
     * @param s the message to be included in the toast
     */
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
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

    /**
     * This method is called to connect to the bluetooth device (Arduino)
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void>  {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute(){
            //show a progress dialog
            progress = ProgressDialog.show(GamePlay.this, "Connecting...", "Please wait!!!");
        }

        /**
         * This method is completed in the background to check connection to the bluetooth device
         * @param devices
         * @return
         */
        @Override
        protected Void doInBackground(Void... devices){
            try {
                if (btSocket == null || !isBtConnected) {
                    //get the mobile bluetooth device
                    m_Bluetooth = BluetoothAdapter.getDefaultAdapter();
                    //connects to the device's address and checks if it's available
                    BluetoothDevice bluetoothDevice = m_Bluetooth.getRemoteDevice(address);
                    //create a RFCOMM (SPP) connection
                    btSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        /**
         * This method is completed after the doInBackground method to ensure everything is ok
         * @param result result of doInBackground
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
