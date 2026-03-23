package com.example.app.server.handlers.docs;

import com.example.app.server.handlers.RequestHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.eclipse.jetty.http.HttpMethod;

public final class ApiDocsHandler extends RequestHandler {
    private static final String SPEC = buildSpec();

    @Override
    public void handle(Context ctx) {
        ctx.contentType("application/json");
        ctx.result(SPEC);
        ctx.status(HttpStatus.OK);
    }

    @Override
    public String path() {
        return "/api-docs";
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.GET;
    }

    private static String buildSpec() {
        OpenAPI spec = new OpenAPI()
            .info(new Info()
                .title("Boilerplate API")
                .version("1.0.0"))
            .paths(new Paths()
                .addPathItem("/health-check", new PathItem()
                    .get(new Operation()
                        .operationId("healthCheck")
                        .summary("Health check")
                        .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse()
                                .description("Service is healthy"))))));

        return Json.pretty(spec);
    }
}
