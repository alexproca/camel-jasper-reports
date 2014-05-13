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
package net.sf.jasperreports.components.map.fill;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.jasperreports.components.map.MapComponent;
import net.sf.jasperreports.components.map.MapPrintElement;
import net.sf.jasperreports.components.map.type.MapImageTypeEnum;
import net.sf.jasperreports.components.map.type.MapScaleEnum;
import net.sf.jasperreports.components.map.type.MapTypeEnum;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.component.BaseFillComponent;
import net.sf.jasperreports.engine.component.FillContext;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRTemplateGenericElement;
import net.sf.jasperreports.engine.fill.JRTemplateGenericPrintElement;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: MapFillComponent.java 6004 2013-03-20 12:49:30Z teodord $
 */
public class MapFillComponent extends BaseFillComponent implements FillContextProvider
{
	private final MapComponent mapComponent;
	
	private Float latitude;
	private Float longitude;
	private Integer zoom;
	private String language;
	private MapTypeEnum mapType;
	private MapScaleEnum mapScale;
	private MapImageTypeEnum imageType;
	private FillItemData markerData;
	private List<Map<String,Object>> markers;
	
	JRFillObjectFactory factory;
	
	public MapFillComponent(MapComponent map)
	{
		this.mapComponent = map;
	}
	
	public MapFillComponent(MapComponent map, JRFillObjectFactory factory)
	{
		this.mapComponent = map;
		this.factory = factory;
		
		if (mapComponent.getMarkerData() != null)
		{
			markerData = new FillItemData(this, mapComponent.getMarkerData(), factory);
		}
	}
	
	protected MapComponent getMap()
	{
		return mapComponent;
	}
	
	public FillContext getFillContext()
	{
		return fillContext;
	}
	
	public void evaluate(byte evaluation) throws JRException
	{
		if (isEvaluateNow())
		{
			evaluateMap(evaluation);
		}
	}
	
	protected void evaluateMap(byte evaluation) throws JRException
	{
		latitude = (Float)fillContext.evaluate(mapComponent.getLatitudeExpression(), evaluation);
		longitude = (Float)fillContext.evaluate(mapComponent.getLongitudeExpression(), evaluation);
		zoom = (Integer)fillContext.evaluate(mapComponent.getZoomExpression(), evaluation);
		zoom = zoom == null ? MapComponent.DEFAULT_ZOOM : zoom;
		if(mapComponent.getLanguageExpression() != null)
		{
			language = (String)fillContext.evaluate(mapComponent.getLanguageExpression(), evaluation);
		}
		else
		{
			Locale locale = fillContext.getReportLocale();
			if(locale != null)
			{
				language = locale.getLanguage();
			}
		}
		mapType = mapComponent.getMapType() == null? MapTypeEnum.ROADMAP : mapComponent.getMapType();
		mapScale = mapComponent.getMapScale();
		imageType = mapComponent.getImageType();
		
		if (mapComponent.getMarkerData() != null)
		{
			markers = markerData.getEvaluateItems(evaluation);
		}
	}
	
	protected boolean isEvaluateNow()
	{
		return mapComponent.getEvaluationTime() == EvaluationTimeEnum.NOW;
	}

	public FillPrepareResult prepare(int availableHeight)
	{
		return FillPrepareResult.PRINT_NO_STRETCH;
//		return isEvaluateNow() && (latitude == null || longitude == null)  
//				? FillPrepareResult.NO_PRINT_NO_OVERFLOW
//				: FillPrepareResult.PRINT_NO_STRETCH;
	}

	public JRPrintElement fill()
	{
		JRComponentElement element = fillContext.getComponentElement();
		JRTemplateGenericElement template = new JRTemplateGenericElement(
				fillContext.getElementOrigin(), 
				fillContext.getDefaultStyleProvider(),
				MapPrintElement.MAP_ELEMENT_TYPE);
		template = deduplicate(template);
		
		JRTemplateGenericPrintElement printElement = new JRTemplateGenericPrintElement(template, elementId);
		printElement.setUUID(element.getUUID());
		printElement.setX(element.getX());
		printElement.setY(fillContext.getElementPrintY());
		printElement.setWidth(element.getWidth());
		printElement.setHeight(element.getHeight());

		if (isEvaluateNow())
		{
			copy(printElement);
		}
		else
		{
			fillContext.registerDelayedEvaluation(printElement, 
					mapComponent.getEvaluationTime(), mapComponent.getEvaluationGroup());
		}
		
		return printElement;
	}

	public void evaluateDelayedElement(JRPrintElement element, byte evaluation)
			throws JRException
	{
		evaluateMap(evaluation);
		copy((JRGenericPrintElement) element);
	}

	protected void copy(JRGenericPrintElement printElement)
	{
		printElement.setParameterValue(MapPrintElement.PARAMETER_LATITUDE, latitude);
		printElement.setParameterValue(MapPrintElement.PARAMETER_LONGITUDE, longitude);
		printElement.setParameterValue(MapPrintElement.PARAMETER_ZOOM, zoom);
		
		if(language != null)
		{
			printElement.setParameterValue(MapPrintElement.PARAMETER_LANGUAGE, language);
		}
		if(mapType != null)
		{
			printElement.setParameterValue(MapPrintElement.PARAMETER_MAP_TYPE, mapType.getName());
		}
		if(mapScale != null)
		{
			printElement.setParameterValue(MapPrintElement.PARAMETER_MAP_SCALE, mapScale.getName());
		}
		if(imageType != null)
		{
			printElement.setParameterValue(MapPrintElement.PARAMETER_IMAGE_TYPE, imageType.getName());
		}
		if(markers != null && !markers.isEmpty())
		{
			printElement.setParameterValue(MapPrintElement.PARAMETER_MARKERS, markers);
		}
	}
}
