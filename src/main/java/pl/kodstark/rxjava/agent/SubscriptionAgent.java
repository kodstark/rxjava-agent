package pl.kodstark.rxjava.agent;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class SubscriptionAgent {

  public static void premain(String agentArgs, Instrumentation inst) {
    new AgentBuilder.Default()
        .type(
            ElementMatchers.hasSuperType(ElementMatchers.named("org.reactivestreams.Subscription")))
        .transform(SubscriptionAgent::transform)
        .installOn(inst);
  }

  private static DynamicType.Builder<?> transform(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassLoader classLoader,
      JavaModule module,
      ProtectionDomain protectionDomain) {
    return builder
        .method(ElementMatchers.named("request"))
        .intercept(Advice.to(RequestAdvice.class));
  }

  public static class RequestAdvice {

    public static final long THRESHOLD;

    public static final boolean LOG_STACKTRACE;

    static {
      String propertyName = "rxjava.agent.subscription.request-logging-threshold";
      String propertyValue = System.getProperty(propertyName, "10000");
      try {
        THRESHOLD = Long.parseLong(propertyValue);
      } catch (NumberFormatException e) {
        throw new RuntimeException(
            String.format("Property %s=%s cannot be parsed to long", propertyName, propertyValue));
      }
    }

    static {
      String propertyName = "rxjava.agent.subscription.log-stacktrace";
      String propertyValue = System.getProperty(propertyName, "true");
      LOG_STACKTRACE = Boolean.parseBoolean(propertyValue);
    }

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Argument(0) long n) {
      if (n > THRESHOLD) {
        String message =
            "[WARN] Backpressure is at risk as Subscription#request executed with more than "
                + THRESHOLD
                + " => "
                + n;
        if (LOG_STACKTRACE) {
          new RuntimeException(message).printStackTrace(System.out);
        } else {
          System.out.println(message);
        }
      }
    }
  }
}
