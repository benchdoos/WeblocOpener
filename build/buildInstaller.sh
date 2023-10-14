#!/usr/bin/env bash

echo current location: $PWD
ls -l $PWD

#exec docker run --rm -i -v "$PWD:/work" amake/innosetup "$@"
exec docker run --rm -i -v "$PWD/../:/work" amake/innosetup build/WeblocOpener.iss