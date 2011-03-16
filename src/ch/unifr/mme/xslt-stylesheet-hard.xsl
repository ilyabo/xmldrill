<?xml version="1.0" encoding="utf-8" ?>
<xsl:stylesheet version='1.0' xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>

    <xsl:template match="/">
        <r><xsl:apply-templates/></r>
    </xsl:template>

    <xsl:template match="*">
        <x> <xsl:apply-templates/></x>
    </xsl:template>

    <xsl:template match="C">
        <c><xsl:apply-templates/></c>
    </xsl:template>

    <xsl:template match="C/B">
        <cb/>
    </xsl:template>

    <xsl:template match="A/*/C">
        <a-c/>
    </xsl:template>

    <xsl:template match="C|D">
        <cd><xsl:apply-templates/></cd>
    </xsl:template>

    <xsl:template match="*[@C]">
        <cc><xsl:value-of select="@C"/></cc>
    </xsl:template>

</xsl:stylesheet>
