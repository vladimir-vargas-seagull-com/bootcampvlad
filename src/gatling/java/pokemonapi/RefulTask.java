package pokemonapi;


import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RefulTask extends Simulation {
    // Case 1 - Como puedo testearlo en diferentes ambientes
    //String baseUrl = System.getProperty("baseUrl", "https://api.restful-api.dev");
    // Define the data
    //FeederBuilder.FileBased<Object> feeder = jsonFile("data/pokemon.json").circular();
    // Define preconditions
    // We want to test the Pokemon API with 5 pokemons
    //
    // Define the base URL and headers
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.restful-api.dev")
            .contentTypeHeader("application/json");


    // Define the scenario


    ScenarioBuilder scn = scenario("Task API Test")
            //.feed(feeder)

            .exec(http("POST task")
                    .post("/objects")
                    .body(StringBody("{\"name\": \"Apple MacBook Pro 16\",\"data\": { \"year\": 2019,\"price\": 1849.99,\"CPU model\": \"Intel Core\",\"Hard disk size\": \"1TB\"}}"))
                    .check(status().is(200))
                    .check(jmesPath("id").find().saveAs("thiis"))
            )

            .exec(
                    session -> {
                        System.out.println("identificatorID: " + session.getString("thiis"));
                        return session;

                    }
            )

            .exec(http("PUT task")
                    .put("/objects/#{thiis}")
                    .body(StringBody("{\"name\": \"Apple MacBook Pro 16 update\",\"data\": { \"year\": 2020,\"price\": 1850,\"CPU model\": \"Intel Core\",\"Hard disk size\": \"1TB\"}}"))
                    .check(status().is(200))
                    .check(jmesPath("updatedAt").find().saveAs("updated"))

            )
            .exec(
                    session -> {
                        System.out.println("Updated: " + session.getString("updated"));
                        return session;

                    }
            )
            .exec(http("GET task")
                    .get("/objects/#{thiis}")
                    .body(StringBody("{\"name\": \"Apple MacBook Pro 16 update\",\"data\": { \"year\": 2020,\"price\": 1850,\"CPU model\": \"Intel Core\",\"Hard disk size\": \"1TB\"}}"))
                    .check(status().is(200))
                    .check(jmesPath("name").find().saveAs("nameupdated"))


            )
            .exec(
                    session -> {
                        System.out.println("GET Updated: " + session.getString("nameupdated"));
                        return session;

                    }
            )

            ;

    {
        setUp(
               /* scn.injectOpen(
                        atOnceUsers(1),
                        nothingFor(Duration.ofSeconds(5)),
                        rampUsers(10).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(10)
                        )
                )
        ).protocols(httpProtocol);*/
                scn.injectClosed(
                        rampConcurrentUsers(1).to(10).during(Duration.ofSeconds(10)),
                        constantConcurrentUsers(10).during(Duration.ofSeconds(20))))
                .protocols(httpProtocol);
    }


}
