========================================
Le XSL (Extensible Stylesheets Language)
========================================

.. contents:: Sommaire

Présentation
============

XML est un langage de structuration des données, et non de représentation des données. Ainsi XSL
(eXtensible StyleSheet Language) est un langage recommandé par le W3C pour effectuer la représentation
des données de documents XML. XSL est lui-même défini avec le formalisme XML, cela signifie qu'une
feuille de style XSL est un document XML bien formé.

XSL est un langage permettant de définir des feuilles de style pour les documents XML au même titre que
les CSS (Cascading StyleSheets) pour le langage HTML ou bien DSSSL (Document Style Semantics and
Specification Language) pour le SGML. XSL est d'ailleurs inspiré de DSSSL dont il reprend beaucoup
de fonctionnalités et est compatible avec les CSS (il s'agit d'un sur-ensemble des CSS).

Toutefois, contrairement aux CSS, XSL permet aussi de retraiter un document XML afin d'en modifier totalement
sa structure, ce qui permet à partir d'un document XML d'être capable de générer d'autres types de documents
(PostScript, HTML, Tex, RTF, ...) ou bien un fichier XML de structure différente.

Ainsi la structuration des données (définie par XML) et leur représentation (définie par un langage tel que
XSL) sont séparées. Cela signifie qu'il est possible à partir d'un document XML de créer des documents
utilisant différentes représentations (HTML pour créer des pages web, WML pour les mobiles WAP, ...).

|presentationxsl|


Structure d'un document XSL
===========================

Un document XSL étant un document XML, il commence obligatoirement par la balise suivante::

   <?xml version="1.0" encoding="ISO-8859-1"?>


D'autre part, toute feuille de style XSL est comprise entre les balises <xsl:stylesheet ...> et </xsl:stylesheet>.

La balise xsl:stylesheet encapsule des balises xsl:template définissant les transformations à faire subir à certains
éléments du document XML.

::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template ... >
          <!-- traitements à effectuer -->
      </xsl:template >
   </xsl:stylesheet>


Association d'une feuille XSL à un document XML
===============================================

Une feuille de style XSL (enregistré dans un fichier dont l'extension est .xsl) peut être liée à un document
XML (de telle manière à ce que le document XML utilise la feuille XSL) en insérant la balise suivante au début
du document XML::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <?xml-stylesheet href="fichier.xsl" type="text/xsl"?>


Les template rules (règles de gabarit)
======================================

Les template rules sont des balises XSL permettant de définir des opérations à réaliser sur certains éléments
du document XML utilisant la page XSL, c'est-à-dire généralement de transformer un tag XML en au moins un tag
HTML (généralement plusieurs).

Ainsi le tag XML suivant::

   <personne>
      <nom>Pillou</nom>
      <prenom>Jean-François</prenom>
   </personne>
   <personne>
      <nom>VanHaute</nom>
      <prenom>Nico</prenom>
   </personne>
   <personne>
      <nom>Andrieu</nom>
      <prenom>Seb</prenom>
   </personne>

pourra être transformé en les tags HTML suivants::

   <ul>
      <li>Pillou - Jean-François</li>
      <li>VanHaute - Nico</li>
      <li>Andrieu - Seb</li>
   </ul>

L'attribut "match" de la balise <xsl:template> permet de définir (grâce à la notation XPath) le ou les éléments
du document XML sur lesquels s'applique la transformation.

La notation Xpath permet de définir des patterns, c'est-à-dire des chaînes de caractères permettant de repérer
un noeud dans le document XML. Les principaux patterns sont :

=======  =============  =================================================================
Pattern  Exemple        Signification   
=======  =============  =================================================================
``|``    Gauche|Milieu  Indique une alternative (un noeud ou bien l'autre (ou les deux))
/        personne/nom   Chemin d'accès aux éléments (personne/bras/gauche)
*        *              Motif "joker" désignant n'importe quel élément
//       //personne     Indique tous les descendants d'un noeud
.        .              Caractérise le noeud courant
``..``   ``..``             Désigne le noeud parent
@        @valeur        Indique un attribut caractéristique
=======  =============  =================================================================

La transformation peut être réalisée :

- soit par ajout de texte,
- soit en définissant des éléments de transformation, c'est-à-dire des éléments permettant de définir
  des règles de transformation à appliquer aux éléments sélectionnés par l'attribut match 

Voici un exemple de feuille XSL permettant d'effectuer la transformation XML vers HTML décrite ci-dessus::

   <?xml version="1.0" encoding="ISO-8859-1"?>
   <xsl:stylesheet
   xmlns:xsl="http://www.w3.org/TR/WD-xsl"
   xmlns="http://www.w3.org/TR/REC-html40"
   result-ns="">
      <xsl:template match="/">
         <HTML>
             <HEAD>
               <TITLE>Titre de la page</TITLE>
             </HEAD>
                 <BODY BGCOLOR="#FFFFFF">
                <xsl:apply-templates/>
             </BODY>
         </HTML>
      </xsl:template >
      <xsl:template match="personne" >
         <ul>
            <li>
               <xsl:value-of select="nom"/>
                 -
               <xsl:value-of select="prenom"/>
            </li>
         </ul>
      </xsl:template >
   </xsl:stylesheet>


Voici la signification de cette feuille XSL :

* Le premier tag <xsl:template> permet d'appliquer une transformation à l'ensemble du document (la valeur
  "/" de l'attribut match indique l'élément racine du document XML). Ce tag contient des balises HTML qui
  seront transmises dans l'arbre résultat.
* L'élément <xsl:apply-templates/> indique le traitement de tous les enfants directs de la racine.
* La balise <xsl:template match="personne"> permet d'aller traiter les éléments de type personne.
* Enfin les deux éléments <xsl:value-of select="nom"/> et <xsl:value-of select="prenom"/> permettent de
  recuperer les valeurs des balises nom et prenom.

Références
==========

- Article : http://www.commentcamarche.net/xml/xmlxsl.php3
- Schéma  : http://fr.wikipedia.org/wiki/Extended_stylesheet_language_transformations

.. |presentationxsl| image:: images/presentationXSL.png 