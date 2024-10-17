# rxjava-agent

Project provides java agent to debug rxjava subscription.

When any class implementing `org.reactivestreams.Subscription` will execute `request` method with argument bigger than
10_000 then warning is logged. It is achieved with **ByteBuddy** instrumentation.

Threshold can be changed using Java property, eg `-Drxjava.agent.subscription.request-logging-threshold=500`

By default, warning includes stacktrace to identify which rxjava operator executed `Subscription#request` method.
Stacktrace can be switched to normal warning with using a property `-Drxjava.agent.subscription.log-stacktrace=false`

## Build

Use gradle to build agent JAR and run tests. Agent jar uses `-all` suffix and contains code running instrumentation and
ByteBuddy code in *ShadowJar*. Agent jar will be placed in `build/libs/rxjava-agent-1.0-SNAPSHOT-all.jar`.

`./gradlew build`

## Use agent

Use `-javaagent` argument to enable this Java agent on your application or tests. For example:

```bash
java -javaagent:../rxjava-agent/build/libs/rxjava-agent-1.0-SNAPSHOT-all.jar ...

# OR
java -Drxjava.agent.subscription.request-logging-threshold=500 \
  -javaagent:../rxjava-agent/build/libs/rxjava-agent-1.0-SNAPSHOT-all.jar ...
```