#!/bin/bash

cd ./parser
./gradlew clean build
mv build/libs/juml-0.1-all.jar ../vsplugin/juml.jar

cd ../vsplugin
./build.sh
