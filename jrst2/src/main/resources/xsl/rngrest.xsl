<?xml version="1.0"?>

<!--

Program name: rngrest.xsl
Description: This style sheet converts annotated RELAX NG schemas
             into reStructuredText documents
Author: Ladislav Lhotka <Lhotka@cesnet.cz>

Copyright (C) 2006 CESNET

This program is free software; you can redistribute it and/or
modify it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.

$Id$
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rng="http://relaxng.org/ns/structure/1.0"
                xmlns:a="http://www.cesnet.cz/ns/rngrest-annotations/1.0"
                version="1.0">

  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
  <xsl:strip-space elements="rng:start rng:define"/>

  <xsl:variable name="NL">
    <xsl:text>
</xsl:text>
  </xsl:variable>

  <xsl:variable name="NLI">
    <xsl:text>
    </xsl:text>
  </xsl:variable>

  <xsl:variable name="NLNL">
    <xsl:value-of select="$NL"/>
    <xsl:value-of select="$NL"/>
  </xsl:variable>

  <xsl:variable name="IND">
    <xsl:text>    </xsl:text>
  </xsl:variable>

  <xsl:template name="underline">
    <xsl:param name="str"/>
    <xsl:text>!</xsl:text>
    <xsl:if test="string-length($str)&gt;1">
      <xsl:call-template name="underline">
        <xsl:with-param name="str">
          <xsl:value-of select="substring($str,2)"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- The root element -->

  <xsl:template match="rng:grammar">
    <xsl:apply-templates select="a:rest"/>
    <xsl:value-of select="$NLNL"/>
    <xsl:text>::</xsl:text>
    <xsl:value-of select="$NLNL"/>
    <xsl:text disable-output-escaping="yes">    &lt;grammar</xsl:text>
    <xsl:for-each select="@*">
      <xsl:value-of select="$NLI"/>
      <xsl:text>    </xsl:text>
      <xsl:value-of select="name()"/>
      <xsl:text>="</xsl:text>
      <xsl:value-of select="."/>
      <xsl:text>"</xsl:text>
    </xsl:for-each>
    <xsl:for-each select="namespace::*">
      <xsl:if test="name()!='xml'">
        <xsl:value-of select="$NLI"/>
        <xsl:text>    xmlns</xsl:text>
        <xsl:if test="name()!=''">
          <xsl:text>:</xsl:text>
          <xsl:value-of select="name()"/>
        </xsl:if>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:value-of select="$NLNL"/>
    <xsl:apply-templates select="rng:*" mode="listing"/>
  </xsl:template>

  <xsl:template match="rng:start" mode="listing">
    <xsl:value-of select="$NL"/>
    <xsl:text>start</xsl:text>
    <xsl:value-of select="$NL"/>
    <xsl:text>!!!!!</xsl:text>
    <xsl:value-of select="$NLNL"/>
    <xsl:apply-templates select="a:rest"/>
    <xsl:value-of select="$NLNL"/>
    <xsl:text>.. parsed-literal::</xsl:text>
    <xsl:value-of select="$NLNL"/>
    <xsl:text disable-output-escaping="yes">    &lt;</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:apply-templates select="@*"/>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:apply-templates mode="listing"/>
    <xsl:value-of select="$NLI"/>
    <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:value-of select="$NL"/>
  </xsl:template>

  <xsl:template match="rng:define" mode="listing">
    <xsl:value-of select="$NL"/>
    <xsl:variable name="elname" select="@name"/>
    <xsl:value-of select="$elname"/>
    <xsl:value-of select="$NL"/>
    <xsl:call-template name="underline">
      <xsl:with-param name="str" select="$elname"/>
    </xsl:call-template>
    <xsl:value-of select="$NLNL"/>
    <xsl:apply-templates select="a:rest"/>
    <xsl:value-of select="$NLNL"/>
    <xsl:variable name="refs"
                  select="//rng:define[descendant::rng:ref/@name=$elname]"/>
    <xsl:if test="count($refs)!=0 or //rng:start//rng:ref[@name=$elname]">
      <xsl:text>
The pattern is referenced by:
      </xsl:text>
      <xsl:value-of select="$NL"/>
      <xsl:if test="//rng:start//rng:ref[@name=$elname]">
        <xsl:text>* start_</xsl:text>
        <xsl:value-of select="$NLNL"/>
      </xsl:if>
      <xsl:for-each select="$refs">
        <xsl:text>* </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>_</xsl:text>
        <xsl:value-of select="$NLNL"/>
      </xsl:for-each>
      <xsl:value-of select="$NL"/>
    </xsl:if>
    <xsl:text>.. parsed-literal::</xsl:text>
    <xsl:value-of select="$NLNL"/>
    <xsl:text disable-output-escaping="yes">    &lt;</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:apply-templates select="@*"/>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:apply-templates mode="listing"/>
    <xsl:value-of select="$NLI"/>
    <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:value-of select="$NL"/>
  </xsl:template>

  <xsl:template match="rng:*" mode="listing">
    <xsl:if test="name(..)='define' or name(..)='start'">
      <xsl:value-of select="$NLI"/>
    </xsl:if>
    <xsl:text disable-output-escaping="yes">  &lt;</xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:apply-templates select="@*"/>
    <xsl:choose>
      <xsl:when test="count(*)=0">
    <xsl:choose>
      <xsl:when test="count(text())=0">
        <xsl:text>/</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
        <xsl:apply-templates/>
        <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
        <xsl:value-of select="name()"/>
      </xsl:otherwise>
    </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
    <xsl:apply-templates mode="listing"/>
    <xsl:text disable-output-escaping="yes">  &lt;/</xsl:text>
    <xsl:value-of select="name()"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
  </xsl:template>

  <xsl:template match="a:rest">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="a:rest" mode="listing"/>

  <xsl:template match="@*">
    <xsl:text> </xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text>="</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>"</xsl:text>
  </xsl:template>

  <xsl:template match="@*">
    <xsl:text> </xsl:text>
    <xsl:value-of select="name()"/>
    <xsl:text>="</xsl:text>
    <xsl:value-of select="."/>
    <xsl:if test="name(..)='ref' and name()='name'">
      <xsl:text>_</xsl:text>
    </xsl:if>
    <xsl:text>"</xsl:text>
  </xsl:template>

</xsl:stylesheet>