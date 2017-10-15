package com.drd.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author darrenp - 2017-10-06.
 */
public class IOUtils {
    private static InputStreamReader istream;

    private static BufferedReader bufRead;

    static {
        istream = new InputStreamReader(System.in) ;

        bufRead = new BufferedReader(istream) ;
    }

    public static String readLine(String s) {
        System.out.print(s);
        String returnval = null;
        try {
            returnval =  bufRead.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnval;
    }
    public static int readInt(String s) {
        System.out.print(s);
        int returnval=0;

        try {
            String txt = bufRead.readLine();
            returnval = Integer.parseInt(txt);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnval;
    }

}