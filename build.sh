#!/bin/bash

## variables
CICADA_COLLECTOR=cicada-collector
CICADA_WEB=cicada-web
SCRIPTS_DIR=./scripts

function local_build() {
    ## package
    mvn clean package -Dmaven.test.skip=true

    BUILD_OUTPUT=./build

    # cicada-collector
    if [ ! -d $BUILD_OUTPUT/$CICADA_COLLECTOR ] 
    then
        mkdir -p $BUILD_OUTPUT/$CICADA_COLLECTOR
    fi
    cp $CICADA_COLLECTOR/target/cicada-collector*.jar $BUILD_OUTPUT/$CICADA_COLLECTOR
    cp $CICADA_COLLECTOR/target/application.yml $BUILD_OUTPUT/$CICADA_COLLECTOR
    cp $CICADA_COLLECTOR/target/logback-spring.xml $BUILD_OUTPUT/$CICADA_COLLECTOR
    cp $SCRIPTS_DIR/collector_run.sh $BUILD_OUTPUT/$CICADA_COLLECTOR/run.sh

    #cicada-web
    if [ ! -d $BUILD_OUTPUT/$CICADA_WEB ] 
    then
        mkdir -p $BUILD_OUTPUT/$CICADA_WEB
    fi
    cp $CICADA_WEB/target/cicada-web-*.jar $BUILD_OUTPUT/$CICADA_WEB
    cp $CICADA_WEB/target/application.yml $BUILD_OUTPUT/$CICADA_WEB
    cp $CICADA_WEB/target/logback-spring.xml $BUILD_OUTPUT/$CICADA_WEB
    cp $SCRIPTS_DIR/web_run.sh $BUILD_OUTPUT/$CICADA_WEB/run.sh
}

case $1 in
    "local_build")
        local_build 
        ;;

    *)
        echo -e "Usage: $0 { local_build }"
        ;;

esac

