package com.hankcs.hanlp;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RunString {

    static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
     * @param args
     */
    public static void main(String[] args) {
        String strs = "1+1*2+(10-(2*(5-3)*(2-1))-4)+10/(5-0)";
        String s = runString(strs);
        System.out.println(s);
    }


    public static String runString(String string){
        try {
            return jse.eval(string).toString();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return string;
    }
}