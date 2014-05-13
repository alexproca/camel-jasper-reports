package com.docprocess.dx.transformation;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.xml.XsltBuilder;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class XmlToPDFComponent extends UriEndpointComponent {

    private static final Logger LOG = LoggerFactory.getLogger(XmlToPDFComponent.class);
    private boolean contentCache = true;

    public XmlToPDFComponent() {
        super(XmlToPDFEndPoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, final String remaining, Map<String, Object> parameters) throws Exception {
        String resourceUri = remaining;
        LOG.debug("{} using schema resource: {}", this, resourceUri);
        final XmlToPDFBuilder xmlToPDF = getCamelContext().getInjector().newInstance(XmlToPDFBuilder.class);

        // default to use the cache option from the component if the endpoint did not have the contentCache parameter
        boolean cache = getAndRemoveParameter(parameters, "contentCache", Boolean.class, contentCache);

        // if its a http uri, then append additional parameters as they are part of the uri
        if (ResourceHelper.isHttpUri(resourceUri)) {
            resourceUri = ResourceHelper.appendParameters(resourceUri, parameters);
        }

        configureJasper(xmlToPDF, uri, remaining, parameters);

        return new XmlToPDFEndPoint(uri, this, xmlToPDF, resourceUri, cache);
    }

    protected void configureJasper(XmlToPDFBuilder jasperPdfBuilder, String uri, String remaining, Map<String, Object> parameters) throws Exception {
        setProperties(jasperPdfBuilder, parameters);
    }

    protected void configureOutput(XsltBuilder xslt, String output) throws Exception {
        if (ObjectHelper.isEmpty(output)) {
            return;
        }
        if ("bytes".equalsIgnoreCase(output)) {
            xslt.outputBytes();
        } else if ("file".equalsIgnoreCase(output)) {
            xslt.outputFile();
        } else {
            throw new IllegalArgumentException("Unknown output type: " + output);
        }
    }

}