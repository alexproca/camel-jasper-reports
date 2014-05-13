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

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.util.Locale;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.RenderableUtil;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ElementGridCell;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.RenderableTypeEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Exports a JasperReports document to Open Document Spreadsheet format. It has character output type
 * and exports the document to a grid-based layout.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JROdsExporter.java 5930 2013-02-28 15:21:30Z teodord $
 */
public class JROdsExporter extends JRXlsAbstractExporter
{
	/**
	 *
	 */
	protected static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";

	/**
	 * 
	 */
	protected OasisZip oasisZip;
	protected ExportZipEntry tempBodyEntry;
	protected ExportZipEntry tempStyleEntry;
	protected WriterHelper tempBodyWriter;
	protected WriterHelper tempStyleWriter;

	protected StyleCache styleCache;

	protected DocumentBuilder documentBuilder;
	protected TableBuilder tableBuilder;

	protected boolean startPage;
	protected boolean flexibleRowHeight;

	
	@Override
	protected void setBackground() 
	{
		//FIXMEODS
	}

	@Override
	protected void openWorkbook(OutputStream os) throws JRException, IOException
	{
		oasisZip = new FileBufferedOasisZip(OasisZip.MIME_TYPE_ODS);

		tempBodyEntry = new FileBufferedZipEntry(null);
		tempStyleEntry = new FileBufferedZipEntry(null);

		tempBodyWriter = new WriterHelper(jasperReportsContext, tempBodyEntry.getWriter());
		tempStyleWriter = new WriterHelper(jasperReportsContext, tempStyleEntry.getWriter());

		documentBuilder = new OdsDocumentBuilder(oasisZip);
		
		styleCache = new StyleCache(jasperReportsContext, tempStyleWriter, fontMap, getExporterKey());

		WriterHelper stylesWriter = new WriterHelper(jasperReportsContext, oasisZip.getStylesEntry().getWriter());

		StyleBuilder styleBuilder = new StyleBuilder(jasperPrintList, stylesWriter);
		styleBuilder.build();

		stylesWriter.close();
	}

	@Override
	protected void createSheet(CutsInfo xCuts, String name)
	{
		closeSheet();
		
		startPage = true;
		
//		CutsInfo xCuts = gridLayout.getXCuts();
//		JRExporterGridCell[][] grid = gridLayout.getGrid();

//		TableBuilder tableBuilder = frameIndex == null
//			? new TableBuilder(reportIndex, pageIndex, tempBodyWriter, tempStyleWriter)
//			: new TableBuilder(frameIndex.toString(), tempBodyWriter, tempStyleWriter);
		tableBuilder = new OdsTableBuilder(documentBuilder, jasperPrint, reportIndex, pageIndex, tempBodyWriter, tempStyleWriter, styleCache);

//		tableBuilder.buildTableStyle(gridLayout.getWidth());
		tableBuilder.buildTableStyle(xCuts.getLastCutOffset());//FIXMEODS
		tableBuilder.buildTableHeader();

//		for(int col = 1; col < xCuts.size(); col++)
//		{
//			tableBuilder.buildColumnStyle(
//					col - 1,
//					xCuts.getCutOffset(col) - xCuts.getCutOffset(col - 1)
//					);
//			tableBuilder.buildColumnHeader(col - 1);
//			tableBuilder.buildColumnFooter();
//		}
	}


	protected void closeSheet()
	{
		if (tableBuilder != null)
		{
			tableBuilder.buildRowFooter();
			tableBuilder.buildTableFooter();
		}
	}

	@Override
	protected void closeWorkbook(OutputStream os) throws JRException, IOException
	{
		closeSheet();
		
		tempBodyWriter.flush();
		tempStyleWriter.flush();


		tempBodyWriter.close();
		tempStyleWriter.close();


		/*   */
		ContentBuilder contentBuilder =
			new ContentBuilder(
				oasisZip.getContentEntry(),
				tempStyleEntry,
				tempBodyEntry,
				styleCache.getFontFaces(),
				OasisZip.MIME_TYPE_ODS
				);
		contentBuilder.build();

		tempStyleEntry.dispose();
		tempBodyEntry.dispose();

		oasisZip.zipEntries(os);

		oasisZip.dispose();
	}

	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit)
	{
		tableBuilder.buildColumnStyle(col - 1, width);
		tableBuilder.buildColumnHeader(col - 1);
		tableBuilder.buildColumnFooter();
	}

	@Override
	protected void setRowHeight(
		int rowIndex, 
		int lastRowHeight, 
		Cut yCut,
		XlsRowLevelInfo levelInfo
		) throws JRException 
	{
		tableBuilder.buildRowStyle(rowIndex, flexibleRowHeight ? -1 : lastRowHeight);
		tableBuilder.buildRow(rowIndex);
	}

//	@Override
//	protected void setCell(
//		JRExporterGridCell gridCell, 
//		int colIndex,
//		int rowIndex) 
//	{
//		//nothing to do
//	}

	@Override
	protected void addBlankCell(
		JRExporterGridCell gridCell, 
		int colIndex,
		int rowIndex
		) 
	{
		tempBodyWriter.write("<table:table-cell");
		//tempBodyWriter.write(" office:value-type=\"string\"");
		if (gridCell == null)
		{
			tempBodyWriter.write(" table:style-name=\"empty-cell\"");
		}
		else
		{
			tempBodyWriter.write(" table:style-name=\"" + styleCache.getCellStyle(gridCell) + "\"");
		}
//		if (emptyCellColSpan > 1)
//		{
//			tempBodyWriter.write(" table:number-columns-spanned=\"" + emptyCellColSpan + "\"");
//		}
		tempBodyWriter.write("/>\n");
//
//		exportOccupiedCells(emptyCellColSpan - 1);
	}

	@Override
	protected void addOccupiedCell(
		OccupiedGridCell occupiedGridCell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		ElementGridCell elementGridCell = (ElementGridCell)occupiedGridCell.getOccupier();
		addBlankCell(elementGridCell, colIndex, rowIndex);
	}

	@Override
	public void exportText(
		JRPrintText text, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException
	{
		tableBuilder.exportText(text, gridCell);
	}

	@Override
	public void exportImage(
		JRPrintImage image, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex, 
		int emptyCols, 
		int yCutsRow,
		JRGridLayout layout
		) throws JRException 
	{
		int topPadding = 
				Math.max(image.getLineBox().getTopPadding().intValue(), Math.round(image.getLineBox().getTopPen().getLineWidth().floatValue()));
			int leftPadding = 
				Math.max(image.getLineBox().getLeftPadding().intValue(), Math.round(image.getLineBox().getLeftPen().getLineWidth().floatValue()));
			int bottomPadding = 
				Math.max(image.getLineBox().getBottomPadding().intValue(), Math.round(image.getLineBox().getBottomPen().getLineWidth().floatValue()));
			int rightPadding = 
				Math.max(image.getLineBox().getRightPadding().intValue(), Math.round(image.getLineBox().getRightPen().getLineWidth().floatValue()));

			int availableImageWidth = image.getWidth() - leftPadding - rightPadding;
			availableImageWidth = availableImageWidth < 0 ? 0 : availableImageWidth;

			int availableImageHeight = image.getHeight() - topPadding - bottomPadding;
			availableImageHeight = availableImageHeight < 0 ? 0 : availableImageHeight;

			int width = availableImageWidth;
			int height = availableImageHeight;

			int xoffset = 0;
			int yoffset = 0;

			tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

			Renderable renderer = image.getRenderable();

			if (
				renderer != null &&
				availableImageWidth > 0 &&
				availableImageHeight > 0
				)
			{
				if (renderer.getTypeValue() == RenderableTypeEnum.IMAGE && !image.isLazy())
				{
					// Non-lazy image renderers are all asked for their image data at some point.
					// Better to test and replace the renderer now, in case of lazy load error.
					renderer = RenderableUtil.getInstance(getJasperReportsContext()).getOnErrorRendererForImageData(renderer, image.getOnErrorTypeValue());
				}
			}
			else
			{
				renderer = null;
			}

			if (renderer != null)
			{
				float xalignFactor = tableBuilder.getXAlignFactor(image);
				float yalignFactor = tableBuilder.getYAlignFactor(image);

				switch (image.getScaleImageValue())
				{
					case FILL_FRAME :
					{
						width = availableImageWidth;
						height = availableImageHeight;
						xoffset = 0;
						yoffset = 0;
						break;
					}
					case CLIP :
					case RETAIN_SHAPE :
					default :
					{
						double normalWidth = availableImageWidth;
						double normalHeight = availableImageHeight;

						if (!image.isLazy())
						{
							// Image load might fail.
							Renderable tmpRenderer =
								RenderableUtil.getInstance(getJasperReportsContext()).getOnErrorRendererForDimension(renderer, image.getOnErrorTypeValue());
							Dimension2D dimension = tmpRenderer == null ? null : tmpRenderer.getDimension(getJasperReportsContext());
							// If renderer was replaced, ignore image dimension.
							if (tmpRenderer == renderer && dimension != null)
							{
								normalWidth = dimension.getWidth();
								normalHeight = dimension.getHeight();
							}
						}

						if (availableImageHeight > 0)
						{
							double ratio = normalWidth / normalHeight;

							if( ratio > availableImageWidth / (double)availableImageHeight )
							{
								width = availableImageWidth;
								height = (int)(width/ratio);

							}
							else
							{
								height = availableImageHeight;
								width = (int)(ratio * height);
							}
						}

						xoffset = (int)(xalignFactor * (availableImageWidth - width));
						yoffset = (int)(yalignFactor * (availableImageHeight - height));
					}
				}

//				tempBodyWriter.write("<text:p>");
				documentBuilder.insertPageAnchor(tableBuilder);
				if (image.getAnchorName() != null)
				{
					tableBuilder.exportAnchor(JRStringUtil.xmlEncode(image.getAnchorName()));
				}


				boolean startedHyperlink = tableBuilder.startHyperlink(image,false);

				//String cellAddress = getCellAddress(rowIndex + gridCell.getRowSpan(), colIndex + gridCell.getColSpan() - 1);
				String cellAddress = getCellAddress(rowIndex + gridCell.getRowSpan() + 1, colIndex + gridCell.getColSpan());
				cellAddress = cellAddress == null ? "" : "table:end-cell-address=\"" + cellAddress + "\" ";
				
				tempBodyWriter.write("<draw:frame text:anchor-type=\"frame\" "
						+ "draw:style-name=\"" + styleCache.getGraphicStyle(image) + "\" "
						+ cellAddress
//						+ "table:end-x=\"" + LengthUtil.inchRound(image.getWidth()) + "in\" "
//						+ "table:end-y=\"" + LengthUtil.inchRound(image.getHeight()) + "in\" "
						+ "table:end-x=\"0in\" "
						+ "table:end-y=\"0in\" "
//						+ "svg:x=\"" + LengthUtil.inch(image.getX() + leftPadding + xoffset) + "in\" "
//						+ "svg:y=\"" + LengthUtil.inch(image.getY() + topPadding + yoffset) + "in\" "
						+ "svg:x=\"0in\" "
						+ "svg:y=\"0in\" "
						+ "svg:width=\"" + LengthUtil.inchRound(image.getWidth()) + "in\" "
						+ "svg:height=\"" + LengthUtil.inchRound(image.getHeight()) + "in\"" 
						+ ">"
						);
				tempBodyWriter.write("<draw:image ");
				String imagePath = documentBuilder.getImagePath(renderer, image, gridCell);
				tempBodyWriter.write(" xlink:href=\"" + JRStringUtil.xmlEncode(imagePath) + "\"");
				tempBodyWriter.write(" xlink:type=\"simple\"");
				tempBodyWriter.write(" xlink:show=\"embed\"");
				tempBodyWriter.write(" xlink:actuate=\"onLoad\"");
				tempBodyWriter.write("/>\n");

				tempBodyWriter.write("</draw:frame>");
				if(startedHyperlink)
				{
					tableBuilder.endHyperlink(false);
				}
//				tempBodyWriter.write("</text:p>");
			}

			tableBuilder.buildCellFooter();
	}
	
	protected String getCellAddress(int row, int col)
	{
		String address = null;

		if(row > 0 && row < 1048577 && col > -1 && col < 16384)
		{
			address = getColumnName(col) + row;
		}
		return address;
	}
	
	protected String getColumnName(int colIndex)
	{
		String colName = null;
		if(colIndex > -1 && colIndex < 16384)
		{
			colName = colIndex > 675 
					? String.valueOf((char)(colIndex/676 +64)) + getColumnName(colIndex%676) 
					: (colIndex > 25 
							? String.valueOf((char)(colIndex/26 + 64)) + getColumnName(colIndex%26) 
							: String.valueOf((char)(colIndex %26 + 65)));
		}
		return colName;
	}
	
	@Override
	protected void exportRectangle(
		JRPrintGraphicElement rectangle,
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		tableBuilder.exportRectangle(rectangle, gridCell);
	}

	@Override
	protected void exportLine(
		JRPrintLine line, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = null;
		float ratio = line.getWidth() / line.getHeight();
		if (ratio > 1)
		{
			if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN)
			{
				pen = box.getTopPen();
			}
			else
			{
				pen = box.getBottomPen();
			}
		}
		else
		{
			if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN)
			{
				pen = box.getLeftPen();
			}
			else
			{
				pen = box.getRightPen();
			}
		}
		pen.setLineColor(line.getLinePen().getLineColor());
		pen.setLineStyle(line.getLinePen().getLineStyleValue());
		pen.setLineWidth(line.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box

		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

//		double x1, y1, x2, y2;
//
//		if (line.getDirection() == JRLine.DIRECTION_TOP_DOWN)
//		{
//			x1 = Utility.translatePixelsToInches(0);
//			y1 = Utility.translatePixelsToInches(0);
//			x2 = Utility.translatePixelsToInches(line.getWidth() - 1);
//			y2 = Utility.translatePixelsToInches(line.getHeight() - 1);
//		}
//		else
//		{
//			x1 = Utility.translatePixelsToInches(0);
//			y1 = Utility.translatePixelsToInches(line.getHeight() - 1);
//			x2 = Utility.translatePixelsToInches(line.getWidth() - 1);
//			y2 = Utility.translatePixelsToInches(0);
//		}

		tempBodyWriter.write("<text:p>");
//FIXMEODS		insertPageAnchor();
//		tempBodyWriter.write(
//				"<draw:line text:anchor-type=\"paragraph\" "
//				+ "draw:style-name=\"" + styleCache.getGraphicStyle(line) + "\" "
//				+ "svg:x1=\"" + x1 + "in\" "
//				+ "svg:y1=\"" + y1 + "in\" "
//				+ "svg:x2=\"" + x2 + "in\" "
//				+ "svg:y2=\"" + y2 + "in\">"
//				//+ "</draw:line>"
//				+ "<text:p/></draw:line>"
//				);
		tempBodyWriter.write("</text:p>");
		tableBuilder.buildCellFooter();
	}

	@Override
	protected void exportFrame(
		JRPrintFrame frame, 
		JRExporterGridCell cell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		addBlankCell(cell, colIndex, rowIndex);
	}

	@Override
	protected void exportGenericElement(
		JRGenericPrintElement element,
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex, 
		int emptyCols,
		int yCutsRow, 
		JRGridLayout layout
		) throws JRException 
	{
		GenericElementOdsHandler handler = (GenericElementOdsHandler) 
		GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), ODS_EXPORTER_KEY);

		if (handler != null)
		{
			JROdsExporterContext exporterContext = new ExporterContext(tableBuilder);

			handler.exportElement(exporterContext, element, gridCell, colIndex, rowIndex, emptyCols, yCutsRow, layout);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No ODS generic element handler for " 
						+ element.getGenericType());
			}
		}
	}

	@Override
	protected void setFreezePane(int rowIndex, int colIndex, boolean isRowEdge,
			boolean isColumnEdge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setSheetName(String sheetName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setAutoFilter(String autoFilterRange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setRowLevels(XlsRowLevelInfo levelInfo, String level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setScale(Integer scale) {
		// TODO Auto-generated method stub
		
	}

	
	private static final Log log = LogFactory.getLog(JROdsExporter.class);
	
	protected static final String ODS_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.ods.";
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getHandler(net.sf.jasperreports.engine.JRGenericElementType, String)}.
	 */
	public static final String ODS_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "ods";
	
	protected class ExporterContext extends BaseExporterContext implements JROdsExporterContext
	{
		TableBuilder tableBuilder = null;
		
		public ExporterContext(TableBuilder tableBuidler)
		{
			this.tableBuilder = tableBuidler;
		}
		
		public TableBuilder getTableBuilder()
		{
			return tableBuilder;
		}

		public String getExportPropertiesPrefix()//FIXMENOW if this is moved in abstract exporter, it can be removed from context
		{
			return ODS_EXPORTER_PROPERTIES_PREFIX;
		}
	}
	
	protected class OdsDocumentBuilder extends DocumentBuilder
	{
		public OdsDocumentBuilder(OasisZip oasisZip) 
		{
			super(oasisZip);
		}

		@Override
		public JRStyledText getStyledText(JRPrintText text) 
		{
			return JROdsExporter.this.getStyledText(text);
		}

		@Override
		public Locale getTextLocale(JRPrintText text) 
		{
			return JROdsExporter.this.getTextLocale(text);
		}

		@Override
		public String getInvalidCharReplacement() 
		{
			return JROdsExporter.this.invalidCharReplacement;
		}

		@Override
		protected void insertPageAnchor(TableBuilder tableBuilder) 
		{
			JROdsExporter.this.insertPageAnchor(tableBuilder);
		}

		@Override
		protected JRHyperlinkProducer getHyperlinkProducer(JRPrintHyperlink link) 
		{
			return JROdsExporter.this.getHyperlinkProducer(link);
		}

		@Override
		protected JasperReportsContext getJasperReportsContext() 
		{
			return JROdsExporter.this.getJasperReportsContext();
		}

		@Override
		protected int getReportIndex() 
		{
			return JROdsExporter.this.reportIndex;
		}

		@Override
		protected int getPageIndex() 
		{
			return JROdsExporter.this.pageIndex;
		}
	}

	
	protected class OdsTableBuilder extends TableBuilder
	{
		protected OdsTableBuilder(DocumentBuilder documentBuilder, JasperPrint jasperPrint,
			int reportIndex, int pageIndex, WriterHelper bodyWriter,
			WriterHelper styleWriter, StyleCache styleCache) 
		{
			super(documentBuilder, jasperPrint, reportIndex, pageIndex, bodyWriter, styleWriter, styleCache);
		}

		@Override
		protected String getIgnoreHyperlinkProperty()
		{
			return JROdsExporter.PROPERTY_IGNORE_HYPERLINK;
		}
		
		@Override
		protected void exportTextContents(JRPrintText textElement)
		{
			String href = null;
			
			String ignLnkPropName = getIgnoreHyperlinkProperty();
			Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(ignLnkPropName, textElement);
			if (ignoreHyperlink == null)
			{
				ignoreHyperlink = JRPropertiesUtil.getInstance(getJasperReportsContext()).getBooleanProperty(jasperPrint, ignLnkPropName, false);
			}

			if (!ignoreHyperlink)
			{
				href = documentBuilder.getHyperlinkURL(textElement);
			}

			if (href == null)
			{
				exportStyledText(textElement, false);
			}
			else
			{
				JRStyledText styledText = getStyledText(textElement);
				if (styledText != null && styledText.length() > 0)
				{
					String text = styledText.getText();
					Locale locale = getTextLocale(textElement);
					
					int runLimit = 0;
					AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();
					while(runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
					{
						// ODS does not like text:span inside text:a
						// writing one text:a inside text:span for each style run
						String runText = text.substring(iterator.getIndex(), runLimit);
						startTextSpan(iterator.getAttributes(), runText, locale);
						writeHyperlink(textElement, href, true);
						writeText(runText);
						endHyperlink(true);
						endTextSpan();

						iterator.setIndex(runLimit);
					}
				}
			}
		}
	}
	
	
	/**
	 * @see #JROdsExporter(JasperReportsContext)
	 */
	public JROdsExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JROdsExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}


	/**
	 *
	 */
	protected void setParameters()
	{
		super.setParameters();

		nature = new JROdsExporterNature(jasperReportsContext, filter, isIgnoreGraphics, isIgnorePageMargins);

//		macroTemplate =  macroTemplate == null ? getPropertiesUtil().getProperty(jasperPrint, PROPERTY_MACRO_TEMPLATE) : macroTemplate;
//		
//		password = 
//			getStringParameter(
//				JExcelApiExporterParameter.PASSWORD,
//				JExcelApiExporterParameter.PROPERTY_PASSWORD
//				);
	}


//	/**
//	 *
//	 */
//	protected String getExporterPropertiesPrefix()
//	{
//		return ODS_EXPORTER_PROPERTIES_PREFIX;
//	}
//
//
//	/**
//	 *
//	 */
//	protected void exportEllipse(TableBuilder tableBuilder, JRPrintEllipse ellipse, JRExporterGridCell gridCell) throws IOException
//	{
//		JRLineBox box = new JRBaseLineBox(null);
//		JRPen pen = box.getPen();
//		pen.setLineColor(ellipse.getLinePen().getLineColor());
//		pen.setLineStyle(ellipse.getLinePen().getLineStyleValue());
//		pen.setLineWidth(ellipse.getLinePen().getLineWidth());
//
//		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
//		
//		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());
//		tempBodyWriter.write("<text:p>");
//		insertPageAnchor();
////		tempBodyWriter.write(
////			"<draw:ellipse text:anchor-type=\"paragraph\" "
////			+ "draw:style-name=\"" + styleCache.getGraphicStyle(ellipse) + "\" "
////			+ "svg:width=\"" + Utility.translatePixelsToInches(ellipse.getWidth()) + "in\" "
////			+ "svg:height=\"" + Utility.translatePixelsToInches(ellipse.getHeight()) + "in\" "
////			+ "svg:x=\"0in\" "
////			+ "svg:y=\"0in\">"
////			+ "<text:p/></draw:ellipse>"
////			);
//		tempBodyWriter.write("</text:p>");
//		tableBuilder.buildCellFooter();
//	}


	/**
	 *
	 */
	protected String getExporterKey()
	{
		return ODS_EXPORTER_KEY;
	}


	/**
	 * 
	 */
//	@Override
	protected JRPrintImage getPrintImageForGenericElement(JRGenericPrintElement genericPrintElement) throws JRException {
		return ((GenericElementOdsHandler) GenericElementHandlerEnviroment
				.getInstance(jasperReportsContext).getElementHandler(
						genericPrintElement.getGenericType(), ODS_EXPORTER_KEY))
				.getImage(new ExporterContext(null), genericPrintElement);
	}

	protected void setFlexibleRowHeight(){
		flexibleRowHeight = 
				getBooleanParameter(
					JROpenDocumentExporterParameter.ODS_FLEXIBLE_ROW_HEIGHT,
					JROpenDocumentExporterParameter.PROPERTY_ODS_FLEXIBLE_ROW_HEIGHT,
					false
					);
	}

	/**
	 * 
	 */
	protected void insertPageAnchor(TableBuilder tableBuilder)
	{
		if(startPage)
		{
			tableBuilder.exportAnchor(DocumentBuilder.JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1));
			startPage = false;
		}
	}

	
}

