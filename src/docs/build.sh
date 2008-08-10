#!/bin/sh
#
# Copyright (C) 2008 Ivan S. Dubrov
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#         http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
