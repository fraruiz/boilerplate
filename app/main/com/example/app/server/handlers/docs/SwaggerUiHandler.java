package com.example.app.server.handlers.docs;

import com.example.app.server.handlers.RequestHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;

public final class SwaggerUiHandler extends RequestHandler {
    private static final String HTML = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Swagger UI</title>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist/swagger-ui.css">
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="https://unpkg.com/swagger-ui-dist/swagger-ui-bundle.js"></script>
                <script>
                    SwaggerUIBundle({
                        url: "/api-docs",
                        dom_id: '#swagger-ui',
                        presets: [
                            SwaggerUIBundle.presets.apis,
                            SwaggerUIBundle.SwaggerUIStandalonePreset
                        ],
                        layout: "BaseLayout"
                    });
                </script>
            </body>
            </html>
            """;

    @Override
    public void handle(Context ctx) {
        ctx.contentType("text/html");
        ctx.result(HTML);
        ctx.status(HttpStatus.OK);
    }

    @Override
    public String path() {
        return "/swagger";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }
}
