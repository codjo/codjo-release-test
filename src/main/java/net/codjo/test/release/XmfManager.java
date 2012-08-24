package net.codjo.test.release;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import net.codjo.util.file.FileUtil;
import net.codjo.variable.basic.BasicVariableReplacer;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XmfManager {
    private static final String VARIABLE_NAME_SEPARATOR = "@";
    private static final String CALL_METHOD_TAG = "call-method";
    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String GROUP_TAG = "<group name=\"";
    private static final String GROUP_TAG_PATTERN = "<group\\s*name=\"";
    private File currentDir;
    private final String releaseTestFile;
    private int nbMethodCalls = 0;

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
                nbMethodCalls += releaseTestBuilder.getNbMethodCalls();
            }
            else {
                output.append(element);
            }
        }
        return output.toString();
    }

    public int getNbMethodCalls() {
        return nbMethodCalls;
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

    static String computeMessageDuplicateParameterValue(String parameterName) {
        return "The parameter '" + removeVariableNameSeparators(parameterName) + "' is specified as CDATA and as an attribute.";
    }

    static String computeMessageParameterNameIsEmpty(String releaseTestFile) {
        return "The parameter's name is blank in " + releaseTestFile;
    }

    static String computeMessageRequiredParameterNotProvided(String releaseTestFile, String... parameterNames) {
        StringBuilder requiredParametersNotFilled = new StringBuilder("These parameters are required but not provided in ");
        requiredParametersNotFilled.append(releaseTestFile).append(" : ");
        boolean first = true;
        for (String param : parameterNames) {
            if (!first) {
                requiredParametersNotFilled.append(", ");
            }
            first = false;

            requiredParametersNotFilled.append('\'').append(param).append('\'');
        }
        return requiredParametersNotFilled.toString();
    }

    private static String completeVariableName(String varName) {
        return VARIABLE_NAME_SEPARATOR + varName + VARIABLE_NAME_SEPARATOR;
    }

    private static String removeVariableNameSeparators(String varName) {
        String result = varName;
        if (result.startsWith(VARIABLE_NAME_SEPARATOR)) {
            result = result.substring(VARIABLE_NAME_SEPARATOR.length());
        }
        if (result.endsWith(VARIABLE_NAME_SEPARATOR)) {
            result = result.substring(0, result.length() - VARIABLE_NAME_SEPARATOR.length());
        }
        return result;
    }

    public class ReleaseTestBuilder extends DefaultHandler {
        private String output = "";
        private String callMethodFile;
        private Map<String, String> parameters = new HashMap<String, String>();
        private String currentParameter;
        private boolean currentParameterHasValueAttribute;

        private int nbMethodCalls = 0;

        public String getOutput() {
            return output;
        }

        public int getNbMethodCalls() {
            return nbMethodCalls;
        }

        @Override
        public void characters(char[] chars, int start, int length) throws SAXException {
            String charsToadd = new String(chars, start, length);
            if (currentParameter != null) {
                if (currentParameterHasValueAttribute) {
                    throw new IllegalArgumentException(computeMessageDuplicateParameterValue(currentParameter));
                } else {
                    String currentValue = parameters.get(currentParameter);
                    if (currentValue == null) {
                        currentValue = "";
                    }
                    currentValue += charsToadd;
                    parameters.put(currentParameter, currentValue);
                }
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
                String value = attributes.getValue("value");
                if (value != null) {
                    currentParameterHasValueAttribute = true;
                    parameters.put(currentParameter, value);
                }
            }
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
                currentParameterHasValueAttribute = false;
            }
        }


        private void addTemplate() throws IOException, SAXException, ParserConfigurationException {
            File resource = new File(currentDir, callMethodFile);
            String templateFile = FileUtil.loadContent(resource);

            MethodFileHandler templateHandler = new MethodFileHandler(resource.getAbsolutePath());
            readXML(getXmlReader(), templateHandler, templateFile);

            // check required parameters
            // and fill optional ones with an empty string
            List<String> requiredParametersNotFilled = new ArrayList<String>();
            for (Parameter param : templateHandler.getParameters()) {
                String varName = completeVariableName(param.name);
                if (!parameters.containsKey(varName)) {
                    if (param.required) {
                        requiredParametersNotFilled.add(param.name);
                    } else {
                        parameters.put(varName, "");
                    }
                }
            }
            if (!requiredParametersNotFilled.isEmpty()) {
                String[] params = requiredParametersNotFilled.toArray(new String[requiredParametersNotFilled.size()]);
                throw new IOException(computeMessageRequiredParameterNotProvided(releaseTestFile, params));
            }

            int beginIndex = templateFile.indexOf("<body>") + 6;
            int endIndex = templateFile.lastIndexOf("</body>");
            String body = templateFile.substring(beginIndex, endIndex);

            output += BasicVariableReplacer.replaceKeysPerValues(body, parameters);
            output = completeGroupNameWithMethodeAndParametersValues();

            nbMethodCalls++;
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
                    throw new SAXException(computeMessageParameterNameIsEmpty(releaseTestFile));
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
