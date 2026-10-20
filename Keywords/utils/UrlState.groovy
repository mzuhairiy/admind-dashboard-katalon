package utils

import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** Reads query-state assertions without depending on query parameter order. */
class UrlState {
    static String firstQueryValue(String url, String key) {
        URI uri = new URI(url)
        if (!uri.rawQuery) {
            return null
        }
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8.name())
        String firstMatch = uri.rawQuery.split('&').find { entry -> entry.split('=', 2)[0] == encodedKey }
        if (!firstMatch) {
            return null
        }
        String[] pair = firstMatch.split('=', 2)
        return pair.length == 1 ? '' : URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name())
    }
}
