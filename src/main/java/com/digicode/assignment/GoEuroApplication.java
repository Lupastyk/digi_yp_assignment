package com.digicode.assignment;

import com.digicode.assignment.context.CamelConfiguration;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ServiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.digicode.assignment.context.CamelConfiguration.HEADER_CITY_NAME;
import static com.digicode.assignment.context.CamelConfiguration.GET_FO_EURO_INFO_ROUTE_ID;

@Configuration
@Import(CamelConfiguration.class)
@SpringBootApplication
public class GoEuroApplication implements CommandLineRunner {

    @Autowired
    CamelContext camelContext;

    public static void main(String[] args) {
        new SpringApplicationBuilder(GoEuroApplication.class).run(args);
    }

    public void run(String... args) throws Exception {
        if (args.length != 1) {
            System.out.println("City name should be specified\n" +
                    "Example of correct usage: java -jar GoEuroTest.jar \"CITY_NAME\"");
        } else {
            ProducerTemplate template = camelContext.createProducerTemplate();
            template.sendBodyAndHeader("direct:start", "", HEADER_CITY_NAME, args[0]);
            while (true) {
                Thread.sleep(1000);
                ServiceStatus routeStatus = camelContext.getRouteStatus(GET_FO_EURO_INFO_ROUTE_ID);
                if (routeStatus.isStopped()) {
                    break;
                }
            }
        }
    }
}
