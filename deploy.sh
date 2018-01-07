#!/bin/bash -xue

lein clean
lein cljsbuild once min
firebase deploy
