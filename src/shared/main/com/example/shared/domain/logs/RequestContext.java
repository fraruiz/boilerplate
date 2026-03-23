package com.example.shared.domain.logs;

import org.slf4j.MDC;

public final class RequestContext {
    public static final String REQUEST_ID = "requestId";
    public static final String METHOD = "method";
    public static final String PATH = "path";

    private RequestContext() {}

    public static void set(String requestId, String method, String path) {
        MDC.put(REQUEST_ID, requestId);
        MDC.put(METHOD, method);
        MDC.put(PATH, path);
    }

    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    public static void clear() {
        MDC.clear();
    }
}
