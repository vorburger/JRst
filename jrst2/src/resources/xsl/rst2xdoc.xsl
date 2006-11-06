<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:transform version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
	xmlns="http://www.w3.org/TR/xhtml1/strict">

<!-- xdoc is mostly an xhtml document wrapped inside a DOCUMENT tag -->
<xsl:import href="rst2xhtml.xsl"/>

  <xsl:template match="/document">
    <document>
    	<section name="">
	      <xsl:apply-templates /> 
		</section>
    </document>
  </xsl:template>

</xsl:transform>

