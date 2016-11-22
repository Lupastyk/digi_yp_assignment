package com.digicode.assignment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

public class GeoPosition {
    @DataField(pos = 4, precision = 13, decimalSeparator = ".", pattern = "###.#####", required = true)
    private final BigDecimal latitude;
    @DataField(pos = 5, precision = 13, decimalSeparator = ".", pattern = "###.#####", required = true)
    private final BigDecimal longitude;

    @JsonCreator
    public GeoPosition(@JsonProperty("latitude") BigDecimal latitude,
                       @JsonProperty("longitude") BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .toString();
    }
}
