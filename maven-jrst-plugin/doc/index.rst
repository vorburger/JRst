Maven-jrst-plugin
=================

.. contents::


Présentation
------------

Le plugin maven-jrst-plugin permet l'utilisation depuis maven de 
JRst. Pour plus de détails sur Jrst veuillez consulter 
l'adresse suivante http://jrst.labs.libre-entreprise.org .

Il permet de transfomer les fichiers RST en fichiers xdoc de maven, pour générer
le site web proposer par le plugin maven-site-plugin.

Utilisation dans le pom.xml
---------------------------

Voici une déclaration de utilisation de maven-jrst-plugin dans un 
pom.xml.

::

  <plugins>
   ...
   <plugin>
    <groupId>lutinplugin</groupId>
    <artifactId>maven-jrst-plugin</artifactId>
    <version>0.7</version>
    <executions>
     <execution>
      <id>jrst</id>
      <phase>pre-site</phase>
      <goals>
       <goal>jrst</goal>
      </goals>
     </execution>
    </executions>
    <configuration>
     <directoryIn>${basedir}/doc</directoryIn>
     <directoryOut>${basedir}/target/site-build/xdoc</directoryOut>
     <verbose>${maven.verbose}</verbose>
    </configuration>
   </plugin>
   ...
  </plugins>
