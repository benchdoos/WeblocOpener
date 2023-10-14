#!/usr/bin/env bash

echo current location: $PWD
echo root location: $GITHUB_WORKSPACE
ls -l $PWD

#exec docker run --rm -i -v "$PWD:/work" amake/innosetup "$@"
exec docker run --rm -i -v "$PWD/../:/work" amake/innosetup build/WeblocOpener.iss