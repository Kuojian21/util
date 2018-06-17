package com.test.clazz;

public class JarPathTest {
    public static void main(String[] args){
        System.out.println(JarPathTest.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
}
