package com.zoctan.smarthub;

import java.util.Base64;

public class ApplicationTest {
    public static void main(final String[] args) {
        final String plain = "19959358 AB:CD:EF:GH:IJ:KL";
        try {
            final String asB64 = Base64.getEncoder().encodeToString(plain.getBytes("utf-8"));

            System.out.println(asB64);
            // MTk5NTkzNTggQUI6Q0Q6RUY6R0g6SUo6S0w=
            final byte[] asBytes = Base64.getDecoder().decode(asB64);
            System.out.println(new String(asBytes, "utf-8"));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}