<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/msg">
<eeml>
  <environment>
      <data id="0">
      <current_value><xsl:value-of select="tmpr" /></current_value>
      </data>
      <xsl:variable name="sensorId"> <xsl:value-of select="sensor" /></xsl:variable>  

      <data id="{$sensorId+1}">
      <current_value> <xsl:value-of select="ch1/watts" />  </current_value>
      </data>
  </environment>
</eeml>	
	</xsl:template>
</xsl:stylesheet>

	
