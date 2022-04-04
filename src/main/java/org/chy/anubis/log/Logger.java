package org.chy.anubis.log;

import java.io.OutputStream;
import java.io.Writer;

public class Logger {

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void waring(String msg) {
        System.out.println(msg);
    }

    public static void error(String msg) {
        System.out.println(msg);
    }


    public static OutputStream printWriter(){
        return System.out;
    }
}
