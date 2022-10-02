#!/bin/bash
set -x
mogrify -background white -extent 3000x1500 graph-*.png
convert -delay 70 -loop 0 graph-*.png graph.gif
mogrify -gravity south -undercolor black -fill white -font arial -pointsize 30 -annotate +0+30 "Parsing of expression $1" graph.gif
