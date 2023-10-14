#!/usr/bin/env bash

echo current location: $PWD
echo root location: $GITHUB_WORKSPACE
ls -l $PWD

if [ -n "$GITHUB_WORKSPACE" ]; then
    working_location=$GITHUB_WORKSPACE
else
    working_location=$PWD
fi

echo "Working Location: $working_location"
cd $working_location
echo current location: $PWD
ls -a

chmod 777 $working_location/target

#exec docker run --rm -i -v "$PWD:/work" amake/innosetup "$@"
exec docker run --rm -i -v "$working_location/WeblocOpener/../:/work" amake/innosetup build/WeblocOpener.iss