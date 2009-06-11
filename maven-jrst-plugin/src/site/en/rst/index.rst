Home
====

.. contents::

Presentation
------------

Maven plugin to use JRST library.


Operation
---------

Maven can't manage RST documentation file format. So, that's
why maven-jrst-plugin has been made.

It use the same directory structure for site sources, transform
it into xdoc, and maven-site-plugin is used to build finale site.

But, pom has to be configured:
  - to pre generate xdoc file from rst
  - change maven default site directory


POM configuration
-----------------

pre-site : maven-jrst-plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

::

  <plugin>
        <groupId>org.codelutin</groupId>
        <artifactId>maven-jrst-plugin</artifactId>
        <version>${jrst.version}</version>
        <configuration>
            <directoryIn>${basedir}/src/site</directoryIn>
            <directoryOut>${project.build.directory}/generated-site</directoryOut>
            <defaultLocale>en</defaultLocale>
            <inputEncoding>UTF-8</inputEncoding>
            <outputEncoding>UTF-8</outputEncoding>
        </configuration>
        <executions>
            <execution>
                <phase>pre-site</phase>
                <goals>
                    <goal>jrst</goal>
                </goals>
            </execution>
        </executions>
  </plugin>


pre-site : maven-antrun-plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This plugin is used to copy non rst files (such as images...)
into the generation site directory.

::

  <plugin>
        <artifactId>maven-antrun-plugin</artifactId>
        <executions>
            <execution>
                <phase>pre-site</phase>
                <configuration>
                    <tasks>
                        <mkdir dir="${basedir}/src/site"/>
                        <copy todir="${project.build.directory}/generated-site" verbose="${maven.verbose}" overwrite="false">
                            <fileset dir="${basedir}/src/site">
                                <exclude name="**/rst/**"/>
                            </fileset>
                        </copy>
                    </tasks>
                </configuration>
                <goals>
                    <goal>run</goal>
                </goals>
            </execution>
        </executions>
  </plugin>


site : maven-site-plugin
~~~~~~~~~~~~~~~~~~~~~~~~

::

  <plugin>
        <artifactId>maven-site-plugin</artifactId>
        <version>2.0-beta-7</version>
        <configuration>
           <siteDirectory>${project.build.directory}/generated-site</siteDirectory>
           <inputEncoding>UTF-8</inputEncoding>
           <outputEncoding>UTF-8</outputEncoding>
           <generateReports>true</generateReports>
           <locales>en,fr</locales>
        </configuration>
  </plugin>