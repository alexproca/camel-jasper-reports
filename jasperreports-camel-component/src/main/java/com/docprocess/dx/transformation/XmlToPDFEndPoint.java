package com.docprocess.dx.transformation;

import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedOperation;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.impl.ProcessorEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

@ManagedResource(description = "Managed JasperPDFEndpoint")
@UriEndpoint(scheme = "jasperpdf")
public class XmlToPDFEndPoint extends ProcessorEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(XmlToPDFEndPoint.class);

    private volatile boolean cacheCleared;
    private XmlToPDFBuilder jasperReport;
    @UriParam
    private String resourceUri;
    @UriParam
    private boolean cacheReport;

    public XmlToPDFEndPoint(String endpointUri,
                            Component component,
                            XmlToPDFBuilder pdfBuilder,
                            String resourceUri,
                            boolean cacheReport) throws Exception {
        super(endpointUri, component, pdfBuilder);
        this.jasperReport = pdfBuilder;
        this.resourceUri = resourceUri;
        this.cacheReport = cacheReport;
    }

    @ManagedOperation(description = "Clears the cached XSLT stylesheet, forcing to re-load the stylesheet on next request")
    public void clearCachedStylesheet() {
        this.cacheCleared = true;
    }

    @ManagedAttribute(description = "Whether the XSLT stylesheet is cached")
    public boolean isCacheReport() {
        return cacheReport;
    }

    @ManagedAttribute(description = "Endpoint State")
    public String getState() {
        return getStatus().name();
    }

    @ManagedAttribute(description = "Camel ID")
    public String getCamelId() {
        return getCamelContext().getName();
    }

    @ManagedAttribute(description = "Camel ManagementName")
    public String getCamelManagementName() {
        return getCamelContext().getManagementName();
    }

    public XmlToPDFEndPoint findOrCreateEndpoint(String uri, String newResourceUri) {
        String newUri = uri.replace(resourceUri, newResourceUri);
        LOG.trace("Getting endpoint with URI: {}", newUri);
        return getCamelContext().getEndpoint(newUri, XmlToPDFEndPoint.class);
    }

    @Override
    protected void onExchange(Exchange exchange) throws Exception {
        if (!cacheReport || cacheCleared) {
            loadResource(resourceUri);
        }
        super.onExchange(exchange);
    }

    /**
     * Loads the resource.
     *
     * @param resourceUri the resource to load
     * @throws javax.xml.transform.TransformerException is thrown if error loading resource
     * @throws java.io.IOException                      is thrown if error loading resource
     */
    protected void loadResource(String resourceUri) throws IOException {
        LOG.trace("{} loading report resource: {}", this, resourceUri);
        URL resourceURL = convertUriToURL(resourceUri);
        jasperReport.init(resourceURL);
        cacheCleared = false;
    }

    private URL convertUriToURL(String resourceUri) {
        return getClass().getClassLoader().getResource(resourceUri);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        loadResource(resourceUri);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
}