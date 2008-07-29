#!/bin/sh
xsltproc /usr/share/sgml/docbook/xsl-stylesheets/fo/docbook.xsl Guide.xml > Guide.fo
fop -fo Guide.fo -pdf Guide.pdf
rm Guide.fo
