package daggerok.app;

import daggerok.app.entities.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

@Order(1)
@Component
@RequiredArgsConstructor
class TransactionTemplateEntityManagerWriter implements ApplicationRunner {

  private static final TransactionStatus STATUS = new SimpleTransactionStatus();

  final EntityManager em;
  final TransactionTemplate transactionTemplate;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    transactionTemplate.execute(status -> {
      Stream.of("ololo", "trololo")
          .map(name -> new Customer()
              .setName(name))
          .forEach(em::persist);
      return STATUS;
    });
  }
}
