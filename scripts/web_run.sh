#!/bin/sh

ACTIVE_PROFILE=env_development # env_development, env_production
VERSION=2.0.0
PACKAGE_NAME=cicada-web

function stop() {
    pid=`ps -ef | grep $PACKAGE_NAME-$VERSION | grep -v "grep" | awk '{print $2}'`
    kill -9 $pid
    echo "killed process: $pid"
}

function start() {
    nohup java -jar -Xmx4096M -XX:MaxPermSize=512M  \
        -Djava.security.egd=file:/dev/./urandom ${PACKAGE_NAME}-${VERSION}-SNAPSHOT.jar \
        --spring.profiles.active=$ACTIVE_PROFILE > nohup.log &
}

function restart() {
    stop
    start
}

function status() {
    ps -ef | grep $PACKAGE_NAME | grep -v "grep" 
}

case $1 in
    start)
        start
        ;;

    stop)
        stop
        ;;

    status)
        status
        ;;

    restart)
        restart
        ;;

    *)
        echo -e "Usage $0 { start | stop | restart | status }"
        ;;
esac

