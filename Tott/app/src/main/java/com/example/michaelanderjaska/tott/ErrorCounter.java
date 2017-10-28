package com.example.michaelanderjaska.tott;

/**
 * Created by michaelanderjaska on 3/26/17.
 */

public class ErrorCounter {
    int count;

    public ErrorCounter(){
        count = 0;

    }

    public void inc(){ count++;}

    public boolean quitNow(){
        return count > 2;
    }



}
