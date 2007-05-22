<?xml version="1.0" encoding="ISO-8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/TR/xhtml1/strict">

	<xsl:output method="xml" encoding="ISO-8859-1" indent="yes"/>

	<xsl:template match="/document">	
	<html>
	  <head>
	    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-15" />
	    <meta name="generator" content="JRST http://jrst.labs.libre-entreprise.org/" />
	    <title><xsl:value-of select="title"/></title>
	  </head>
	  <body>	    
		<xsl:apply-templates/>
	  </body>
	</html>
	</xsl:template>

	<xsl:template match="title">
	   <xsl:element name="h{count(ancestor::section) + 1}">
		   <xsl:if test="@refid">
     		 <a href="#{@refid}" id="{../@id}"><xsl:apply-templates/></a>
    	   </xsl:if>
		   <xsl:if test="not(@refid)">
     		 <xsl:apply-templates/>
    	   </xsl:if>
         
       </xsl:element>
	</xsl:template>
	
	<xsl:template match="subtitle">
		<xsl:element name="h2">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	<!-- just eat it -->
	<xsl:template match="substitution_definition">
	</xsl:template>
	
	<xsl:template match="docinfo">
	  <table class="docinfo" frame="void" rules="none">
		<col class="docinfo-name" />
		<col class="docinfo-content" />
		<tbody valign="top">
			<xsl:apply-templates/>
		</tbody>
	  </table>
	</xsl:template>
	
	<xsl:template match="organization|address|contact|version|revision|status|date|copyright">
		<tr>
			<th class="docinfo-name">
				<xsl:value-of select="name(.)"/> : 
			</th>
			<td class="docinfo-content">
				 <xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
			
	<xsl:template match="author">
		<xsl:if test="not(../../authors)">
				<tr>
					<th class="docpatterninfo-name">
						<xsl:value-of select="name(.)"/> :
					</th>
					<td class="docinfo-content">
						<xsl:apply-templates/>
					</td>
				</tr>
    	</xsl:if>
		<xsl:if test="../../authors">
			<xsl:variable name="num" select="position()"/>
			<xsl:if test="$num=1">
				<tr>
					<th class="docinfo-name">
						<xsl:value-of select="authors"/>authors :
					</th>
					<td class="docinfo-content">
						<xsl:apply-templates/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$num>1">
				<tr>
					<th>
						
					</th>
					<td class="docinfo-content">
						<xsl:apply-templates/>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	
		
	<xsl:template match="transition">
	  <hr/>
	</xsl:template>

	<xsl:template match="section">
	  <xsl:apply-templates/>
	</xsl:template>

    <xsl:template match="list_item/paragraph[1] | definition_list_item/*/paragraph[1] | field/*/paragraph[1] | option/*/paragraph[1]">
            <!--XXX - Unclear how to handle multi-paragraph list items.
             | Certainly when they're single paragraphs, we don't want them
             | wrapped in a <P> tag.  This seems to work okay.
             +-->
            <xsl:apply-templates/>
    </xsl:template>

	<xsl:template match="paragraph">
	  <p><xsl:apply-templates/></p>
	</xsl:template>

	<xsl:template match="reference">
	  <a href="{@refuri}#{@refid}" id="{@id}"><xsl:apply-templates/></a>
	</xsl:template>

	<xsl:template match="emphasis">
	  <em><xsl:apply-templates/></em>
	</xsl:template>
	
	<xsl:template match="strong">
	  <b><xsl:apply-templates/></b>
	</xsl:template>
	
	<xsl:template match="literal">
		<code><xsl:value-of select="text()"/></code>
	</xsl:template>
	
	<xsl:template match="literal_block">
		<pre><xsl:value-of select="text()"/></pre>
	</xsl:template>

	<xsl:template match="bullet_list">
		<ul><xsl:apply-templates/></ul>
	</xsl:template>

	<xsl:template match="enumerated_list">
		<ol>
		  	<xsl:choose>
			  	<xsl:when test="@enumtype='arabic'">
			  		<xsl:attribute name="type">1</xsl:attribute>
			    </xsl:when>
				<xsl:when test="@enumtype='loweralpha'">
					<xsl:attribute name="type">a</xsl:attribute>
				</xsl:when>
				<xsl:when test="@enumtype='upperalpha'">
					<xsl:attribute name="type">A</xsl:attribute>
				</xsl:when>
	            <xsl:when test="@enumtype='lowerroman'">
	                    <xsl:attribute name="type">i</xsl:attribute>
	            </xsl:when>
	            <xsl:when test="@enumtype='upperroman'">
	                    <xsl:attribute name="type">I</xsl:attribute>
	            </xsl:when>
            </xsl:choose>
	        <xsl:copy-of select="@start"/>
			<xsl:apply-templates/>
		</ol>
	</xsl:template>

	<xsl:template match="list_item">
		<li><xsl:apply-templates/></li>
	</xsl:template>

	<xsl:template match="field_list">
		<div class="field_list"><xsl:apply-templates/></div>
	</xsl:template>

	<xsl:template match="field">
		<div class="field"><xsl:apply-templates/></div>
	</xsl:template>

	<xsl:template match="field_name">
		<span class="field_name"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="field_body">
		<span class="field_body"><xsl:apply-templates/></span>
	</xsl:template>

	<xsl:template match="definition_list">
		<dl class="definition_list"><xsl:apply-templates/></dl>
	</xsl:template>

	<xsl:template match="definition_list_item">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="term">
		<dt class="term"><xsl:apply-templates/><xsl:call-template name="classifier"/></dt>
	</xsl:template>

	<xsl:template name="classifier">
	    <xsl:for-each select="../classifier">
			<span class="classifier"><xsl:apply-templates/></span>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="classifier">
		<!-- do nothing -->
	</xsl:template>

	<xsl:template match="definition">
		<dd class="definition"><xsl:apply-templates/></dd>
	</xsl:template>

	<xsl:template match="image">
		<img alt="{@alt}" src="{@uri}"><xsl:apply-templates/></img>
	</xsl:template>

	<xsl:template match="header">
		<p class="header"><xsl:apply-templates/></p>
		<hr/>
	</xsl:template>
	
	
	<!--
	 | Table
	 +-->
	 
	<xsl:template match="table">
		<table border="1">
			<colgroup>
				<xsl:apply-templates select="tgroup/colspec"/>
			</colgroup>
			<xsl:apply-templates select="./tgroup/thead|./tgroup/tbody"/>
		</table>
	</xsl:template>

	<xsl:template match="tgroup/colspec">
		<col width="{@colwidth}%"/>
	</xsl:template>

	<xsl:template match="row">
		<tr><xsl:apply-templates/></tr>
	</xsl:template>

	<xsl:template match="thead">
		<thead><xsl:apply-templates/></thead>
	</xsl:template>

	<xsl:template match="thead/row/entry">
		<th>
			<xsl:if test="@morecols"><xsl:attribute name="colspan"><xsl:value-of select="@morecols+1"/></xsl:attribute></xsl:if>
			<xsl:if test="@morerows"><xsl:attribute name="rowspan"><xsl:value-of select="@morerows+1"/></xsl:attribute></xsl:if>
			<xsl:apply-templates/>
		</th>
	</xsl:template>

	<xsl:template match="tbody">
		<tbody><xsl:apply-templates/></tbody>
	</xsl:template>

	<xsl:template match="tbody/row/entry">
		<td>
			<xsl:if test="@morecols"><xsl:attribute name="colspan"><xsl:value-of select="@morecols+1"/></xsl:attribute></xsl:if>
			<xsl:if test="@morerows"><xsl:attribute name="rowspan"><xsl:value-of select="@morerows+1"/></xsl:attribute></xsl:if>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	
	<xsl:template match="admonition">
		<div class="admonition">
			<div class="{@class}">
				<p class="{title}">
					<xsl:apply-templates select="./title"/>
				</p>
				<p class="body">
					<xsl:apply-templates select="child::*[position()>1]"/>
				</p>
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="attention|caution|danger|error|hint|important|note|tip|warning">
		<div class="{name(.)}">
			<p class="title"><xsl:value-of select="name(.)"/> :</p>
			<p class="body">
				<xsl:apply-templates/>
			</p>
		</div>
	</xsl:template>
	
	<xsl:template match="block_quote">
		<div class="block_quote">
			<p><xsl:apply-templates select="child::*[position()=1]"/></p>
			<xsl:if test="./attribution">
				<p class="attribution">
					<xsl:apply-templates select="./attribution"/>
				</p>
			</xsl:if>
		</div>
	</xsl:template>
	
	<xsl:template match="doctest_block">
		<pre class="doctest_block">
			<xsl:apply-templates/>
		</pre>
	</xsl:template>
	
	<xsl:template match="line_block">
		<div class="line_block">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="line">
		<div class="line">
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<xsl:template match="sidebar">
		<div class="sidebar">
			<p class="title">
				<xsl:apply-templates select="./title"/>
			</p>
			<xsl:if test="./subtitle">
				<p class="subtitle">
					<xsl:apply-templates select="./subtitle"/>
				</p>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="./subtitle">
					<xsl:apply-templates select="child::*[position()>2]"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="child::*[position()>1]"/>
				</xsl:otherwise>
			</xsl:choose>
			
		</div>
	</xsl:template>
	
	<xsl:template match="topic">
		<div class="topic">
			<p class="title">
				<xsl:apply-templates select="./title"/>
			</p>
			<xsl:apply-templates select="child::*[position()>1]"/>
		</div>
	</xsl:template>
	
	<xsl:template match="option_list">
		<table class="option_list">
			<col class="option" />
			<col class="description" />
			<tbody valign="top">
				
				<xsl:apply-templates/>
				
			</tbody>
		</table>
	</xsl:template>
	
	<xsl:template match="option_list_item">
		<tr>
			<td class="option-group">
				<kbd>
					<span class="option">
						<xsl:value-of
							select="option_group/option/option_string"/>
					</span>
				</kbd>
			</td>
			<td>
				<xsl:value-of select="description"/>
			</td>
		</tr>
	</xsl:template>
	
	
	<xsl:template match="footnote">
		<table class="footnote" frame="void" id="{@id}" rules="none">
			<colgroup>
				<col class="label"/>
				<col/>
			</colgroup>
			<tbody valign="top">
				<tr>
					<td class="label">
						<a class="backref" href="#{@backrefs}" name="{id}">
							[<xsl:value-of select="label"/>]
						</a>
					</td>
					<td>
						<xsl:value-of select="child::*[position()>1]"/>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>
	
</xsl:stylesheet>