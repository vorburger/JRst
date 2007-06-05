=====================================
XSL (Extensible Stylesheets Language)
=====================================

.. contents:: Summary

Presentation
============

XML is a data structural language, and not a data repressentation. So, XSL (Extensible Stylesheets Language)
is advise by W3C to doing XML document data representation. XSL is itself design with XML formalism,
that means that style sheet XSL is a well formed XML document. 

XSL is a language which makes possible to define style sheets for documents XML as well as CSS (Cascading 
StyleSheets) for HTML or DSSSL (Document Style Semantics and Specification Language) for SGML. XSL is 
inspired by DSSSL which it take many functionalities and is compatible with CSS.

However, contrary to the CSS, XSL also enable to reprocess a XML document in order to modify
its structure completely, which enable with a XML document to be able to generate
other types of documents (PostScript, HTML, Tex, rtf,...) or a XML file of different structure.

So, the data structuring (defined by XML) and their representation (defined by a language such as XSL)
are separate. That means that it is possible with a XML document to create documents using various
representations (HTML to create Web pages, WML for WAP mobiles,...).

|presentationxsl|

XSL document structure
======================

	
XSL document being XML document, it starts obligatorily with the following tag::

   <?xml version="1.0" encoding="ISO-8859-1"?>

In other part, any XSL style sheet are include between the tag <xsl:stylesheet... > and </xsl:stylesheet>. 
The tag xsl:stylesheet encapsulates tags xsl:template defining elements of XML document transformations.

::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template ... >
          <!-- transformation to make -->
      </xsl:template >
   </xsl:stylesheet>

Association of XSL sheet with XML document
==========================================

	
XSL style sheet (saved in a .xsl extension file) can be related to a XML document 
(To XML document uses XSL sheet) inserting the following tag in the beginning of XML document::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <?xml-stylesheet href="file.xsl" type="text/xsl"?>


Template rules
==============

Template rules are XSL tags to define operations to be realized on XML document's elements
using XSL page, transform a XML tag to at least a HTML tag (generally several).

So XML tag following::

   <person>
      <name>Pillou</name>
      <firstName>Jean-François</firstName>
   </person>
   <person>
      <name>VanHaute</name>
      <firstName>Nico</firstName>
   </person>
   <person>
      <name>Andrieu</name>
      <firstName>Seb</firstName>
   </person>

Will be transform to following HTML tags::

   <ul>
      <li>Pillou - Jean-François</li>
      <li>VanHaute - Nico</li>
      <li>Andrieu - Seb</li>
   </ul>

The "match" attribute of the <xsl:template> tag enable to define (with XPath notation)
the XML document's elements on which the transformation applies.

Xpath notation enables to define patterns, character strings enable to locate
a node in XML document. Principals patterns are:

=======  =============  =================================================================
Pattern  Exemple        Signification   
=======  =============  =================================================================
``|``    left|right     Indicate an alternative (a node either the other (or both)) 
/        person/name   	Access path to the elements (person/arm/left) 
*        *              Pattern "joker" indicating any element 
//       //person       Indicate all the node descendants
.        .              Characterize the current node 
``..``   ``..``         Indicate the parent node
@        @value        	Indicate a characteristic attribute 
=======  =============  =================================================================

	
The transformation can be made:

- by addition of text, 
- by defining element transformation, elements which can define transformation rules
  to be applied to the selected elements by match attribute  
  
This is an example of XSL sheet to transform XML towards HTML::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template match="/">
         <HTML>
             <HEAD>
               <TITLE>Page title</TITLE>
             </HEAD>
                 <BODY BGCOLOR="#FFFFFF">
                <xsl:apply-templates/>
             </BODY>
         </HTML>
      </xsl:template >
      <xsl:template match="personne" >
         <ul>
            <li>
               <xsl:value-of select="name"/>
                 ...
               <xsl:value-of select="firstName"/>
            </li>
         </ul>
      </xsl:template >
   </xsl:stylesheet>

	
This XSL sheet signification :

* The first <xsl:template> tag makes possible to apply a transformation to the whole of the document
  (the value "/" of match attribute indicates the XML document's root element). This tag contains HTML tags 
  which will be transmitted in the tree result.
* The <xsl:apply-templates/> element indicates the root's direct children treatment.
* The <xsl:template match="person"> tag transform person type elements.
* Finally the two elements <xsl:value-of select="name"/> and <xsl:value-of select="firstName"/>
  return name and firstname tags values.

Links
=====

- Article : http://www.commentcamarche.net/xml/xmlxsl.php3
- Diagram : http://fr.wikipedia.org/wiki/Extended_stylesheet_language_transformations

.. |presentationxsl| image:: image/presentationXSL.png 

