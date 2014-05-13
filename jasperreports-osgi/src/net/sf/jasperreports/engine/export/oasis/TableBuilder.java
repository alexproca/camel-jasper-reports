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

/*
 * Special thanks to Google 'Summer of Code 2005' program for supporting this development
 * 
 * Contributors:
 * Majid Ali Khan - majidkk@users.sourceforge.net
 * Frank Sch�nheit - Frank.Schoenheit@Sun.COM
 */
package net.sf.jasperreports.engine.export.oasis;

import java.text.AttributedCharacterIterator;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRTextAttribute;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: TableBuilder.java 5930 2013-02-28 15:21:30Z teodord $
 */
public class TableBuilder 
{
	/**
	 *
	 */
	private final DocumentBuilder documentBuilder;
	private String tableName;
	private final JasperPrint jasperPrint;
	private int reportIndex;
	private final WriterHelper bodyWriter;
	private final WriterHelper styleWriter;
	private final StyleCache styleCache;
	private boolean isFrame;
	private boolean isPageBreak;
	

	protected TableBuilder(
		DocumentBuilder documentBuilder,
		JasperPrint jasperPrint,
		String name, 
		WriterHelper bodyWriter,
		WriterHelper styleWriter,
		StyleCache styleCache
		) 
	{
		this.documentBuilder = documentBuilder;
		this.jasperPrint = jasperPrint;

		isFrame = true;
		isPageBreak = false;
		
		this.bodyWriter = bodyWriter;
		this.styleWriter = styleWriter;
		this.styleCache = styleCache;

		this.tableName = "TBL_" + name;
	}

	protected TableBuilder(
		DocumentBuilder documentBuilder,
		JasperPrint jasperPrint,
		int reportIndex,
		int pageIndex,
		WriterHelper bodyWriter,
		WriterHelper styleWriter,
		StyleCache styleCache
		) 
	{
		this.documentBuilder = documentBuilder;
		this.jasperPrint = jasperPrint;

		isFrame = false;
		isPageBreak = (reportIndex != 0 || pageIndex != 0);
		
		this.reportIndex = reportIndex;
		this.bodyWriter = bodyWriter;
		this.styleWriter = styleWriter;
		this.styleCache = styleCache;

		this.tableName = "TBL_" + reportIndex + "_" + pageIndex;
	}


	public void buildTableStyle(int width) 
	{
		styleWriter.write(" <style:style style:name=\"" + tableName + "\"");//FIXMEODT can we have only one page style per report?
		if (!isFrame)
		{
			styleWriter.write(" style:master-page-name=\"master_" + reportIndex +"\"");
		}
		styleWriter.write(" style:family=\"table\">\n");
		styleWriter.write("   <style:table-properties");		
		styleWriter.write(" table:align=\"left\" style:width=\"" + LengthUtil.inch(width) + "in\"");
		if (isPageBreak)
		{
			styleWriter.write(" fo:break-before=\"page\"");
		}
//		FIXMEODT
//		if (tableWidth != null)
//		{
//			styleWriter.write(" style:width=\""+ tableWidth +"in\"");
//		}
//		if (align != null)
//		{
//			styleWriter.write(" table:align=\""+ align +"\"");
//		}
//		if (margin != null)
//		{
//			styleWriter.write(" fo:margin=\""+ margin +"\"");
//		}
//		if (backGroundColor != null)
//		{
//			styleWriter.write(" fo:background-color=\""+ backGroundColor +"\"");
//		}
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
	}
	
	public void buildTableHeader() 
	{
		bodyWriter.write("<table:table");
		if (isFrame)
		{
			bodyWriter.write(" is-subtable=\"true\"");
		}
		bodyWriter.write(" table:name=\"");
		bodyWriter.write(tableName);
		bodyWriter.write("\"");
		bodyWriter.write(" table:style-name=\"");
		bodyWriter.write(tableName);
		bodyWriter.write("\"");
		bodyWriter.write(">\n");
	}
	
	public void buildTableFooter() 
	{
		bodyWriter.write("</table:table>\n");
	}
	
	public void buildRowStyle(int rowIndex, int rowHeight) 
	{
		String rowName = tableName + "_row_" + rowIndex;
		styleWriter.write(" <style:style style:name=\"" + rowName + "\"");
		styleWriter.write(" style:family=\"table-row\">\n");
		styleWriter.write("   <style:table-row-properties");		
		if(rowHeight < 0)
		{
			styleWriter.write(" style:use-optimal-row-height=\"true\"");
		}
		else
		{
			styleWriter.write(" style:use-optimal-row-height=\"false\"");
			styleWriter.write(" style:row-height=\"" + LengthUtil.inch(rowHeight) + "in\"");
		}
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
	}

	public void buildRowHeader(int rowIndex) 
	{
		String rowName = tableName + "_row_" + rowIndex;
		bodyWriter.write("<table:table-row");
		bodyWriter.write(" table:style-name=\"" + rowName + "\"");
		bodyWriter.write(">\n");
	}
	
	public void buildRowFooter() 
	{
		bodyWriter.write("</table:table-row>\n");
	}
	
	public void buildRow(int rowIndex) 
	{
		if (rowIndex > 0)
		{
			buildRowFooter();
		}
		buildRowHeader(rowIndex);
	}
	
	public void buildColumnStyle(int colIndex, int colWidth) 
	{
		String columnName = tableName + "_col_" + colIndex;
		styleWriter.write(" <style:style style:name=\"" + columnName + "\"");
		styleWriter.write(" style:family=\"table-column\">\n");
		styleWriter.write("   <style:table-column-properties");		
		styleWriter.write(" style:column-width=\"" + LengthUtil.inch(colWidth) + "in\"");
		styleWriter.write("/>\n");
		styleWriter.write(" </style:style>\n");
	}

	public void buildColumnHeader(int colIndex) 
	{
		String columnName = tableName + "_col_" + colIndex;
		bodyWriter.write("<table:table-column");		
		bodyWriter.write(" table:style-name=\"" + columnName + "\"");
		bodyWriter.write(">\n");
	}

	public void buildColumnFooter() 
	{
		bodyWriter.write("</table:table-column>\n");		
	}

	public void buildCellHeader(String cellStyleName, int colSpan, int rowSpan) 
	{
		//FIXMEODT officevalue bodyWriter.write("<table:table-cell office:value-type=\"string\"");
		bodyWriter.write("<table:table-cell");
		if (cellStyleName != null)
		{
			bodyWriter.write(" table:style-name=\"" + cellStyleName + "\"");
		}
		if (colSpan > 1)
		{
			bodyWriter.write(" table:number-columns-spanned=\"" + colSpan + "\"");
		}
		if (rowSpan > 1)
		{
			bodyWriter.write(" table:number-rows-spanned=\"" + rowSpan + "\"");
		}
		
		bodyWriter.write(">\n");
	}

	public void buildCellFooter()
	{
		bodyWriter.write("</table:table-cell>\n");
	}
	

	/**
	 *
	 */
	public void exportRectangle(JRPrintGraphicElement rectangle, JRExporterGridCell gridCell)
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(rectangle.getLinePen().getLineColor());
		pen.setLineStyle(rectangle.getLinePen().getLineStyleValue());
		pen.setLineWidth(rectangle.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box

		buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());
		buildCellFooter();
	}

	
	/**
	 *
	 */
	public void exportLine(JRPrintLine line, JRExporterGridCell gridCell)
	{
		buildCellHeader(null, gridCell.getColSpan(), gridCell.getRowSpan());

		double x1, y1, x2, y2;

		if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN)
		{
			x1 = LengthUtil.inch(0);
			y1 = LengthUtil.inch(0);
			x2 = LengthUtil.inch(line.getWidth() - 1);
			y2 = LengthUtil.inch(line.getHeight() - 1);
		}
		else
		{
			x1 = LengthUtil.inch(0);
			y1 = LengthUtil.inch(line.getHeight() - 1);
			x2 = LengthUtil.inch(line.getWidth() - 1);
			y2 = LengthUtil.inch(0);
		}

		bodyWriter.write("<text:p>");
		documentBuilder.insertPageAnchor(this);
		bodyWriter.write(
				"<draw:line text:anchor-type=\"paragraph\" "
				+ "draw:style-name=\"" + styleCache.getGraphicStyle(line) + "\" "
				+ "svg:x1=\"" + x1 + "in\" "
				+ "svg:y1=\"" + y1 + "in\" "
				+ "svg:x2=\"" + x2 + "in\" "
				+ "svg:y2=\"" + y2 + "in\">"
				//+ "</draw:line>"
				+ "<text:p/></draw:line>"
				+ "</text:p>"
				);
		buildCellFooter();
	}

	
	/**
	 *
	 */
	public void exportEllipse(JRPrintEllipse ellipse, JRExporterGridCell gridCell)
	{
		buildCellHeader(null, gridCell.getColSpan(), gridCell.getRowSpan());
		bodyWriter.write("<text:p>");
		documentBuilder.insertPageAnchor(this);
		bodyWriter.write(
			"<draw:ellipse text:anchor-type=\"paragraph\" "
			+ "draw:style-name=\"" + styleCache.getGraphicStyle(ellipse) + "\" "
			+ "svg:width=\"" + LengthUtil.inch(ellipse.getWidth()) + "in\" "
			+ "svg:height=\"" + LengthUtil.inch(ellipse.getHeight()) + "in\" "
			+ "svg:x=\"0in\" "
			+ "svg:y=\"0in\">"
			+ "<text:p/></draw:ellipse></text:p>"
			);
		buildCellFooter();
	}


	/**
	 *
	 */
	public void exportText(JRPrintText text, JRExporterGridCell gridCell)
	{
		buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

		bodyWriter.write("<text:p text:style-name=\"");
		bodyWriter.write(styleCache.getParagraphStyle(text));
		bodyWriter.write("\">");
		documentBuilder.insertPageAnchor(this);
		if (text.getAnchorName() != null)
		{
			exportAnchor(JRStringUtil.xmlEncode(text.getAnchorName()));
		}

		exportTextContents(text);

		bodyWriter.write("</text:p>\n");

		buildCellFooter();
	}


	/**
	 *
	 */
	protected void exportTextContents(JRPrintText text)
	{
		boolean startedHyperlink = startHyperlink(text, true);

		exportStyledText(text, startedHyperlink);

		if (startedHyperlink)
		{
			endHyperlink(true);
		}
	}


	/**
	 *
	 */
	protected void exportStyledText(JRPrintText text, boolean startedHyperlink)
	{
		JRStyledText styledText = documentBuilder.getStyledText(text);
		if (styledText != null && styledText.length() > 0)
		{
			exportStyledText(styledText, documentBuilder.getTextLocale(text), startedHyperlink);
		}
	}


	/**
	 *
	 */
	protected void exportStyledText(JRStyledText styledText, Locale locale, boolean startedHyperlink)
	{
		String text = styledText.getText();

		int runLimit = 0;

		AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

		while(runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
		{
			exportStyledTextRun(
				iterator.getAttributes(), 
				text.substring(iterator.getIndex(), runLimit),
				locale,
				startedHyperlink
				);

			iterator.setIndex(runLimit);
		}
	}


	/**
	 *
	 */
	protected void exportStyledTextRun(
			Map<AttributedCharacterIterator.Attribute, 
			Object> attributes, 
			String text, 
			Locale locale, 
			boolean startedHyperlink
			)
	{
		startTextSpan(attributes, text, locale);

		boolean localHyperlink = false;

		if (!startedHyperlink)
		{
			JRPrintHyperlink hyperlink = (JRPrintHyperlink)attributes.get(JRTextAttribute.HYPERLINK);
			if (hyperlink != null)
			{
				localHyperlink = startHyperlink(hyperlink, true);
			}
		}
		
		writeText(text);

		if (localHyperlink)
		{
			endHyperlink(true);
		}

		endTextSpan();
	}


	/**
	 *
	 */
	protected void startTextSpan(Map<AttributedCharacterIterator.Attribute, Object> attributes, String text, Locale locale)
	{
		String textSpanStyleName = styleCache.getTextSpanStyle(attributes, text, locale);

		bodyWriter.write("<text:span");
		bodyWriter.write(" text:style-name=\"" + textSpanStyleName + "\"");
		bodyWriter.write(">");
	}

	
	/**
	 *
	 */
	protected void endTextSpan()
	{
		bodyWriter.write("</text:span>");
	}

	
	/**
	 *
	 */
	protected void writeText(String text)
	{
		if (text != null)
		{
			bodyWriter.write(Utility.replaceNewLineWithLineBreak(JRStringUtil.xmlEncode(text, documentBuilder.getInvalidCharReplacement())));//FIXMEODT try something nicer for replace
		}
	}


	/**
	 *
	 */
	protected void exportAnchor(String anchorName)
	{
		bodyWriter.write("<text:bookmark text:name=\"");
		bodyWriter.write(anchorName);
		bodyWriter.write("\"/>\n");
	}

	
	/**
	 *
	 */
	protected String getIgnoreHyperlinkProperty()
	{
		return JROdtExporter.PROPERTY_IGNORE_HYPERLINK;
	}

	
	/**
	 *
	 */
	protected boolean startHyperlink(JRPrintHyperlink link, boolean isText)
	{
		String href = null;

		String ignLnkPropName = getIgnoreHyperlinkProperty();
		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(ignLnkPropName, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(jasperPrint, ignLnkPropName, false);
		}

		if (!ignoreHyperlink)
		{
			href = documentBuilder.getHyperlinkURL(link);
		}
		
		if (href != null)
		{
			writeHyperlink(link, href, isText);
		}

		return href != null;
	}


	/**
	 *
	 */
	protected void writeHyperlink(JRPrintHyperlink link, String href, boolean isText)
	{
		if(isText)
		{
			bodyWriter.write("<text:a xlink:href=\"");
		}
		else
		{
			bodyWriter.write("<draw:a xlink:type=\"simple\" xlink:href=\"");
		}
		bodyWriter.write(JRStringUtil.xmlEncode(href));
		bodyWriter.write("\"");


		String target = getHyperlinkTarget(link);//FIXMETARGET
		if (target != null)
		{
			bodyWriter.write(" office:target-frame-name=\"");
			bodyWriter.write(target);
			bodyWriter.write("\"");
			if(target.equals("_blank"))
			{
				bodyWriter.write(" xlink:show=\"new\"");
			}
		}
/*
 * tooltips are unavailable for the moment
 *
		if (link.getHyperlinkTooltip() != null)
		{
			bodyWriter.write(" xlink:title=\"");
			bodyWriter.write(JRStringUtil.xmlEncode(link.getHyperlinkTooltip()));
			bodyWriter.write("\"");
		}
*/
		bodyWriter.write(">");
	}


	/**
	 *
	 */
	protected void endHyperlink(boolean isText)
	{
		if(isText)
		{
			bodyWriter.write("</text:a>");
		}
		else
		{
			bodyWriter.write("</draw:a>");
		}
	}


	/**
	 *
	 */
	protected String getHyperlinkTarget(JRPrintHyperlink link)
	{
		String target = null;
		switch(link.getHyperlinkTargetValue())
		{
			case SELF :
			{
				target = "_self";
				break;
			}
			case BLANK :
			default :
			{
				target = "_blank";
				break;
			}
		}
		return target;
	}

	
	/**
	 *
	 */
	protected float getXAlignFactor(JRPrintImage image)
	{
		float xalignFactor = 0f;
		switch (image.getHorizontalAlignmentValue())
		{
			case RIGHT :
			{
				xalignFactor = 1f;
				break;
			}
			case CENTER :
			{
				xalignFactor = 0.5f;
				break;
			}
			case LEFT :
			default :
			{
				xalignFactor = 0f;
				break;
			}
		}
		return xalignFactor;
	}


	/**
	 *
	 */
	protected float getYAlignFactor(JRPrintImage image)
	{
		float yalignFactor = 0f;
		switch (image.getVerticalAlignmentValue())
		{
			case BOTTOM :
			{
				yalignFactor = 1f;
				break;
			}
			case MIDDLE :
			{
				yalignFactor = 0.5f;
				break;
			}
			case TOP :
			default :
			{
				yalignFactor = 0f;
				break;
			}
		}
		return yalignFactor;
	}


	/**
	 *
	 */
	protected JasperReportsContext getJasperReportsContext()
	{
		return documentBuilder.getJasperReportsContext();
	}
}