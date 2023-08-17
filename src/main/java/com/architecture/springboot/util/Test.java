package com.architecture.springboot.util;

public class Test {
    public static void main(String[] args) {
        String a = "https://opmay.s3.ap-northeast-2.amazonaws.com/보정본.jpg";
        System.out.printf(a.substring(a.lastIndexOf("/") + 1));
    }
}
