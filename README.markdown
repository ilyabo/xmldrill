Intro
=====

!(xmldrill - XML tree exercise)[https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/xmltree-sm.png]

xmldrill is a Java web-app for students learning XML featuring the following interactive exercises:

- [XML tree](https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/xmltree-compare.png): A random XML is generated and visualized as a tree. The students have to enter the XML code for it.
- [XML schema](https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/xmlschema.png): A random XML schema is generated. The students have to provide a valid XML instance for the schema. 
- [XSLT](https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/xslt.png): A random input is generated for a given XSL stylesheet. The students have to provide the output of the transformation. 
- [SVG](https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/svg.png): A random SVG document is generated. The users have to draw it and can then compare to the real output. 


Plus, there is an auto-updated page with the students [ranking](https://github.com/ilyabo/xmldrill/raw/master/doc/screenshots/ranking.png).


Building
========
Run ant on build.xml 


Installation
============
Drop [xmldrill.war](https://github.com/downloads/ilyabo/xmldrill/xmldrill.war) to the Tomcat's webapps folder. The application does not require a database. 


