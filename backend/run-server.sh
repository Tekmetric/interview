#!/usr/bin/env bash
set -e

mvn clean install

set +e

mvn spring-boot:run -pl server-module