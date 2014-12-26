#!/bin/bash
#
# This script installs ybr on your system.
#

BINFILE=/usr/bin/ybr
LIBDIR=/usr/lib/yellow-brick-road/

if [ "$EUID" -ne 0 ]
then 
  echo "Please run as root"
  exit
fi

echo -n "Cleaning old installations... "

if [ -f "$BINFILE" ]
then
  rm -f $BINFILE || (echo "failed" && exit 1)
fi

if [ -d "$LIBDIR" ]
then
  rm -rf $LIBDIR || (echo "failed" && exit 1)
fi

echo "ok"
echo -n "Installing new files... "

install -d $LIBDIR || (echo "failed" && exit 1)

for f in ./lib/*; do \
    install -D -t $LIBDIR $f || (echo "failed" && exit 1)
done

for f in ./bin/*; do \
    install -D -t /usr/bin/ $f || (echo "failed" && exit 1)
done

echo "ok"