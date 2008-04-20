Weiter infos:
https://sourceforge.net/projects/settings4j/
http://www.settings4j.org/

When I make a Project, I must decide where the configuration settings will be stored.
There are many diffeneret solutions:
 - You can store *.property and *.xml into the Classpath/FileSystem
 - You can get your Settings from a JNDI Context
 - You can get your Settings from a DB

I was inspired by log4j:
The programmer says, that he want log some information.
It is everytime possible to simply change the target (FileSystem, DB, MailServer, ...) where the information ist logged out. The Programmer must not care about it.

For Settings, the Solution could be similar.
The Programmer says he want the settings for Key 'xyz', and should not care about where the Settings comes from.
------------------------------------

A sample configuration could looks like:
------------------------------------
<settings4j:configuration xmlns:settings4j='http://settings4j.org/'>
  <connector name="SystemPropertyConnector" class="org.settings4j.connector.SystemPropertyConnector"/>
  <connector name="JNDIConnector" class="org.settings4j.connector.JNDIConnector"/>
  <connector name="ClasspathConnector" class="org.settings4j.connector.ClasspathConnector"/>
  <connector name="DBConnector1" class="org.settings4j.connector.DBHibernateJNDIConnector">
  	<param name="jndiName" value="jdbc/myapp-db"/>
  	<param name="dialect" value="org.hibernate.dialect.Oracle9Dialect"/>
  </connector>
  <connector name="DBConnector2" class="org.settings4j.connector.DBHibernateJNDIConnector">
  	<param name="jndiName" value="jdbc/settings4j-db"/>
  	<param name="dialect" value="org.hibernate.dialect.MySQLInnoDBDialect"/>
  </connector>
  
  <settings name="com.mycompany.myapp1">
    <connector-ref ref="DBConnector1" readonly="false" />
  </settings>
  
  <root>
    <connector-ref ref="SystemPropertyConnector" readonly="true" />
    <connector-ref ref="JNDIConnector" readonly="true" />
    <connector-ref ref="ClasspathConnector" readonly="true" />
    <connector-ref ref="DBConnector2" readonly="true" />
  </root>
</settings4j:configuration>
------------------------------------

A sample Interface could looks like:
------------------------------------
public interface Settings {
    String getString(String key);
    byte[] getContent(String key);
    Object getObject(String key);
    void setString(String key, String value);
    void setContent(String key, byte[] value);
    void setObject(String key, Object value);
}