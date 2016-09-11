#!/bin/bash


function init() {
    sudo yum groupinstall -y Base "Development Tools" "Perl Support"
    sudo yum install -y zlib zlib-devel
}

function install_cronolog() {
    tar xf cronolog-1.6.2.tar.gz
    cd cronolog-1.6.2
    ./configure && make -j8 && sudo make install
    cd ..
}

function install_nginx() {
    tar xf ./nginx.tar.gz
    cd nginx
    bash ./install_tengine.sh
    cd ..
}

## main
init
install_cronolog
install_nginx

