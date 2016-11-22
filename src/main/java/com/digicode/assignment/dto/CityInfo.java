package com.digicode.assignment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.camel.dataformat.bindy.annotation.Link;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@CsvRecord(separator = ",")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityInfo {
    @DataField(pos = 1, required = true)
    private final Long id;
    private final String key;
    @DataField(pos = 2, required = true)
    private final String name;
    private final String fullName;
    private final String iataAirportCode;
    @DataField(pos = 3, required = true)
    private final String type;
    private final String country;
    @Link
    private final GeoPosition geoPosition;
    private final Long locationId;
    private final Boolean inEurope;
    private final String countryCode;
    private final Boolean coreCountry;
    private final Double distance;


    @JsonCreator
    public CityInfo(@JsonProperty("_id") Long id,
                    @JsonProperty("key") String key,
                    @JsonProperty("name") String name,
                    @JsonProperty("fullName") String fullName,
                    @JsonProperty("iata_airport_code") String iataAirportCode,
                    @JsonProperty("type") String type,
                    @JsonProperty("country") String country,
                    @JsonProperty("geo_position") GeoPosition geoPosition,
                    @JsonProperty("locationId") Long locationId,
                    @JsonProperty("inEurope") Boolean inEurope,
                    @JsonProperty("countryCode") String countryCode,
                    @JsonProperty("coreCountry") Boolean coreCountry,
                    @JsonProperty("distance") Double distance) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.fullName = fullName;
        this.iataAirportCode = iataAirportCode;
        this.type = type;
        this.country = country;
        this.geoPosition = geoPosition;
        this.locationId = locationId;
        this.inEurope = inEurope;
        this.countryCode = countryCode;
        this.coreCountry = coreCountry;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getIataAirportCode() {
        return iataAirportCode;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public GeoPosition getGeoPosition() {
        return geoPosition;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Boolean getInEurope() {
        return inEurope;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Boolean getCoreCountry() {
        return coreCountry;
    }

    public Double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("key", key)
                .append("name", name)
                .append("fullName", fullName)
                .append("iataAirportCode", iataAirportCode)
                .append("type", type)
                .append("country", country)
                .append("geoPosition", geoPosition)
                .append("locationId", locationId)
                .append("inEurope", inEurope)
                .append("countryCode", countryCode)
                .append("coreCountry", coreCountry)
                .append("distance", distance)
                .toString();
    }
}
