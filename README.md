![settings4jlogo](http://settings4j.org/archiv/latest/icon/settings4j-logo-64px.png)

[![Build Status](https://secure.travis-ci.org/brabenetz/settings4j.png?branch=master)](http://travis-ci.org/brabenetz/settings4j)
[![Coverage Status](https://coveralls.io/repos/brabenetz/settings4j/badge.svg?branch=code-quality&service=github)](https://coveralls.io/github/brabenetz/settings4j?branch=code-quality)
[![Coverity](https://scan.coverity.com/projects/6217/badge.svg)](https://scan.coverity.com/projects/brabenetz-settings4j)
[![Dependency Status](https://www.versioneye.com/user/projects/55e50d658c0f62001b000180/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55e50d658c0f62001b000180)
[![Maven site](https://img.shields.io/badge/Maven-site-blue.svg)](http://settings4j.org/archiv/latest/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.settings4j/settings4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.settings4j/settings4j)
[![License: Apache 2.0](https://img.shields.io/badge/license-Apache_2.0-brightgreen.svg)](https://github.com/brabenetz/settings4j/blob/master/LICENSE.txt)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/brabenetz/settings4j?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# Settings for Java

Settings4j provides you with a lightweight Java library that allows programmers not to worry
about the location where the configuration settings are stored.

Designed to ease the development process, settings4j can decide where the value will be placed,
so the developer does not have to select the type of configuration.

## Dependency Information

Simply add to your pom.xml:

```xml
<dependency>
  <groupId>org.settings4j</groupId>
  <artifactId>settings4j</artifactId>
  <version>2.0.1</version>
</dependency>
```

See https://maven-badges.herokuapp.com/maven-central/org.settings4j/settings4j

## You don't know how your Project should be configured (System Properties, JNDI, Preferences, others...)?

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

See http://settings4j.org

## You are not happy with the default setting4j config?

Simply put your own settings4j.xml into the root-classpath or configure it manual in Java.

See http://settings4j.org/archiv/latest/config.html

## You use Spring?

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

See http://settings4j.org/archiv/latest/configSpringPlaceholder.html

## License

This project is licensed under Apache License, Version 2.0.

See https://github.com/brabenetz/settings4j/blob/master/LICENSE.txt


