#!/bin/bash

rm -rf out
rm *.vsix

npm run compile
npm run vscode:prepublish
vsce package
