package students.aalto.org.indoormappingapp.deadreckoning;

import android.util.Pair;
import java.util.Random;

/**
 * Created by olli-mattisaario on 3.2.16.
 */
public class DeadReckoning {
    public static Pair<Integer, Integer> calculatePositionDelta(Integer oldX, Integer oldY, Integer intervalMS, Object sensorInput) {
        Integer x = new Random().nextInt(intervalMS) - intervalMS/2;
        Integer y = new Random().nextInt(intervalMS) - intervalMS/2;

        return new Pair<>(oldX + x / 10, oldY + y / 10);

    }
}
