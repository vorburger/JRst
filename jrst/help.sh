#!/bin/bash

rep=`dirname $0`
action=`basename $0 .sh`

PROPERTIES=LutinBuilder.properties

if [ -z "$JAVA_HOME" -a -f "$rep/$PROPERTIES" ]; then
        JAVA_HOME=`grep lutinbuilder.java.home $rep/$PROPERTIES`
        JAVA_HOME=`echo $JAVA_HOME|sed s/lutinbuilder.java.home=//|sed s/^#.*//`
fi

if [ -z "$JAVA_HOME" -a -f "$HOME/$PROPERTIES" ]; then
        JAVA_HOME=`grep lutinbuilder.java.home $HOME/$PROPERTIES`
        JAVA_HOME=`echo $JAVA_HOME|sed s/lutinbuilder.java.home=//|sed s/^#.*//`
fi

if [ -z "$JAVA_HOME" ]; then
        JAVA=`TYPE --path java`
else
        JAVA=$JAVA_HOME/bin/java
fi

if [ -z "$JAVA" ]; then
        echo "impossible de trouver l'emplacement de java"
        exit 1
fi

if [ -z "$LUTIN_BUILDER_JAR" -a -f "$rep/$PROPERTIES" ]; then
        LUTIN_BUILDER_JAR=`grep lutinbuilder.jar $rep/$PROPERTIES`
        LUTIN_BUILDER_JAR=`echo $LUTIN_BUILDER_JAR|sed s/lutinbuilder.jar=//|sed s/^#.*//`
fi
if [ -z "$LUTIN_BUILDER_JAR" -a -f "$HOME/$PROPERTIES" ]; then
        LUTIN_BUILDER_JAR=`grep lutinbuilder.jar $HOME/$PROPERTIES`
        LUTIN_BUILDER_JAR=`echo $LUTIN_BUILDER_JAR|sed s/lutinbuilder.jar=//|sed s/^#.*//`
fi

if [ -z "$LUTIN_BUILDER_JAR" -a -f "$rep/lib/LutinBuilder.jar" ]; then
     LUTIN_BUILDER_JAR=$rep/lib/LutinBuilder.jar
fi

if [ -z "$LUTIN_BUILDER_JAR" -a -f "$rep/binlib/LutinBuilder.jar" ]; then
     LUTIN_BUILDER_JAR=$rep/binlib/LutinBuilder.jar
fi

if [ -z "$LUTIN_BUILDER_JAR" ]; then
        echo "impossible de trouver l'emplacement de LutinBuilder.jar"
        echo "essai de récupération de LutinBuilder.jar grâce à ant"
        result=`ant -f $rep/build.xml getLutinBuilder 2>&1`
        retour=$?
        if [ "$retour" = "0" ]; then
                LUTIN_BUILDER_JAR=$rep/binlib/LutinBuilder.jar
        else
                echo "impossible de récupérer LutinBuilder.jar, vous avez deux solution:"
                echo " - installer ant sur votre système"
                echo " - placer LutinBuilder.jar dans le répertoire binlib"
                exit 1
        fi
fi

if [ $action == "build" -o $action == "run" ]; then
        stoparg="--"
fi

time $JAVA -jar $LUTIN_BUILDER_JAR --racine . $action $stoparg "$@"
