#!/usr/bin/env bash

#exec docker run --rm -i -v "$PWD:/work" amake/innosetup "$@"
exec docker run --rm -i -v "$PWD/../:/work" amake/innosetup build/WeblocOpener.iss