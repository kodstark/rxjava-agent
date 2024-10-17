package pl.kodstark.rxjava.agent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class SubscriptionAgentTests {
  private static PrintStream originalOut;
  private static ByteArrayOutputStream outputStream;

  @BeforeAll
  static void setUp() {
    originalOut = System.out;
    outputStream = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStream));
  }

  @AfterAll
  static void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void test01_shouldLogRequestFor_15_000() {
    Subscription subscription = Mockito.mock(Subscription.class);
    subscription.request(15_000);
    assertThat(outputStream.toString())
        .contains(
            "[WARN] Backpressure is at risk as Subscription#request executed with more than 10000 => 15000");
  }

  @Test
  void test02_shouldNotLogRequestFor_5_000() {
    Subscription subscription = Mockito.mock(Subscription.class);
    subscription.request(5_000);
    assertThat(outputStream.toString())
        .doesNotContain(
            "[WARN] Backpressure is at risk as Subscription#request executed with more than 10000 => 5000");
  }

  @Test
  void test03_shouldLogRequestFor_20_000() {
    Subscription subscription = Mockito.mock(Subscription.class);
    subscription.request(20_000);
    assertThat(outputStream.toString())
        .contains(
            "[WARN] Backpressure is at risk as Subscription#request executed with more than 10000 => 20000");
  }
}
