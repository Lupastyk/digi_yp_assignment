package com.digicode.assignment.context;

import com.digicode.assignment.dto.CityInfo;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class CamelConfiguration {
    private static final Logger LOGGER = getLogger(CamelConfiguration.class);

    public static final String HEADER_CITY_NAME = "CITY_NAME";
    public static final String GET_FO_EURO_INFO_ROUTE_ID = "getGoEuroInfo";

    @Value("${goeuro.api.url}")
    private String goEuroEndpoint;

    @Bean
    public RouteBuilder routeBuilder(final DataFormat goEuroDataFormat, final DataFormat outputDataFormat,
                                     final Processor exitProcessor, final Processor errorPrcocessor) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class)
                        .process(errorPrcocessor)
                        .handled(true)
                        .process(exitProcessor)
                        .end();

                from("direct:start")
                        .setHeader(HEADER_CITY_NAME, header(HEADER_CITY_NAME).regexReplaceAll("\"", ""))
                        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                        .toD(goEuroEndpoint + "${header.CITY_NAME}")
                        .unmarshal(goEuroDataFormat)
                        .marshal(outputDataFormat)
                        .to("file:./?fileName=${header.CITY_NAME}.txt")
                        .process(exitProcessor)
                        .end()
                        .setId(GET_FO_EURO_INFO_ROUTE_ID);
            }
        };
    }

    @Bean
    public DataFormat outputDataFormat() {
        return new BindyCsvDataFormat(CityInfo.class);

    }

    @Bean
    public DataFormat goEuroDataFormat() {
        JacksonDataFormat format = new JacksonDataFormat();
        format.useList();
        format.setUnmarshalType(CityInfo.class);
        return format;
    }

    @Bean
    public Processor exitProcessor() {
        return new Processor() {
            Thread stop;

            public void process(final Exchange exchange) throws Exception {
                // stop this route using a thread that will stop
                // this route gracefully while we are still running
                if (stop == null) {
                    stop = new Thread() {
                        @Override
                        public void run() {
                            try {
                                exchange.getContext().stopRoute(GET_FO_EURO_INFO_ROUTE_ID);
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    };
                }
                // start the thread that stops this route
                stop.start();
            }
        };
    }

    @Bean
    public Processor errorPrcocessor() {
        return new Processor() {
            public void process(Exchange exchange) throws Exception {
                Exception exception = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
                String inputCityName = (String) exchange.getIn().getHeader(HEADER_CITY_NAME);
                LOGGER.error("Failed to execute query for \"{}\". Error was: {}", inputCityName, getRootCauseMessage(exception));
            }
        };
    }
}
