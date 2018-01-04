package my.edu.tarc.assignment.TopUp;

import java.util.Random;

/**
 * Created by JE on 12/14/2017.
 */

public class RandomNumberGenerator {
    private int randNo=0;

    public int randomNumberGenerator(){
        Random rand = new Random();

        return randNo = rand.nextInt(90000)+10000;
    }
}
