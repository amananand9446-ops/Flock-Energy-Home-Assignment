
package com.flockenergy.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flockenergy.api.decoder.MeterDetailsDecoder;
import com.flockenergy.api.model.MeterDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

@Component
public class PortalClient {

    private final RestClient restClient;
    private final MeterDetailsDecoder meterDetailsDecoder =
            new MeterDetailsDecoder();

    public PortalClient(
            RestClient.Builder builder,
            @Value("${flock.portal.base-url}") String baseUrl,
            @Value("${flock.portal.session-cookie:}") String sessionCookie) {

        RestClient.Builder configuredBuilder = builder.baseUrl(baseUrl);

        if (sessionCookie != null && !sessionCookie.isBlank()) {
            configuredBuilder.defaultHeader("Cookie", sessionCookie);
        }

        this.restClient = configuredBuilder.build();
    }

    public String searchMeters(String q, int page) {
        return executePortalRequest(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/portal/meters/search")
                        .queryParam("q", q)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(String.class));
    }

    public String getMeterEnergy(String meterId) {
        return executePortalRequest(() -> restClient.get()
                .uri("/portal/meters/{meterId}/energy", meterId)
                .retrieve()
                .body(String.class));
    }

    public String getMeterLocation(String meterId) {
        return executePortalRequest(() -> restClient.get()
                .uri("/portal/meters/{meterId}/geo", meterId)
                .retrieve()
                .body(String.class));
    }

    public MeterDetailsResponse getMeterDetails(String meterId) {
        String rawJson = executePortalRequest(() -> restClient.get()
                .uri("/meters/{meterId}/__data.json", meterId)
                .retrieve()
                .body(String.class));

        try {
            return meterDetailsDecoder.decode(rawJson);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "The portal returned an unexpected meter-details response.",
                    e);
        }
    }

    private String executePortalRequest(
            java.util.function.Supplier<String> request) {

        try {
            return request.get();
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 401) {
                throw new IllegalStateException(
                        "Portal authentication failed. Refresh the portal session cookie.",
                        e);
            }

            if (e.getStatusCode().value() == 403) {
                throw new IllegalStateException(
                        "The portal denied access to this resource.",
                        e);
            }

            throw new IllegalStateException(
                    "The portal returned HTTP " + e.getStatusCode().value() + ".",
                    e);
        } catch (ResourceAccessException e) {
            throw new IllegalStateException(
                    "The portal could not be reached. Check your network connection.",
                    e);
        }
    }
}
