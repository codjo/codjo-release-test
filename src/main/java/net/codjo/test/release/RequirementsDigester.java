/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.test.release;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
/**
 * 
 */
public class RequirementsDigester {
    private String releaseTestsRoot;
    private String projectName;
    private Map pathesByfunctionIds;
    private File outputDirectory;

    public void generateXdocs(String releaseTestsDirectory, String targetDirectory, String projectNameParam)
            throws IOException, ParserConfigurationException, TransformerException, SAXException {
        generate(releaseTestsDirectory, targetDirectory + File.separator + "generated-xdocs", projectNameParam);
    }


    public void generate(String releaseTestsDirectory, String xdocsOutputDirectory, String reportOutputName)
            throws ParserConfigurationException, TransformerException, IOException, SAXException {
        this.outputDirectory = new File(xdocsOutputDirectory);
        this.releaseTestsRoot = releaseTestsDirectory;
        outputDirectory.mkdirs();
        if (!outputDirectory.exists()) {
            throw new FileNotFoundException(outputDirectory.getPath());
        }

        this.projectName = reportOutputName;

        Document document = synthesizeAllRequirements();

        generateSyntheticXdoc(document, toStream("all-requirements.xsl"));
        generateRequirementsXdocs(toStream("requirement.xsl"));
    }


    private Document synthesizeAllRequirements() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("requirements");
        document.appendChild(rootElement);

        RequirementsFinder requirementsFinder = new RequirementsFinder(releaseTestsRoot);
        Set allPathes = requirementsFinder.getPathes();
        pathesByfunctionIds = new HashMap();
        for (Iterator iterator = allPathes.iterator(); iterator.hasNext();) {
            File requirementFile = (File)iterator.next();

            Element fileElement = document.createElement("file");
            fileElement.setAttribute("path", requirementFile.getPath());
            String functionId = extractFunctionId(requirementFile.getPath());
            fileElement.setAttribute("functionId", functionId);
            rootElement.appendChild(fileElement);
            pathesByfunctionIds.put(functionId, requirementFile.getPath());
        }
        return document;
    }


    private void generateSyntheticXdoc(Document document, InputStream stylesheet)
            throws TransformerException, IOException {
        Transformer transformer = createTransformer(stylesheet);
        transformer.setParameter("projectName", projectName);

        Source domSource = new DOMSource(document);

        File syntheticXdoc = new File(outputDirectory, projectName + ".xml");
        FileWriter fileWriter = new FileWriter(syntheticXdoc);
        StreamResult streamResult = new StreamResult(fileWriter);
        transformer.transform(domSource, streamResult);
    }


    private void generateRequirementsXdocs(InputStream stylesheet)
            throws TransformerException, ParserConfigurationException, IOException, SAXException {
        Transformer transformer = createTransformer(stylesheet);
        transformer.setParameter("projectName", projectName);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        for (Iterator iterator = pathesByfunctionIds.keySet().iterator(); iterator.hasNext();) {
            String functionId = (String)iterator.next();
            String requirementFilePath = (String)pathesByfunctionIds.get(functionId);

            File inputRequirementFile = new File(requirementFilePath);
            Document document = documentBuilder.parse(inputRequirementFile);
            Source xmlSource = new DOMSource(document);

            File outputRequirementFile =
                new File(outputDirectory, projectName + "_" + functionId + "_testcases.xml");
            FileWriter fileWriter = new FileWriter(outputRequirementFile);

            transformer.setURIResolver(new UriResolver(releaseTestsRoot + File.separator + functionId));
            transformer.setParameter("functionId", functionId);
            transformer.transform(xmlSource, new StreamResult(fileWriter));

            copySpecifications(requirementFilePath, functionId);
        }
    }


    private void copySpecifications(String requirementFilePath, String functionId) {
        int endPosition = requirementFilePath.lastIndexOf("_requirement.xrl");
        String directory = requirementFilePath.substring(0, endPosition);

        File fromFile = new File(directory + File.separator + "xdocs", "index.xml");
        File destinationFile =
            new File(outputDirectory, projectName + "_" + functionId + "_specifications.xml");

        Copy copy = new Copy();

        // copy.setProject(getProject());
        copy.setProject(new Project());
        copy.setTaskName("copy");
        copy.setFile(fromFile);
        copy.setTofile(destinationFile);
        copy.setOverwrite(true);
        copy.execute();
    }


    private Transformer createTransformer(InputStream stylesheet)
            throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource xslStreamSource = new StreamSource(stylesheet);
        Transformer transformer = transformerFactory.newTransformer(xslStreamSource);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        return transformer;
    }


    private String extractFunctionId(String filePath) {
        //"D:\dev\projects\LIB\agf-test\release\conf\test\IntegrationGimw\_requirement.xrl"
        String suffix = File.separator + "_requirement.xrl";
        String result = null;
        if (filePath.endsWith(suffix)) {
            int endPosition = filePath.lastIndexOf(suffix);
            String restOfPath = filePath.substring(0, endPosition);
            int beginPosition = restOfPath.lastIndexOf(File.separator) + 1;
            result = filePath.substring(beginPosition, endPosition);
        }
        return result;
    }


    public static void main(String[] arguments)
            throws ParserConfigurationException, IOException, TransformerException, SAXException {
        new RequirementsDigester().generateXdocs("D:\\dev\\projects\\LIB\\agf-test\\release\\conf\\test",
            "C:\\tmp\\RequirementsDigester", "Roses");
    }


    private InputStream toStream(String name) {
        return getClass().getResourceAsStream(name);
    }

    private static class UriResolver implements URIResolver {
        private String releaseTestsRoot;

        UriResolver(String releaseTestsRoot) {
            this.releaseTestsRoot = releaseTestsRoot;
        }

        public Source resolve(String systemId, String base) {
            String resourcePath = releaseTestsRoot + File.separator + systemId;
            return new StreamSource(resourcePath);
        }
    }
}
