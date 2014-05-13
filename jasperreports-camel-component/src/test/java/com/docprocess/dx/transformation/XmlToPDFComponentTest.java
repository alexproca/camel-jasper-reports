package com.docprocess.dx.transformation;

import com.google.common.io.Resources;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class XmlToPDFComponentTest extends org.apache.camel.test.junit4.CamelTestSupport {
    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        CamelContext context = context();
        context.addComponent("jasperpdf", new XmlToPDFComponent());

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:fileinput")
                        .to("jasperpdf://reports/OrdersReport.jrxml")
                        .to("mock:end")
                        .to("file://target/marshalled");
            }

        };
    }

    @Test
    public void testComponent() throws Exception {
        byte[] xmlContent = Resources.toByteArray(Resources.getResource("data/northwind.xml"));

        Exchange orderExchange = createExchangeWithBody(xmlContent);

        orderExchange.getIn().setHeader(Exchange.FILE_NAME, "test.pdf");

        template.send("direct:fileinput", orderExchange);

        endEndpoint.setMinimumExpectedMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    public MockEndpoint getStartEndpoint() {
        return startEndpoint;
    }

    public void setStartEndpoint(MockEndpoint startEndpoint) {
        this.startEndpoint = startEndpoint;
    }

    public MockEndpoint getEndEndpoint() {
        return endEndpoint;
    }

    public void setEndEndpoint(MockEndpoint endEndpoint) {
        this.endEndpoint = endEndpoint;
    }

    @EndpointInject(uri = "mock:end")
    private MockEndpoint startEndpoint;
    @EndpointInject(uri = "mock:end")
    private MockEndpoint endEndpoint;
}
