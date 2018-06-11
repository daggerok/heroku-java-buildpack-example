package daggerok.app;

import daggerok.app.dtos.CompanyDTO;
import daggerok.app.dtos.CompanyProjection;
import daggerok.app.entities.Customer;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.persistence.EntityManager;
import java.net.InetAddress;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
@RequiredArgsConstructor
public class Webflux {

  final EntityManager em;

  @SneakyThrows
  @Bean(name = "baseUrl")
  String baseUrl(final Environment env, @Value("${app.base.url}") final String appBaseUrl) {

    final String hostAddressValue = InetAddress.getLocalHost().getHostAddress();
    final String hostAddress = null == hostAddressValue ? "127.0.0.1" : hostAddressValue;
    final String portNumber = env.getProperty("server.port", "8080");
    final Integer port = Try.of(() -> Integer.parseInt(portNumber)).getOrElseGet(throwable -> 8080);
    final String baseUrl = format("http://%s:%d", hostAddress, port);

    return null == appBaseUrl || appBaseUrl.trim().isEmpty() ? baseUrl : appBaseUrl;
  }

  @Bean
  HandlerFunction<ServerResponse> entityHandler() {
    return request ->
        ok().contentType(APPLICATION_JSON)
            .body(Flux.fromIterable(
                em.createQuery(
                    " select c from Customer c left join fetch c.orders o left join fetch o.orderItems oi ",
                    Customer.class).getResultList()), Customer.class);
  }

  @Bean
  HandlerFunction<ServerResponse> companyProjectionHandler() {
    return request ->
        ok().contentType(APPLICATION_JSON)
            .body(Flux.fromIterable(
                em.createQuery(
                    // so when we are using DTO we don't need to wary about collection initialization in we don't need it...
                    " select new daggerok.app.dtos.CompanyProjection(c.id, c.name) from Customer c ",
                    CompanyProjection.class).getResultList()), Object.class);
  }

  @Bean
  @Transactional
  HandlerFunction<ServerResponse> dtoHandler() {
    return request ->
        ok().contentType(APPLICATION_JSON)
            .body(Flux.fromIterable(
                em.createQuery(
                    // so when we are using DTO we don't need to wary about collection initialization in we don't need it...
                    " select c from Customer c left join fetch c.orders o ",
                    Customer.class).getResultList())
                .map(customer -> new CompanyDTO()
                    .setCompanyId(customer.getId())
                    .setCompanyName(customer.getName())
                    .setOrders(customer.getOrders().stream().map(order -> (Object)order).collect(toSet())))
                .collect(toList()), List.class);
  }

  @Bean
  HandlerFunction<ServerResponse> fallbackHandler(@Qualifier("baseUrl") final String baseUrl) {
    return request ->
        ok().body(Mono.just(asList(
            format("GET     api -> %s/api", baseUrl),
            format("GET  entity -> %s/api/em/entity", baseUrl),
            format("GET     dto -> %s/api/em/dto", baseUrl),
            format("GET company -> %s/api/em/company", baseUrl),
            format("GET       * -> %s/", baseUrl))), List.class);
  }

  @Bean
  RouterFunction routes(final HandlerFunction<ServerResponse> fallbackHandler) {
    return
        nest(
            path("/api"),
            nest(
                path("/em"),
                route(
                    GET("/entity"),
                    entityHandler()
                ).andRoute(
                    GET("/dto"),
                    dtoHandler()
                ).andRoute(
                    GET("/company"),
                    companyProjectionHandler()
                )
            )
//        ).andRoute(
//            GET("/"),
//            fallbackHandler
        ).andOther(
            route(
                GET("/**"),
                fallbackHandler
            )
        )
    ;
  }
}
