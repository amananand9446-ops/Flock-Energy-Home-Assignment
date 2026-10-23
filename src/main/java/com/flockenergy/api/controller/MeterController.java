
package com.flockenergy.api.controller;

import com.flockenergy.api.client.PortalClient;
import com.flockenergy.api.model.MeterDetailsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final PortalClient portalClient;

    public MeterController(PortalClient portalClient) {
        this.portalClient = portalClient;
    }

    @GetMapping
    public String getMeters(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "1") int page) {
        return portalClient.searchMeters(q, page);
    }

    @GetMapping("/{meterId}")
    public MeterDetailsResponse getMeterDetails(
            @PathVariable String meterId) {
        return portalClient.getMeterDetails(meterId);
    }

    @GetMapping("/{meterId}/energy")
    public String getMeterEnergy(
            @PathVariable String meterId) {
        return portalClient.getMeterEnergy(meterId);
    }

    @GetMapping("/{meterId}/location")
    public String getMeterLocation(
            @PathVariable String meterId) {
        return portalClient.getMeterLocation(meterId);
    }
}
