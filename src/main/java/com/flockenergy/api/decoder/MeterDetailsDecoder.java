
package com.flockenergy.api.decoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flockenergy.api.model.MeterDetails;
import com.flockenergy.api.model.MeterDetailsResponse;
import com.flockenergy.api.model.MeterHierarchy;
import org.springframework.stereotype.Component;

@Component
public class MeterDetailsDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MeterDetailsResponse decode(String rawJson)
            throws JsonProcessingException {

        JsonNode root = objectMapper.readTree(rawJson);
        JsonNode indexedData = root.path("nodes").get(2).path("data");

        JsonNode meter = indexedData.get(0);

        int detailIndex = meter.path("detail").asInt();
        int detailDataIndex = indexedData.get(detailIndex)
                .path("data").asInt();

        JsonNode detailReferences = indexedData.get(detailDataIndex);

        String meterId = "";
        String serialNo = "";
        String make = "";
        String phaseType = "";
        String installStatus = "";
        String installationType = "";

        for (JsonNode referenceIndexNode : detailReferences) {
            JsonNode reference =
                    indexedData.get(referenceIndexNode.asInt());

            String name = indexedData
                    .get(reference.path("parameterName").asInt())
                    .asText();

            String value = indexedData
                    .get(reference.path("parameterValue").asInt())
                    .asText();

            switch (name) {
                case "Meter ID" -> meterId = value;
                case "Serial No" -> serialNo = value;
                case "Make" -> make = value;
                case "Phase Type" -> phaseType = value;
                case "Installation Status" -> installStatus = value;
                case "Installation Type" -> installationType = value;
            }
        }

        int hierarchyIndex = meter.path("hierarchy").asInt();
        JsonNode hierarchy = indexedData.get(hierarchyIndex);

        MeterDetails details = new MeterDetails(
                meterId,
                serialNo,
                make,
                phaseType,
                installStatus,
                installationType
        );

        MeterHierarchy meterHierarchy = new MeterHierarchy(
                getIndexedValue(indexedData, hierarchy, "Zone"),
                getIndexedValue(indexedData, hierarchy, "Circle"),
                getIndexedValue(indexedData, hierarchy, "Division"),
                getIndexedValue(indexedData, hierarchy, "Subdivision"),
                getIndexedValue(indexedData, hierarchy, "Sub Station"),
                getIndexedValue(indexedData, hierarchy, "Feeder"),
                getIndexedValue(indexedData, hierarchy, "DT")
        );

        return new MeterDetailsResponse(details, meterHierarchy);
    }

    private String getIndexedValue(
            JsonNode indexedData,
            JsonNode object,
            String field) {

        int valueIndex = object.path(field).asInt();
        return indexedData.get(valueIndex).asText();
    }
}
