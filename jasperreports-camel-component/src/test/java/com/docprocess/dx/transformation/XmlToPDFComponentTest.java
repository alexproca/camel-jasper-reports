package com.docprocess.dx.transformation;

import com.google.common.base.Objects;
import com.google.common.io.Resources;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;

public class XmlToPDFComponentTest extends org.apache.camel.test.junit4.CamelTestSupport {

    @EndpointInject(uri = "mock:end")
    private MockEndpoint endEndpoint;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        CamelContext context = context();
        context.addComponent("jasperpdf", new XmlToPDFComponent());

        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:first-endpoint")
                        .to("jasperpdf://reports/OrdersReport.jrxml")
                        .to("mock:end")
                        .setHeader(Exchange.FILE_NAME, constant("first.pdf"))
                        .to("file://target");

                from("direct:second-endpoint")
                        .to("jasperpdf://reports/OrdersReport2.jrxml")
                        .to("mock:end")
                        .setHeader(Exchange.FILE_NAME, constant("second.pdf"))
                        .to("file://target");
            }

        };
    }

    @Test
    public void testComponent() throws Exception {
        byte[] xmlContent = Resources.toByteArray(Resources.getResource("data/northwind.xml"));
        endEndpoint.expectedMessageCount(2);

        template.send("direct:first-endpoint", createExchangeWithBody(xmlContent));
        template.send("direct:second-endpoint", createExchangeWithBody(xmlContent));

        assertMockEndpointsSatisfied();
    }

    public static URL getResource(String resourceName) {
        ClassLoader loader = Objects.firstNonNull(
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader());
        URL url = loader.getResource(resourceName);
        checkArgument(url != null, "resource %s not found.", resourceName);
        return url;
    }
}
