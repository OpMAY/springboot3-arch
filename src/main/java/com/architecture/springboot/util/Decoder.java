package com.architecture.springboot.util;

import lombok.extern.log4j.Log4j2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;

@Log4j2
public class Decoder {
    public static String fileNameDecoder(String filename) {
        try {
            filename = normalizeNfc(URLDecoder.decode(filename, "UTF-8"));
            return filename;
        } catch (UnsupportedEncodingException e) {
            log.error(e);
            throw new RuntimeException();
        }
    }

    protected static String normalizeNfc(String unNormalMailBoxName) {
        if (!Normalizer.isNormalized(unNormalMailBoxName, Normalizer.Form.NFC)) {
            return Normalizer.normalize(unNormalMailBoxName, Normalizer.Form.NFC);
        }
        return unNormalMailBoxName;
    }
}
