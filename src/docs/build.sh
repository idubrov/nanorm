#!/bin/sh
#xsltproc /usr/share/sgml/docbook/xsl-stylesheets/fo/docbook.xsl Guide.xml > Guide.fo
#fop -fo Guide.fo -pdf Guide.pdf
#rm Guide.fo

OPTS="$OPTS -P latex.unicode.use=1"
OPTS="$OPTS -P doc.collab.show=0"
OPTS="$OPTS -P latex.output.revhistory=0"
OPTS="$OPTS -P latex.class.book=report"
OPTS="$OPTS -P latex.hyperparam=linktocpage,colorlinks,citecolor=blue,pdfstartview=FitH"
OPTS="$OPTS -P table.default.position=[hp]"
OPTS="$OPTS -P table.in.float=0"
OPTS="$OPTS -P table.title.top=1"
OPTS="$OPTS -P doc.lot.show="

#export XML_CATALOG_FILES="file:///usr/share/sgml/docbook/xml-dtd-4.5/catalog.xml"
#export  XML_DEBUG_CATALOG=1
dblatex -t tex $OPTS Guide.xml
dblatex $OPTS Guide.xml
