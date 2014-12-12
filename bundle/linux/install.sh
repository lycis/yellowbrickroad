#!/bin/bash
#
# This script installs ybr on your system.
#

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

install -d /usr/lib/yellow-brick-road/

for f in ./lib/*; do \
    install -D -t /usr/lib/yellow-brick-road/ $f; \
done

for f in ./bin/*; do \
    install -D -t /usr/bin/ $f; \
done
