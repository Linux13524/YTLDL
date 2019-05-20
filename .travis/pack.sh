#!/bin/bash
set -e

rm -rf dist/
mkdir -p dist/
if [ -z "$PLATFORM" ]; then
    PLATFORM=$TRAVIS_OS_NAME
fi
cp app/build/outputs/apk/debug/app-debug.apk dist/YTLDL-${PLATFORM}-debug.apk