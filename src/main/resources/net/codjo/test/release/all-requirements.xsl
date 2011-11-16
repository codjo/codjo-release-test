<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="iso-8859-1" indent="yes"/>

    <xsl:param name="projectName">[[ProjectName]]</xsl:param>

    <xsl:template match="requirements">
        <document>
            <properties>
                <title>User Stories</title>
            </properties>
            <body>
                <section name="User stories">
                    <table>
                        <xsl:apply-templates select="file"/>
                    </table>
                </section>
            </body>
        </document>
    </xsl:template>


    <xsl:template match="file">
        <xsl:variable name="requirementPath" select="@path"/>
        <xsl:variable name="functionId" select="@functionId"/>

        <tr>
            <th>
                <xsl:comment>
                    <xsl:value-of select="$requirementPath"/>
                </xsl:comment>
                <a href="{$projectName}_{$functionId}_testcases.html" alt="Détails...">
                    <xsl:value-of select="document($requirementPath)/function/name"/>
                </a>
            </th>
            <td>
                <xsl:apply-templates select="document($requirementPath)/function/description"/>
            </td>
        </tr>
    </xsl:template>


    <xsl:template match="description">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
</xsl:stylesheet>
