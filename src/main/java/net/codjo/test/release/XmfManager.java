package net.codjo.test.release;

import java.util.Collection;
import net.codjo.util.file.FileUtil;
import net.codjo.variable.basic.BasicVariableReplacer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XmfManager {
    private static final String CALL_METHOD_TAG = "call-method";
    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String GROUP_TAG = "<group name=\"";
    private static final String GROUP_TAG_PATTERN = "<group\\s*name=\"";
    private File currentDir;
    private final String releaseTestFile;

    public XmfManager(File currentDir, String releaseTestFile) {
        this.currentDir = currentDir;
        this.releaseTestFile = releaseTestFile;
    }


    public String parse(String releaseTestXml)
          throws ParserConfigurationException, SAXException, IOException {
        StringBuilder output = new StringBuilder();
        XMLReader xmlReader = getXmlReader();
        String normalizedReleaseTestXml = normalizeCallMethodTags(releaseTestXml);
        for (String element : cutFile(normalizedReleaseTestXml)) {
            if (element.startsWith("<" + CALL_METHOD_TAG)) {
                ReleaseTestBuilder releaseTestBuilder = new ReleaseTestBuilder();
                readXML(xmlReader, releaseTestBuilder, HEADER + element);
                output.append(releaseTestBuilder.getOutput());
            }
            else {
                output.append(element);
            }
        }
        return output.toString();
    }

    private <T extends DefaultHandler> void readXML(XMLReader xmlReader, T handler, String xml)
          throws IOException, SAXException {
        xmlReader.setContentHandler(handler);
        xmlReader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
    }

    public static XMLReader getXmlReader() throws SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        return factory.newSAXParser().getXMLReader();
    }


    private String normalizeCallMethodTags(String fileContent) {
        Pattern pattern = Pattern.compile("(<" + CALL_METHOD_TAG + "[^<]*)/>");
        Matcher matcher = pattern.matcher(fileContent);
        while (matcher.find()) {
            fileContent = matcher.replaceFirst(matcher.group(1) + "></" + CALL_METHOD_TAG + ">");
            matcher = pattern.matcher(fileContent);
        }
        return fileContent;
    }


    private List<String> cutFile(String completeFile)
          throws SAXException, ParserConfigurationException, IOException {
        List<String> arrayFile = new ArrayList<String>();
        int endIndex = 0;
        int beginIndex = completeFile.indexOf("<" + CALL_METHOD_TAG, endIndex);
        while (beginIndex != -1) {
//            ajout du bloc supérieur
            arrayFile.add(completeFile.substring(endIndex, beginIndex));
//            détection et ajout de la fin du group call-method
            endIndex = rightIndexOf(completeFile, "</" + CALL_METHOD_TAG + ">", beginIndex);
            arrayFile.add(completeFile.substring(beginIndex, endIndex));
            beginIndex = completeFile.indexOf("<" + CALL_METHOD_TAG, endIndex);
        }
        arrayFile.add(completeFile.substring(endIndex));
        return arrayFile;
    }


    private int rightIndexOf(String completeFile, String toFind, int beginIndex) {
        int index = completeFile.indexOf(toFind, beginIndex);
        if (index < 0) {
            return -1;
        }

        return index + toFind.length();
    }


    public class ReleaseTestBuilder extends DefaultHandler {
        private String output = "";
        private String callMethodFile;
        private Map<String, String> parameters = new HashMap<String, String>();
        private String currentParameter;

        public String getOutput() {
            return output;
        }


        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            String charsToadd = new String(chars, start, length);
            if (currentParameter != null) {
                String currentValue = parameters.get(currentParameter);
                if (currentValue == null) {
                    currentValue = "";
                }
                currentValue += charsToadd;
                parameters.put(currentParameter, currentValue);
            }
        }


        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
              throws SAXException {
            if ("call-method".equals(qName)) {
                parameters.clear();
                callMethodFile = attributes.getValue("file");
            }
            else if ("parameter".equals(qName)) {
                currentParameter = completeVariableName(attributes.getValue("name"));
                parameters.put(currentParameter, attributes.getValue("value"));
            }
        }

        private String completeVariableName(String varName) {
            return '@' + varName + '@';
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("call-method".equals(qName)) {
                try {
                    addTemplate();
                }
                catch (IOException e) {
                    throw new SAXException(e);
                }
                catch (ParserConfigurationException e) {
                    throw new SAXException(e);
                }
            }
            else if ("parameter".equals(qName)) {
                currentParameter = null;
            }
        }


        private void addTemplate() throws IOException, SAXException, ParserConfigurationException {
            File resource = new File(currentDir, callMethodFile);
            String templateFile = FileUtil.loadContent(resource);

            MethodFileHandler templateHandler = new MethodFileHandler(resource.getAbsolutePath());
            readXML(getXmlReader(), templateHandler, templateFile);

            // check required parameters
            // and fill optional ones with an empty string
            StringBuilder requiredParametersNotFilled = new StringBuilder();
            for (Parameter param : templateHandler.getParameters()) {
                String varName = completeVariableName(param.name);
                if (!parameters.containsKey(varName)) {
                    if (param.required) {
                        if (requiredParametersNotFilled.length() > 0) {
                            requiredParametersNotFilled.append(", ");
                        }
                        requiredParametersNotFilled.append(param.name);
                    } else {
                        parameters.put(varName, "");
                    }
                }
            }
            if (requiredParametersNotFilled.length() > 0) {
                requiredParametersNotFilled.insert(0, "These parameters are required but not provided in " + releaseTestFile + " : ");
                throw new IllegalArgumentException(requiredParametersNotFilled.toString());
            }

            int beginIndex = templateFile.indexOf("<body>") + 6;
            int endIndex = templateFile.lastIndexOf("</body>");
            String body = templateFile.substring(beginIndex, endIndex);

            output += BasicVariableReplacer.replaceKeysPerValues(body, parameters);
            output = completeGroupNameWithMethodeAndParametersValues();
        }

        private String completeGroupNameWithMethodeAndParametersValues() {
            StringBuilder builder = new StringBuilder();
            if (!parameters.isEmpty()) {
                builder.append("(");
                for (String key : parameters.keySet()) {
                    String value = parameters.get(key);
                    if (value.contains("<") || value.contains("&")) {
                        continue;
                    }
                    builder.append(key).append("=").append(value).append(" ");
                }
                builder.append(")");
            }
            return output.replaceAll(GROUP_TAG_PATTERN, GROUP_TAG + callMethodFile + builder + ":::");
        }
    }

    private static class Parameter {
        private final String name;
        private final boolean required;

        public Parameter(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                   "name='" + name + '\'' +
                   ", required=" + required +
                   '}';
        }
    }

    public class MethodFileHandler extends DefaultHandler {
        private Collection<Parameter> parameters = new ArrayList<Parameter>();
        private boolean insideParameters;
        private final String resource;

        public MethodFileHandler(String resource) {
            this.resource = resource;
        }

        public Collection<Parameter> getParameters() {
            return parameters;
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
              throws SAXException {
            if ("parameters".equals(qName)) {
                insideParameters = true;
            } else if (insideParameters && "parameter".equals(qName)) {
                String name = attributes.getValue("name");
                boolean required = Boolean.parseBoolean(attributes.getValue("required"));

                if (StringUtils.isBlank(name)) {
                    throw new SAXException("parameter's name is blank in " + releaseTestFile);
                }

                parameters.add(new Parameter(name, required));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("parameters".equals(qName)) {
                insideParameters = false;
            }
        }
    }
}
