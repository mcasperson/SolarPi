#!/bin/bash
VERSION=`curl --silent "https://api.github.com/repos/mcasperson/SolarPi/releases/latest" | jq -r .tag_name`
wget -O /opt/solarpi.jar https://github.com/mcasperson/SolarPi/releases/download/${VERSION}/solarpi.jar