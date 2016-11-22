package com.digicode.assignment;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.digicode.assignment.context.CamelConfiguration.GET_FO_EURO_INFO_ROUTE_ID;
import static com.digicode.assignment.context.CamelConfiguration.HEADER_CITY_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Администратор on 20.11.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = GoEuroApplication.class)
public class CamelProcessingTest {
    @Rule
    public WireMockClassRule wireMockServer = new WireMockClassRule(8080);

    @Autowired
    CamelContext context;

    @Value("${goeuro.api.url}")
    private String goEuroEndpoint;
    private static final String RESULT_ENDPOINT = "mock:myEndpoint";

    @Autowired
    ProducerTemplate producerTemplate;

    @Test
    @DirtiesContext
    public void shouldProcessCityNameWithoutQuotes() throws Exception {
        wireMockServer.addStubMapping(get(urlPathEqualTo("/api/v2/position/suggest/en/Berlin"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("[\n" +
                                "{\n" +
                                "\"_id\":376217,\n" +
                                "\"name\":\"Berlin\",\n" +
                                "\"type\":\"location\",\n" +
                                "\"geo_position\":{\n" +
                                "\"latitude\":52.52437,\n" +
                                "\"longitude\":13.41053\n" +
                                "}\n" +
                                "}\n" +
                                "]"))
                .build());

        context.getRouteDefinition(GET_FO_EURO_INFO_ROUTE_ID)
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByToString(".*file.*")
                                .replace()
                                .to(RESULT_ENDPOINT);
                    }
                });

        final MockEndpoint resultEndpoint = context.getEndpoint(RESULT_ENDPOINT, MockEndpoint.class);
        producerTemplate.sendBodyAndHeader("direct:start", "", HEADER_CITY_NAME, "Berlin");
        await().atMost(5, SECONDS).pollDelay(1, SECONDS).until(() -> resultEndpoint.getExchanges().get(0).getIn().getBody(String.class),
                IsEqual.equalTo("376217,Berlin,location,52.52437,13.41053\r\n"));
    }

    @Test
    @DirtiesContext
    public void shouldProcessCityNameWithQuotes() throws Exception {
        wireMockServer.addStubMapping(get(urlPathEqualTo("/api/v2/position/suggest/en/Baden%20baden"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("[\n" +
                                "{\n" +
                                "\"_id\":376138,\n" +
                                "\"name\":\"Baden-Baden\",\n" +
                                "\"type\":\"location\",\n" +
                                "\"geo_position\":{\n" +
                                "\"latitude\":48.7606,\n" +
                                "\"longitude\":8.23975\n" +
                                "}\n" +
                                "}\n" +
                                "]"))
                .build());

        context.getRouteDefinition(GET_FO_EURO_INFO_ROUTE_ID)
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByToString(".*file.*")
                                .replace()
                                .to(RESULT_ENDPOINT);
                    }
                });

        final MockEndpoint resultEndpoint = context.getEndpoint(RESULT_ENDPOINT, MockEndpoint.class);
        producerTemplate.sendBodyAndHeader("direct:start", "", HEADER_CITY_NAME, "\"Baden baden\"");
        await().atMost(5, SECONDS).pollDelay(1, SECONDS).until(() -> resultEndpoint.getExchanges().get(0).getIn().getBody(String.class), IsEqual.equalTo("376138,Baden-Baden,location,48.7606,8.23975\r\n"));
    }

    @Test
    @DirtiesContext
    public void shouldReturnEmptyResultIfNotFound() throws Exception {
        wireMockServer.addStubMapping(get(urlPathEqualTo("/api/v2/position/suggest/en/Berlin"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("[]"))
                .build());

        context.getRouteDefinition(GET_FO_EURO_INFO_ROUTE_ID)
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveByToString(".*file.*")
                                .replace()
                                .to(RESULT_ENDPOINT);
                    }
                });

        final MockEndpoint resultEndpoint = context.getEndpoint(RESULT_ENDPOINT, MockEndpoint.class);
        producerTemplate.sendBodyAndHeader("direct:start", "", HEADER_CITY_NAME, "Berlin");
        await().atMost(5, SECONDS).pollDelay(1, SECONDS).until(() -> resultEndpoint.getExchanges().get(0).getIn().getBody(String.class), IsEqual.equalTo(""));
    }
}
