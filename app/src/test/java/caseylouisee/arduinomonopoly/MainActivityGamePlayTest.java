package caseylouisee.arduinomonopoly;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import android.content.SharedPreferences;

/**
 * Created by caseylouisee on 20/04/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainActivityGamePlayTest {

    @Mock
    Context mMockContext;

    /**
     * This test ensures the game method works as expected.
     * @throws Exception
     */
    @Test
    public void game_test() throws Exception {

        //MainActivity mainActivity = new MainActivity(m);
        Intent i = new Intent(mMockContext, GamePlay.class);

        i.putExtra("EXTRA_ADDRESS", "BTAddress");
        i.putExtra("MANAGE_FUNDS", false);
        i.putExtra("PLAYER1", "player1");
        i.putExtra("PLAYER2", "player2");

        mMockContext.startActivity(i);

    }
}
