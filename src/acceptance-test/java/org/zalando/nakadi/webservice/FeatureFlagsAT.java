package org.zalando.nakadi.webservice;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.Test;
import org.zalando.nakadi.domain.EventType;
import org.zalando.nakadi.service.FeatureToggleService;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.zalando.nakadi.utils.TestUtils.buildDefaultEventType;

public class FeatureFlagsAT extends BaseAT {

    private static final String ENDPOINT = "/settings/features";

    @Test
    public void whenDbWriteOperationsInactiveThen503s() throws Exception {
        enableFeature(FeatureToggleService.Feature.DISABLE_DB_WRITE_OPERATIONS);
        try {
            final EventType eventType = buildDefaultEventType();
            final String body = MAPPER.writer().writeValueAsString(eventType);
            given()
                    .body(body)
                    .header("accept", "application/json")
                    .contentType(JSON)
                    .post("/event-types")
                    .then()
                    .statusCode(HttpStatus.SC_SERVICE_UNAVAILABLE);


        } finally {
            disableFeature(FeatureToggleService.Feature.DISABLE_DB_WRITE_OPERATIONS);
        }

    }

    private void enableFeature(FeatureToggleService.Feature feature) {
        JSONObject payload = new JSONObject();
        payload.put("feature", feature.getId());
        payload.put("enabled", true);
        given()
                .header("accept", "application/json")
                .contentType(JSON)
                .body(payload.toString())
                .put(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    private void disableFeature(FeatureToggleService.Feature feature) {
        JSONObject payload = new JSONObject();
        payload.put("feature", feature.getId());
        payload.put("enabled", false);
        given()
                .header("accept", "application/json")
                .contentType(JSON)
                .body(payload.toString())
                .put(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
