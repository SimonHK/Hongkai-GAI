package com.graphai.drivers.Proxy;

public class Tools {

        public static int toRodom(int Max, int min) {
            int i = (int) (min + Math.random() * (Max - min + 1));
            return i;
        }

}
