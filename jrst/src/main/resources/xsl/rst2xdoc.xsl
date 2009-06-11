<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
	xmlns="http://www.w3.org/TR/xhtml1/strict">

<!-- xdoc is mostly an xhtml document wrapped inside a DOCUMENT tag -->
<xsl:import href="rst2xhtml.xsl"/>

  <xsl:template match="/document">
    <document>
        <properties>
            <title><xsl:value-of select="title"/></title>
        </properties>
    	<section>
          <xsl:attribute name="name"><xsl:value-of select="title"/></xsl:attribute>
	      <xsl:apply-templates /> 
		</section>
    </document>
  </xsl:template>

  <xsl:template match="title">
    <xsl:choose>
      <xsl:when test="count(ancestor::section) = 0">
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="h{count(ancestor::section) + 1}">
          <xsl:apply-templates/>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


</xsl:transform>

