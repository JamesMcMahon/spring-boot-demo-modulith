package sh.jfm.springbootdemos.modulith.inventory;

import org.springframework.stereotype.Component;
import sh.jfm.springbootdemos.modulith.inventoryapi.InventoryApi;

import java.util.Optional;

/// Simple direct call implementation of public api.
/// Component is injected at run time to avoid direct dependencies.
///
/// When moving to a full-on microservice disconnected by network bounderies
/// this can be implemented using Spring's [`HttpExchange`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/service/annotation/HttpExchange.html).
/// @see <a href="https://www.baeldung.com/spring-6-http-interface">Spring 6 HTTP Interface</a>
/// @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/service/invoker/HttpServiceProxyFactory.html">HttpServiceProxyFactory</a>
@Component
class DirectInventoryApi implements InventoryApi {

    private final Inventory inventory;

    DirectInventoryApi(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Optional<Long> markNextCopyAsUnavailable(String isbn) {
        return inventory.markNextCopyAsUnavailable(isbn)
                .map(Copy::id);
    }
}
