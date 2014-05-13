package com.docprocess.dx.transformation;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.DataFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class XmlToPDFBuilder implements Processor, DataFormat {

    private JasperReport jasperReport;

    public void init(URL config) {
        try {
            jasperReport = JasperCompileManager.compileReport(config.openStream());
        } catch (JRException e) {
            throw new RuntimeException("Exception compiling jasper report", e);
        } catch (IOException ioe) {
            throw new RuntimeException("Exception opening jasper config", ioe);
        }
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        byte[] xmlBytes = exchange.getIn().getBody(byte[].class);
        exchange.getIn().setBody(transform(xmlBytes));
    }

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        byte[] xmlBytes = exchange.getContext().getTypeConverter().mandatoryConvertTo(byte[].class, graph);
        stream.write(transform(xmlBytes));
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        throw new UnsupportedOperationException("Cannot unmarshall PDF");
    }

    public byte[] transform(byte[] data) throws JRException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JRExporter exporter = new JRPdfExporter();
        JRXmlDataSource ds = new JRXmlDataSource(new ByteArrayInputStream(data));

        JasperPrint jasperPrint = JasperFillManager.fillReport(getJasperReport(), parameters, ds);

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bos);
        exporter.exportReport();

        return bos.toByteArray();
    }

    JasperReport getJasperReport() {
        return jasperReport;
    }
}
