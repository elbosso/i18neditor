#!/bin/sh
echo "$1"|translate-bin -f $2 -t $3 - | html2text - 
