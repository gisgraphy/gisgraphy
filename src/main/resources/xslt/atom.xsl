<?xml version='1.0' encoding='UTF-8'?>

<!-- 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 -->

<!-- 
  Simple transform of Solr query results to Atom
 -->

<xsl:stylesheet version='1.0'
    xmlns:xsl='http://www.w3.org/1999/XSL/Transform'  xmlns:georss="http://www.georss.org/georss">

  <xsl:output method="xml" encoding="UTF-8"  media-type="text/xml; charset=UTF-8" />

  <xsl:template match='/'>
      <feed xmlns="http://www.w3.org/2005/Atom" xmlns:georss="http://www.georss.org/georss" >
      <title>Gisgraphy</title>
      <subtitle>
      Free open sources framework and GIS Services
      </subtitle>
      <author>
        <name>David MASCLET</name>
        <email>davidmasclet@gisgraphy.com</email>
      </author>
      <link href="http://services.gisgraphy.com/"/>
      <updated>2008-12-01T07:02:32Z</updated>
      <id>tag:gisgraphy,2008:GIS</id>
      <xsl:apply-templates select="response/result/doc"/>
    </feed>
  </xsl:template>
    
  <!-- search results xslt -->
  <xsl:template match="doc">
    <xsl:variable name="feature_id" select="long[@name='feature_id']"/>
    <entry>
     <title><xsl:value-of select="str[@name='name']"/> (<xsl:value-of select="str[@name='placetype']"/>)</title>
     <link href="http://services.gisgraphy.com/displayfeature.html?featureId={$feature_id}"/>
      <id><xsl:value-of select="$feature_id"/></id>
  	<summary><xsl:value-of select="str[@name='name']"/></summary>
      <updated>2008-12-01T07:02:32Z</updated>
      <georss:point><xsl:value-of select="double[@name='lat']"/><xsl:text disable-output-escaping="yes"> </xsl:text>
	  <xsl:value-of select="double[@name='lng']"/></georss:point>
    </entry>
  </xsl:template>

</xsl:stylesheet>