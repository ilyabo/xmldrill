<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

    <xsl:template match="/">
        <html><body><xsl:apply-templates/></body></html>
    </xsl:template>

    <xsl:template match="paragraph">
        <p><xsl:apply-templates/></p>
    </xsl:template>

    <xsl:template match="section">
        <h1><xsl:value-of select="@title"/></h1>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="data">
        <h1>Dataset: <xsl:value-of select="@name"/></h1>
        <table border="1">
            <tr><th>Name</th><th>Value</th></tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="item">
        <tr>
            <td><xsl:value-of select="@name"/></td>
            <td><xsl:value-of select="text()"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
