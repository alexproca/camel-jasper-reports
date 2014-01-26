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

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.RenderableUtil;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ElementGridCell;
import net.sf.jasperreports.engine.export.ExporterNature;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.Grid;
import net.sf.jasperreports.engine.export.GridRow;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RenderableTypeEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Exports a JasperReports document to ODF format. It has character output type and exports the document to a
 * grid-based layout.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JROdtExporter.java 6089 2013-04-19 15:20:44Z lucianc $
 */
public class JROdtExporter extends JRAbstractExporter
{
	private static final Log log = LogFactory.getLog(JROdtExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getHandler(net.sf.jasperreports.engine.JRGenericElementType, String)}.
	 */
	public static final String ODT_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "odt";
	
	protected static final String ODT_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.odt.";

	
	/**
	 * 
	 */
	public static final String PROPERTY_IGNORE_HYPERLINK = ODT_EXPORTER_PROPERTIES_PREFIX + JRPrintHyperlink.PROPERTY_IGNORE_HYPERLINK_SUFFIX;
	

	protected class ExporterContext extends BaseExporterContext implements JROdtExporterContext
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

		public String getExportPropertiesPrefix()
		{
			return ODT_EXPORTER_PROPERTIES_PREFIX;
		}
	}

	protected class OdtDocumentBuilder extends DocumentBuilder
	{
		public OdtDocumentBuilder(OasisZip oasisZip) 
		{
			super(oasisZip);
		}

		@Override
		public JRStyledText getStyledText(JRPrintText text) 
		{
			return JROdtExporter.this.getStyledText(text);
		}

		@Override
		public Locale getTextLocale(JRPrintText text) 
		{
			return JROdtExporter.this.getTextLocale(text);
		}

		@Override
		public String getInvalidCharReplacement() 
		{
			return JROdtExporter.this.invalidCharReplacement;
		}

		@Override
		protected void insertPageAnchor(TableBuilder tableBuilder) 
		{
			JROdtExporter.this.insertPageAnchor(tableBuilder);
		}

		@Override
		protected JRHyperlinkProducer getHyperlinkProducer(JRPrintHyperlink link) 
		{
			return JROdtExporter.this.getHyperlinkProducer(link);
		}

		@Override
		protected JasperReportsContext getJasperReportsContext() 
		{
			return JROdtExporter.this.getJasperReportsContext();
		}

		@Override
		protected int getReportIndex() 
		{
			return JROdtExporter.this.reportIndex;
		}

		@Override
		protected int getPageIndex() 
		{
			return JROdtExporter.this.pageIndex;
		}
	}

	/**
	 *
	 */
	protected WriterHelper tempBodyWriter;
	protected WriterHelper tempStyleWriter;

	protected JRExportProgressMonitor progressMonitor;

	protected int reportIndex;
	protected int pageIndex;
	protected int tableIndex;
	protected boolean startPage;
	protected boolean flexibleRowHeight;
	
	protected String invalidCharReplacement;

	/**
	 *
	 */
	protected String encoding;


	protected LinkedList<Color> backcolorStack = new LinkedList<Color>();
	protected Color backcolor;

	protected DocumentBuilder documentBuilder;

	protected StyleCache styleCache;

	protected ExporterNature nature;

	
	/**
	 * @see #JROdtExporter(JasperReportsContext)
	 */
	public JROdtExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JROdtExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}


	/**
	 *
	 */
	public void exportReport() throws JRException
	{
		progressMonitor = (JRExportProgressMonitor)parameters.get(JRExporterParameter.PROGRESS_MONITOR);

		/*   */
		setOffset();

		try
		{
			/*   */
			setExportContext();

			/*   */
			setInput();

			if (!parameters.containsKey(JRExporterParameter.FILTER))
			{
				filter = createFilter(getExporterPropertiesPrefix());
			}

			/*   */
			if (!isModeBatch)
			{
				setPageRange();
			}

			encoding =
				getStringParameterOrDefault(
					JRExporterParameter.CHARACTER_ENCODING,
					JRExporterParameter.PROPERTY_CHARACTER_ENCODING
					);

			setFlexibleRowHeight();

			setFontMap();

			setHyperlinkProducerFactory();

			nature = new JROdtExporterNature(filter);

			OutputStream os = (OutputStream)parameters.get(JRExporterParameter.OUTPUT_STREAM);
			if (os != null)
			{
				try
				{
					exportReportToOasisZip(os);
				}
				catch (IOException e)
				{
					throw new JRException("Error trying to export to output stream : " + jasperPrint.getName(), e);
				}
			}
			else
			{
				File destFile = (File)parameters.get(JRExporterParameter.OUTPUT_FILE);
				if (destFile == null)
				{
					String fileName = (String)parameters.get(JRExporterParameter.OUTPUT_FILE_NAME);
					if (fileName != null)
					{
						destFile = new File(fileName);
					}
					else
					{
						throw new JRException("No output specified for the exporter.");
					}
				}

				try
				{
					os = new FileOutputStream(destFile);
					exportReportToOasisZip(os);
				}
				catch (IOException e)
				{
					throw new JRException("Error trying to export to file : " + destFile, e);
				}
				finally
				{
					if (os != null)
					{
						try
						{
							os.close();
						}
						catch(IOException e)
						{
						}
					}
				}
			}
		}
		finally
		{
			resetExportContext();
		}
	}


	/**
	 *
	 */
	protected void exportReportToOasisZip(OutputStream os) throws JRException, IOException
	{
		OasisZip oasisZip = new FileBufferedOasisZip(OasisZip.MIME_TYPE_ODT);

		ExportZipEntry tempBodyEntry = new FileBufferedZipEntry(null);
		ExportZipEntry tempStyleEntry = new FileBufferedZipEntry(null);

		tempBodyWriter = new WriterHelper(jasperReportsContext, tempBodyEntry.getWriter());
		tempStyleWriter = new WriterHelper(jasperReportsContext, tempStyleEntry.getWriter());

		documentBuilder = new OdtDocumentBuilder(oasisZip);
		
		styleCache = new StyleCache(jasperReportsContext, tempStyleWriter, fontMap, getExporterKey());

		WriterHelper stylesWriter = new WriterHelper(jasperReportsContext, oasisZip.getStylesEntry().getWriter());

		StyleBuilder styleBuilder = new StyleBuilder(jasperPrintList, stylesWriter);
		styleBuilder.build();

		stylesWriter.close();

		for(reportIndex = 0; reportIndex < jasperPrintList.size(); reportIndex++)
		{
			setJasperPrint(jasperPrintList.get(reportIndex));
			setExporterHints();
			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				if (isModeBatch)
				{
					startPageIndex = 0;
					endPageIndex = pages.size() - 1;
				}

				JRPrintPage page = null;
				for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
				{
					if (Thread.interrupted())
					{
						throw new JRException("Current thread interrupted.");
					}

					page = pages.get(pageIndex);

					exportPage(page);
				}
			}
		}


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
				OasisZip.MIME_TYPE_ODT
				);
		contentBuilder.build();

		tempStyleEntry.dispose();
		tempBodyEntry.dispose();

		oasisZip.zipEntries(os);

		oasisZip.dispose();
	}


	/**
	 *
	 */
	protected void exportPage(JRPrintPage page) throws JRException, IOException
	{
		startPage = true;
		JRGridLayout layout =
			new JRGridLayout(
				nature,
				page.getElements(),
				jasperPrint.getPageWidth(),
				jasperPrint.getPageHeight(),
				globalOffsetX,
				globalOffsetY,
				null //address
				);

		exportGrid(layout, null);

		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}


	/**
	 *
	 */
	protected void exportGrid(JRGridLayout gridLayout, JRPrintElementIndex frameIndex) throws IOException, JRException
	{
		CutsInfo xCuts = gridLayout.getXCuts();
		Grid grid = gridLayout.getGrid();

		TableBuilder tableBuilder = frameIndex == null
			? new TableBuilder(documentBuilder, jasperPrint, reportIndex, pageIndex, tempBodyWriter, tempStyleWriter, styleCache)
			: new TableBuilder(documentBuilder, jasperPrint, frameIndex.toString(), tempBodyWriter, tempStyleWriter, styleCache);

		
		tableBuilder.buildTableStyle(gridLayout.getWidth());
		tableBuilder.buildTableHeader();

		for(int col = 1; col < xCuts.size(); col++)
		{
			tableBuilder.buildColumnStyle(
					col - 1,
					xCuts.getCutOffset(col) - xCuts.getCutOffset(col - 1)
					);
			tableBuilder.buildColumnHeader(col - 1);
			tableBuilder.buildColumnFooter();
		}

		int rowCount = grid.getRowCount();
		for(int row = 0; row < rowCount; row++)
		{
			int emptyCellColSpan = 0;
			//int emptyCellWidth = 0;
			int rowHeight = gridLayout.getRowHeight(row);

			tableBuilder.buildRowStyle(row, flexibleRowHeight ? -1 : rowHeight);
			tableBuilder.buildRowHeader(row);

			GridRow gridRow = grid.getRow(row);
			int rowSize = gridRow.size();
			for(int col = 0; col < rowSize; col++)
			{
				JRExporterGridCell gridCell = gridRow.get(col);
				if (gridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
				{
					if (emptyCellColSpan > 0)
					{
						//writeEmptyCell(gridCell, emptyCellColSpan, emptyCellWidth, rowHeight);
						emptyCellColSpan = 0;
						//emptyCellWidth = 0;
					}

					//writeOccupiedCells(1);
					exportOccupiedCells(1);
//					OccupiedGridCell occupiedGridCell = (OccupiedGridCell)gridCell;
//					ElementGridCell elementGridCell = (ElementGridCell)grid[occupiedGridCell.getRow()][occupiedGridCell.getCol()];
//					exportOccupiedCells(elementGridCell);
//					col += elementGridCell.getColSpan() - 1;
				}
				else if(gridCell.getType() == JRExporterGridCell.TYPE_ELEMENT_CELL)
				{
					if (emptyCellColSpan > 0)
					{
						//writeEmptyCell(gridCell, emptyCellColSpan, emptyCellWidth, rowHeight);
						emptyCellColSpan = 0;
						//emptyCellWidth = 0;
					}

					JRPrintElement element = gridCell.getElement();

					if (element instanceof JRPrintLine)
					{
						exportLine(tableBuilder, (JRPrintLine)element, gridCell);
					}
					else if (element instanceof JRPrintRectangle)
					{
						exportRectangle(tableBuilder, (JRPrintRectangle)element, gridCell);
					}
					else if (element instanceof JRPrintEllipse)
					{
						exportEllipse(tableBuilder, (JRPrintEllipse)element, gridCell);
					}
					else if (element instanceof JRPrintImage)
					{
						exportImage(tableBuilder, (JRPrintImage)element, gridCell);
					}
					else if (element instanceof JRPrintText)
					{
						exportText(tableBuilder, (JRPrintText)element, gridCell);
					}
					else if (element instanceof JRPrintFrame)
					{
						exportFrame(tableBuilder, (JRPrintFrame)element, gridCell);
					}
					else if (element instanceof JRGenericPrintElement)
					{
						exportGenericElement(tableBuilder, (JRGenericPrintElement)element, gridCell);
					}

					// //x += gridCell.colSpan - 1;
					//col += gridCell.getColSpan() - 1;
				}
				else
				{
					emptyCellColSpan++;
					//emptyCellWidth += gridCell.getWidth();
					exportEmptyCell(gridCell, 1);
				}
			}

//			if (emptyCellColSpan > 0)
//			{
//				//writeEmptyCell(null, emptyCellColSpan, emptyCellWidth, rowHeight);
//			}

			tableBuilder.buildRowFooter();
		}

		tableBuilder.buildTableFooter();
	}


	/**
	 *
	 */
	protected String getExporterPropertiesPrefix()
	{
		return ODT_EXPORTER_PROPERTIES_PREFIX;
	}

	
	/**
	 *
	 */
	public JRPrintImage getImage(List<JasperPrint> jasperPrintList, String imageName) throws JRException//FIXMEODS duplicate
	{
		return getImage(jasperPrintList, DocumentBuilder.getPrintElementIndex(imageName));
	}

	
	/**
	 *
	 */
	public JRPrintImage getImage(List<JasperPrint> jasperPrintList, JRPrintElementIndex imageIndex) throws JRException//FIXMEODS duplicate
	{
		JasperPrint report = jasperPrintList.get(imageIndex.getReportIndex());
		JRPrintPage page = report.getPages().get(imageIndex.getPageIndex());

		Integer[] elementIndexes = imageIndex.getAddressArray();
		Object element = page.getElements().get(elementIndexes[0].intValue());

		for (int i = 1; i < elementIndexes.length; ++i)
		{
			JRPrintFrame frame = (JRPrintFrame) element;
			element = frame.getElements().get(elementIndexes[i].intValue());
		}
		
		if(element instanceof JRGenericPrintElement)
		{
			return getPrintImageForGenericElement((JRGenericPrintElement)element);
		}

		return (JRPrintImage) element;
	}

	
	/**
	 *
	 */
	private void exportOccupiedCells(int count)
	{
		for(int i = 0; i < count; i++)
		{
			tempBodyWriter.write("<table:covered-table-cell/>\n");
		}
	}

	
	/**
	 *
	 */
	private void exportEmptyCell(JRExporterGridCell gridCell, int emptyCellColSpan) throws IOException
	{
		tempBodyWriter.write("<table:table-cell");
		//tempBodyWriter.write(" office:value-type=\"string\"");
//		if (gridCell == null)
//		{
//			tempBodyWriter.write(" table:style-name=\"empty-cell\"");
//		}
//		else
		{
			tempBodyWriter.write(" table:style-name=\"" + styleCache.getCellStyle(gridCell) + "\"");
		}
		if (emptyCellColSpan > 1)
		{
			tempBodyWriter.write(" table:number-columns-spanned=\"" + emptyCellColSpan + "\"");
		}
		tempBodyWriter.write("/>\n");

		exportOccupiedCells(emptyCellColSpan - 1);
	}


	/**
	 *
	 */
	protected void exportFrame(TableBuilder tableBuilder, JRPrintFrame frame, JRExporterGridCell gridCell) throws IOException, JRException
	{
		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

		boolean appendBackcolor =
			frame.getModeValue() == ModeEnum.OPAQUE
			&& (backcolor == null || frame.getBackcolor().getRGB() != backcolor.getRGB());

		if (appendBackcolor)
		{
			setBackcolor(frame.getBackcolor());
		}

		try
		{
			JRGridLayout layout = ((ElementGridCell) gridCell).getLayout();
			JRPrintElementIndex frameIndex =
				new JRPrintElementIndex(
						reportIndex,
						pageIndex,
						gridCell.getElementAddress()
						);
			exportGrid(layout, frameIndex);
		}
		finally
		{
			if (appendBackcolor)
			{
				restoreBackcolor();
			}
		}

		tableBuilder.buildCellFooter();
	}


	/**
	 *
	 */
	protected void setBackcolor(Color color)
	{
		backcolorStack.addLast(backcolor);

		backcolor = color;
	}


	/**
	 *
	 */
	protected void restoreBackcolor()
	{
		backcolor = backcolorStack.removeLast();
	}


	/**
	 *
	 */
	protected void exportRectangle(TableBuilder tableBuilder, JRPrintRectangle rectangle, JRExporterGridCell gridCell)
	{
		tableBuilder.exportRectangle(rectangle, gridCell);
	}

	
	/**
	 *
	 */
	protected void exportLine(TableBuilder tableBuilder, JRPrintLine line, JRExporterGridCell gridCell)
	{
		tableBuilder.exportLine(line, gridCell);
	}

	
	/**
	 *
	 */
	protected void exportEllipse(TableBuilder tableBuilder, JRPrintEllipse ellipse, JRExporterGridCell gridCell)
	{
		tableBuilder.exportEllipse(ellipse, gridCell);
	}


	/**
	 *
	 */
	public void exportText(TableBuilder tableBuilder, JRPrintText text, JRExporterGridCell gridCell)
	{
		tableBuilder.exportText(text, gridCell);
	}

	
	/**
	 *
	 */
	public void exportImage(TableBuilder tableBuilder, JRPrintImage image, JRExporterGridCell gridCell) throws JRException
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

			tempBodyWriter.write("<text:p>");
			documentBuilder.insertPageAnchor(tableBuilder);
			if (image.getAnchorName() != null)
			{
				tableBuilder.exportAnchor(JRStringUtil.xmlEncode(image.getAnchorName()));
			}


			boolean startedHyperlink = tableBuilder.startHyperlink(image,false);

			tempBodyWriter.write("<draw:frame text:anchor-type=\"paragraph\" "
					+ "draw:style-name=\"" + styleCache.getGraphicStyle(image) + "\" "
					+ "svg:x=\"" + LengthUtil.inch(leftPadding + xoffset) + "in\" "
					+ "svg:y=\"" + LengthUtil.inch(topPadding + yoffset) + "in\" "
					+ "svg:width=\"" + LengthUtil.inch(width) + "in\" "
					+ "svg:height=\"" + LengthUtil.inch(height) + "in\">"
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
			tempBodyWriter.write("</text:p>");
		}

		tableBuilder.buildCellFooter();
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

	
	/**
	 *
	 */
	protected void exportGenericElement(TableBuilder tableBuilder, JRGenericPrintElement element, JRExporterGridCell gridCell) throws IOException, JRException
	{
		GenericElementOdtHandler handler = (GenericElementOdtHandler) 
		GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), getExporterKey());

		if (handler != null)
		{
			JROdtExporterContext exporterContext = new ExporterContext(tableBuilder);

			handler.exportElement(exporterContext, element, gridCell);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No ODT generic element handler for " 
						+ element.getGenericType());
			}
		}
	}

	
	/**
	 *
	 */
	protected void setExporterHints()
	{
		if(jasperPrint.hasProperties() && jasperPrint.getPropertiesMap().containsProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS))
		{
			// allows null values for the property
			invalidCharReplacement = jasperPrint.getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS);
		}
		else
		{
			invalidCharReplacement = getPropertiesUtil().getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS, jasperPrint);
		}
	}

	
	/**
	 * 
	 */
	protected String getExporterKey()
	{
		return ODT_EXPORTER_KEY;
	}


	/**
	 * 
	 */
	protected JRPrintImage getPrintImageForGenericElement(JRGenericPrintElement genericPrintElement) throws JRException {
		return ((GenericElementOdtHandler) GenericElementHandlerEnviroment
				.getInstance(jasperReportsContext).getElementHandler(
						genericPrintElement.getGenericType(), ODT_EXPORTER_KEY))
				.getImage(new ExporterContext(null), genericPrintElement);
	}

	
	/**
	 *
	 */
	protected void setFlexibleRowHeight(){
		flexibleRowHeight = 
				getBooleanParameter(
					JROpenDocumentExporterParameter.ODT_FLEXIBLE_ROW_HEIGHT,
					JROpenDocumentExporterParameter.PROPERTY_ODT_FLEXIBLE_ROW_HEIGHT,
					false
					);
	}


}

