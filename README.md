# Settings for Java

http://settings4j.org

Settings4j provides you with a lightweight Java library that allows programmers not to worry
about the location where the configuration settings are stored.

Designed to ease the development process, settings4j can decide where the value will be placed,
so the developer does not have to select the type of configuration.

### You don't know how your Project should be configured (System Properties, JNDI, Preferences, others...)?

Use settings4j:

```java
  String myValue = Settings4j.getString("SOME_KEY");
```

The Default Configuration works in the following Order to get Configuration-Values:
	
  * Lookup in System.getProperty(...)
  * Lookup in JNDI Context
  * Lookup in Preferences.userRoot() and then Preferences.systemRoot()
  * Lookup in Classpath

With Settings4j, your Operation can decide how the application should be configured.

### You are not happy with the default setting4j config?

Simply put your own settings4j.xml into the root-classpath or configure it manual in Java.

### You use Spring?

You probable uses "@Value" annotation like that:

```java
  @Value("${SOME_KEY}")
  String myValue;
```

simply configure Settings4jPlaceholderConfigurer and your "@Value" Annotations will be processes through Settings4j:

```xml
<beans>
  <!-- The PlaceholderConfigurer: -->
  <bean class="org.settings4j.helper.spring.Settings4jPlaceholderConfigurer" />
</beans>
```
(Works also with Spring Java Based Configuration)





