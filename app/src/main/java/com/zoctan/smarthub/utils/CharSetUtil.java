package com.zoctan.smarthub.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharSetUtil {

    /**
     * 解码 Unicode \uXXXX
     *
     * @param str
     * @return
     */
    public static String decodeUnicode(final String str) {
        final Charset set = Charset.forName("UTF-16");
        final Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        final Matcher m = p.matcher(str);
        int start = 0;
        int start2;
        final StringBuilder sb = new StringBuilder();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                final String seg = str.substring(start, start2);
                sb.append(seg);
            }
            final String code = m.group(1);
            final int i = Integer.valueOf(code, 16);
            final byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            final ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            final String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }
}
