package xyz.icanfly.websocket.websocket.util;

import xyz.icanfly.websocket.websocket.handshake.WebSocketFullHttpRequest;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocket13FrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketScheme;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import io.netty.util.internal.PlatformDependent;

import java.net.URI;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * copy from io.netty.handler.codec.http.websocketx{#WebSocketUtil}
 *
 * @author yang
 */
public class WebSocketUtil {
    private static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
    private static final String HTTP_SCHEME_PREFIX = HttpScheme.HTTP + "://";
    private static final String HTTPS_SCHEME_PREFIX = HttpScheme.HTTPS + "://";

    public static byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        PlatformDependent.threadLocalRandom().nextBytes(bytes);
        return bytes;
    }

    public static WebSocketFullHttpRequest newUpgradeRequest(String socketAddress){
        try {
            URI uri = new URI(socketAddress);
            return newUpgradeRequest(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static WebSocketFullHttpRequest newUpgradeRequest(URI uri) {
        String expectedChallengeResponseString;
        URI wsURL = uri;
        byte[] nonce = WebSocketUtil.randomBytes(16);
        String key = WebSocketUtil.base64(nonce);
        String acceptSeed = key + MAGIC_GUID;
        byte[] sha1 = WebSocketUtil.sha1(acceptSeed.getBytes(CharsetUtil.US_ASCII));
        expectedChallengeResponseString = WebSocketUtil.base64(sha1);
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, upgradeUrl(wsURL),
                Unpooled.EMPTY_BUFFER);

        HttpHeaders headers = request.headers();
        headers.add(new DefaultHttpHeaders());
        headers.set(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET)
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
                .set(HttpHeaderNames.SEC_WEBSOCKET_KEY, key)
                .set(HttpHeaderNames.HOST, websocketHostValue(wsURL));

        if (!headers.contains(HttpHeaderNames.ORIGIN)) {
            headers.set(HttpHeaderNames.ORIGIN, websocketOriginValue(wsURL));
        }
        headers.set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, "13");

        return new WebSocketFullHttpRequest(request,expectedChallengeResponseString);
    }

    private static CharSequence websocketOriginValue(URI wsURL) {
        String scheme = wsURL.getScheme();
        final String schemePrefix;
        int port = wsURL.getPort();
        final int defaultPort;
        if (WebSocketScheme.WSS.name().contentEquals(scheme)
                || HttpScheme.HTTPS.name().contentEquals(scheme)
                || (scheme == null && port == WebSocketScheme.WSS.port())) {

            schemePrefix = HTTPS_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WSS.port();
        } else {
            schemePrefix = HTTP_SCHEME_PREFIX;
            defaultPort = WebSocketScheme.WS.port();
        }
        String host = wsURL.getHost().toLowerCase(Locale.US);
        if (port != defaultPort && port != -1) {
            return schemePrefix + NetUtil.toSocketAddressString(host, port);
        }
        return schemePrefix + host;
    }

    private static CharSequence websocketHostValue(URI wsURL) {
        int port = wsURL.getPort();
        if (port == -1) {
            return wsURL.getHost();
        }
        String host = wsURL.getHost();
        String scheme = wsURL.getScheme();
        if (port == HttpScheme.HTTP.port()) {
            return HttpScheme.HTTP.name().contentEquals(scheme)
                    || WebSocketScheme.WS.name().contentEquals(scheme) ?
                    host : NetUtil.toSocketAddressString(host, port);
        }
        if (port == HttpScheme.HTTPS.port()) {
            return HttpScheme.HTTPS.name().contentEquals(scheme)
                    || WebSocketScheme.WSS.name().contentEquals(scheme) ?
                    host : NetUtil.toSocketAddressString(host, port);
        }
        return NetUtil.toSocketAddressString(host, port);
    }

    private static String upgradeUrl(URI wsURL) {
        String path = wsURL.getRawPath();
        String query = wsURL.getRawQuery();
        if (query != null && !query.isEmpty()) {
            path = path + '?' + query;
        }
        return path == null || path.isEmpty() ? "/" : path;
    }

    public static String base64(byte[] nonce) {
        return java.util.Base64.getEncoder().encodeToString(nonce);
    }

    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.reset();
            return digest.digest(bytes);
        } catch (Exception e) {
            throw new InternalError("SHA-1 not supported on this platform");
        }
    }

    public static void verify(FullHttpResponse response,String expectedResponseString){
        final HttpResponseStatus status = HttpResponseStatus.SWITCHING_PROTOCOLS;
        final HttpHeaders headers = response.headers();

        if (!response.status().equals(status)) {
            throw new WebSocketHandshakeException("Invalid handshake response getStatus: " + response.status());
        }

        CharSequence upgrade = headers.get(HttpHeaderNames.UPGRADE);
        if (!HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(upgrade)) {
            throw new WebSocketHandshakeException("Invalid handshake response upgrade: " + upgrade);
        }

        if (!headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            throw new WebSocketHandshakeException("Invalid handshake response connection: "
                    + headers.get(HttpHeaderNames.CONNECTION));
        }

        CharSequence accept = headers.get(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT);
        if (accept == null || !accept.equals(expectedResponseString)) {
            throw new WebSocketHandshakeException(
                String.format("Invalid challenge. Actual: %s. Expected: %s", accept, expectedResponseString));
        }
    }

    public static WebSocket13FrameDecoder newWebsocketDecoder(){
        return new WebSocket13FrameDecoder(false, true, 65536, false);
    }

}
