package com.selcukaloba.to_do_api_project.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IdEncoder {
    public static String encode(Long id) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
    }

    public static Long decode(String encodedId) {
        byte[] decoded = Base64.getUrlDecoder().decode(encodedId);
        return Long.valueOf(new String(decoded, StandardCharsets.UTF_8));
    }
}
