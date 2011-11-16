<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="iso-8859-1" indent="yes"/>

    <xsl:param name="wikiPageName" select="function/@id"/>
    <xsl:param name="projectName">[[ProjectName]]</xsl:param>
    <xsl:param name="functionId">[[FunctionId]]</xsl:param>


    <xsl:template match="function">
        <document>
            <properties>
                <title>[User Story]
                    <xsl:value-of select="name"/>
                </title>
            </properties>
            <body>
                <section name="{name}">
                    <a href="{$projectName}.html">Toutes les stories</a>
                    |
                    <xsl:call-template name="warningIfEmpty">
                        <xsl:with-param name="content" select="documents/specification"/>
                        <xsl:with-param name="name">spécification</xsl:with-param>
                    </xsl:call-template>
                    |
                    <a href="http://4ddev02/vqwiki/jsp/Wiki?{$projectName}{$wikiPageName}">Page Wiki</a>

                    <subsection name="Description">
                        <xsl:apply-templates select="description"/>
                    </subsection>

                    <xsl:apply-templates select="documents"/>
                    <xsl:apply-templates select="audit"/>
                </section>
            </body>
        </document>
    </xsl:template>

    <xsl:template match="specification">
        <a href="{$projectName}_{$functionId}_specifications.html">Page des spécifications</a>
    </xsl:template>

    <xsl:template match="description">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>

    <xsl:template match="documents">
        <subsection name="Cas de test">
            <table>
                <xsl:apply-templates select="test"/>
            </table>
        </subsection>
    </xsl:template>

    <xsl:template match="test">
        <xsl:variable name="link" select="@href"/>
        <xsl:if test="contains($link,'.xml')">
            <xsl:variable name="fileName" select="substring-before($link,'.xml')"/>
            <tr>
                <th>
                    <xsl:value-of select="$fileName"/>
                </th>
                <td>
                    <xsl:call-template name="warningIfEmpty">
                        <xsl:with-param name="content" select="document($link)/release-test/description"/>
                        <xsl:with-param name="name">description</xsl:with-param>
                    </xsl:call-template>

                    <xsl:if test="document($link)/release-test/description/@mantis">
                        <p>
                            <a targte="_blank"
                               href="http://4ddev02:8080/mantis/bug_view_page.php?bug_id={document($link)/release-test/description/@mantis}">
                                Relatif au bug Mantis n°
                                <xsl:value-of select="document($link)/release-test/description/@mantis"/>
                            </a>
                        </p>
                    </xsl:if>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>

    <xsl:template match="audit">
        <subsection name="Audit">
            <ul>
                <li>
                    <p>
                        <strong>Livraison</strong>
                        :
                        <xsl:variable name="nameTag">$Name:</xsl:variable>
                        <xsl:value-of select="substring-before(substring-after(@tag, $nameTag), '$')"/>
                    </p>
                </li>
                <li>
                    <p>
                        <strong>Version</strong>
                        :
                        <xsl:variable name="revisionTag">$Revision:</xsl:variable>
                        <xsl:value-of
                              select="substring-before(substring-after(@revision, $revisionTag), '$')"/>
                    </p>
                </li>
                <li>
                    <p>
                        <strong>Changements</strong>
                        :
                    </p>
                    <xsl:apply-templates select="log"/>
                </li>
            </ul>
        </subsection>
    </xsl:template>
    <xsl:template match="log">
        <p>
            <pre>
                <xsl:apply-templates/>
            </pre>
        </p>
    </xsl:template>

    <xsl:template name="warningIfEmpty">
        <xsl:param name="content"/>
        <xsl:param name="name"/>

        <xsl:choose>
            <xsl:when test="$content">
                <xsl:apply-templates select="$content"/>
            </xsl:when>
            <xsl:otherwise>
                <p>
                    <i>[Pas de
                        <xsl:value-of select="$name"/>
                        disponible].
                    </i>
                </p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="p">
        <p>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    <xsl:template match="ol">
        <ol>
            <xsl:apply-templates/>
        </ol>
    </xsl:template>
    <xsl:template match="ul">
        <ul>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
    <xsl:template match="li">
        <li>
            <xsl:apply-templates/>
        </li>
    </xsl:template>
    <xsl:template match="b">
        <b>
            <xsl:apply-templates/>
        </b>
    </xsl:template>

</xsl:stylesheet>
