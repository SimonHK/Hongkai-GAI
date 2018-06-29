package com.graphai.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {


    public static String stringToMsqlMD5(String inStr){
        //inStr = "邵武市银塘沙石土开采有限公司";	//需要加密的字符串
        String md5_32 = "";
        try {
            byte[] c = inStr.getBytes();
            //生成md5加密算法照耀
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int j = 0; j < b.length; j++) {
                i = b[j];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }


            md5_32 = buf.toString();		//32位加密
            //String md5_16 = buf.toString().substring(8, 24);	//16位加密

            //System.out.println("MD5(" + sourceStr + ",32) = " + md5_32);
            //System.out.println("MD5(" + sourceStr + ",16) = " + md5_16);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return md5_32;

    }

    public static void main(String[] args) {

        String sourceStr = "邵武市银塘沙石土开采有限公司";	//需要加密的字符串
        try {
            byte[] c = sourceStr.getBytes();
            //生成md5加密算法照耀
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int j = 0; j < b.length; j++) {
                i = b[j];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }


            String md5_32 = buf.toString();		//32位加密
            String md5_16 = buf.toString().substring(8, 24);	//16位加密

            System.out.println("MD5(" + sourceStr + ",32) = " + md5_32);
            System.out.println("MD5(" + sourceStr + ",16) = " + md5_16);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
    }

}
