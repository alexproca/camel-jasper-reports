/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine.export;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.export.ooxml.GenericElementPptxHandler;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporterContext;
import net.sf.jasperreports.engine.util.HtmlPrintElement;
import net.sf.jasperreports.engine.util.HtmlPrintElementUtils;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 * @version $Id: HtmlElementPptxHandler.java 5876 2013-01-07 19:05:05Z teodord $
 */
public class HtmlElementPptxHandler implements GenericElementPptxHandler 
{
	@Override
	public void exportElement(JRPptxExporterContext exporterContext,
			JRGenericPrintElement element) 
	{
		JRPptxExporter exporter = (JRPptxExporter)exporterContext.getExporter();
		try 
		{
			exporter.exportImage(getImage(exporterContext, element));
		} catch (JRException e) 
		{
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public JRPrintImage getImage(JRPptxExporterContext exporterContext,
			JRGenericPrintElement element) throws JRException {
		
		HtmlPrintElement htmlPrintElement = HtmlPrintElementUtils.getHtmlPrintElement();
		return htmlPrintElement.createImageFromElement(element);
	}

	public boolean toExport(JRGenericPrintElement element) 
	{
		return true;
	}
}
