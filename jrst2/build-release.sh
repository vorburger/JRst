#!/bin/sh

APP_NAME=jrst

# script pour la creation d'un zip de release

if [ "$USER" = "poussin" ]; then
  LABS_LOGIN=bpoussin
else
  LABS_LOGIN=$USER
fi

dir=$(dirname $0)
cd $dir

VER=$(xmlstarlet sel -N "p=http://maven.apache.org/POM/4.0.0" -t -v "/p:project/p:version" pom.xml)
RELEASE=${APP_NAME}-$VER
echo "build release $RELEASE"

# recuperation de tous les jar dans un répertoire
# target/${APP_NAME}-$VER/WEB-INF/lib/
echo "prepare jar ..."
mvn -o compile jar:jar war:war

# creation du repertoire cible
TARGET=/tmp/$RELEASE
mkdir -p $TARGET/lib

echo "copy library ..."
cp target/$RELEASE/WEB-INF/lib/* $TARGET/lib
echo "copy ${APP_NAME} ..."
cp target/$RELEASE.jar $TARGET

# creation du Class Path
echo "create classpath ..."
cd $TARGET
SEPW=";"
SEP=":"
CP=$RELEASE.jar
CPW=$RELEASE.jar
for f in lib/*; do
  CP="$CP$SEP$f"
  CPW="$CPW$SEPW$f"
done

echo "create script ..."
cat << EOF > $TARGET/${APP_NAME}.bat
java -cp $CPW org.codelutin.jrst.JRST %1 %2 %3 %4 %5 %6 %7 %8 %9
EOF

cat << EOF > $TARGET/${APP_NAME}.sh
java -cp $CP org.codelutin.jrst.JRST \$*
EOF

chmod +x $TARGET/${APP_NAME}.sh

echo "create zip /tmp/$RELEASE.zip ..."
cd /tmp
zip -q -r $RELEASE.zip $RELEASE

echo "copy zip to labs ..."
ssh $LABS_LOGIN@labs.libre-entreprise.org "mkdir -p /home/groups/${APP_NAME}/htdocs/download"
scp $RELEASE.zip $LABS_LOGIN@labs.libre-entreprise.org:/home/groups/${APP_NAME}/htdocs/download/
