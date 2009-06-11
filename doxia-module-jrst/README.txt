Doxia Module JRst
=================

Configuration
-------------

To use it, put it in your pom.xml :

  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <dependencies>
      <dependency>
        <groupId>org.codelutin</groupId>
        <artifactId>doxia-module-jrst</artifactId>
        <version>${doxia-module-jrst.version}</version>
      </dependency>
    </dependencies>
  </plugin>

That it ! Then run "mvn site".
