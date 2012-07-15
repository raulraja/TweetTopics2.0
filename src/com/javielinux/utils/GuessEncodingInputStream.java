package com.javielinux.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GuessEncodingInputStream extends FilterInputStream {
    private static final int HEAD_BUF_SIZE = 2048;
    private byte headBuffer[];
    private int bufConsumed;
    private int bufFilled;
    private InputStream in;

    public GuessEncodingInputStream(InputStream in) throws IOException {
        super(in);
        headBuffer = new byte[HEAD_BUF_SIZE];
        this.in = in;
        while (bufFilled < headBuffer.length) {
            int n = headBuffer.length - bufFilled;
            n = in.read(headBuffer, bufFilled, n);
            if (n <= 0) {
                break;
            } else {
                bufFilled += n;
            }
        }
    }

    public String guess() {
        byte b[] = headBuffer;
        String head = new String(b);
        
        final String[] listEncodings = {"UTF-8", "ISO-8859-1", "UTF-16", "ISO-8859-15", "ISO-8859-16"};
        for (String encoding : listEncodings) {
        	String inXML = "ENCODING=\"" + encoding + "\"";
        	String inXML_sim = "ENCODING='" + encoding + "'";
        	String inHTML = "CHARSET=" + encoding;
        	if ( (head.toUpperCase().indexOf(inXML) > -1) || (head.toUpperCase().indexOf(inXML_sim) > -1) || (head.toUpperCase().indexOf(inHTML) > -1)) {
        		return encoding;
        	}
        }
        return null;
    }

    public boolean  markSupported() {
        return false;
    }

    public int available() throws IOException {
        int av = (bufFilled - bufConsumed);
        if (av > 0) {
            return av;
        }
        return in.available();
    }

    public int read(byte[] buffer, int offset, int count) throws IOException {
        int av = (bufFilled - bufConsumed);
        if (av > 0) {
            if (count > av) {
                count = av;
            }
            System.arraycopy(headBuffer, bufConsumed, buffer, offset, count);
            bufConsumed += count;
            return count;
        }
        return in.read(buffer, offset, count);
    }

    public long skip(long count) throws IOException {
        long skipped = 0;
        int av = (bufFilled - bufConsumed);
        if (av > 0) {
            if ((long)av >= count) {
                bufConsumed += (int)count;
                return count;
            }
            bufConsumed += av;
            count -= av;
            skipped = av;
        }
        return skipped + in.skip(count);
    }

    public int read() throws IOException {
        int av = (bufFilled - bufConsumed);
        if (av > 0) {
            int r = (int)headBuffer[bufConsumed];
            bufConsumed ++;
            return r;
        }
        return in.read();
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }
    public void close() throws IOException {
        in.close();
    }
}
