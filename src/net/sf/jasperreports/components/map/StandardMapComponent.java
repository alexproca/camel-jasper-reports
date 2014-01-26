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
package net.sf.jasperreports.components.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import net.sf.jasperreports.components.map.type.MapImageTypeEnum;
import net.sf.jasperreports.components.map.type.MapScaleEnum;
import net.sf.jasperreports.components.map.type.MapTypeEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: StandardMapComponent.java 6002 2013-03-20 08:15:32Z teodord $
 */
public class StandardMapComponent implements MapComponent, Serializable, JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_LATITUDE_EXPRESSION = "latitudeExpression";
	public static final String PROPERTY_LONGITUDE_EXPRESSION = "longitudeExpression";
	public static final String PROPERTY_ZOOM_EXPRESSION = "zoomExpression";
	public static final String PROPERTY_LANGUAGE_EXPRESSION = "languageExpression";
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	public static final String PROPERTY_MAP_TYPE = "mapType";
	public static final String PROPERTY_MAP_SCALE = "mapScale";
	public static final String PROPERTY_IMAGE_TYPE = "imageType";
	public static final String PROPERTY_MARKER_DATA = "markerData";
	/**
	 * @deprecated Replaced by {@link #PROPERTY_MARKER_DATA}.
	 */
	public static final String PROPERTY_MARKER_DATASET = "markerDataset";

	private JRExpression latitudeExpression;
	private JRExpression longitudeExpression;
	private JRExpression zoomExpression;
	private JRExpression languageExpression;
	private EvaluationTimeEnum evaluationTime = EvaluationTimeEnum.NOW;
	private String evaluationGroup;
	private MapTypeEnum mapType;
	private MapScaleEnum mapScale;
	private MapImageTypeEnum imageType;
	private ItemData markerData;
	
	private transient JRPropertyChangeSupport eventSupport;

	public StandardMapComponent()
	{
	}

	public StandardMapComponent(MapComponent map, JRBaseObjectFactory objectFactory)
	{
		this.latitudeExpression = objectFactory.getExpression(map.getLatitudeExpression());
		this.longitudeExpression = objectFactory.getExpression(map.getLongitudeExpression());
		this.zoomExpression = objectFactory.getExpression(map.getZoomExpression());
		this.languageExpression = objectFactory.getExpression(map.getLanguageExpression());
		this.evaluationTime = map.getEvaluationTime();
		this.evaluationGroup = map.getEvaluationGroup();
		this.mapType = map.getMapType();
		this.mapScale = map.getMapScale();
		this.imageType = map.getImageType();
		if(map.getMarkerData() != null)
		{
			this.markerData = new StandardItemData(map.getMarkerData(), objectFactory);
		}
	}
	
	public JRExpression getLatitudeExpression()
	{
		return latitudeExpression;
	}

	public void setLatitudeExpression(JRExpression latitudeExpression)
	{
		Object old = this.latitudeExpression;
		this.latitudeExpression = latitudeExpression;
		getEventSupport().firePropertyChange(PROPERTY_LATITUDE_EXPRESSION, old, this.latitudeExpression);
	}

	public JRExpression getLongitudeExpression()
	{
		return longitudeExpression;
	}

	public void setLongitudeExpression(JRExpression longitudeExpression)
	{
		Object old = this.longitudeExpression;
		this.longitudeExpression = longitudeExpression;
		getEventSupport().firePropertyChange(PROPERTY_LONGITUDE_EXPRESSION, old, this.longitudeExpression);
	}

	public JRExpression getZoomExpression()
	{
		return zoomExpression;
	}
	
	public void setZoomExpression(JRExpression zoomExpression)
	{
		Object old = this.zoomExpression;
		this.zoomExpression = zoomExpression;
		getEventSupport().firePropertyChange(PROPERTY_ZOOM_EXPRESSION, old, this.zoomExpression);
	}
	
	public JRExpression getLanguageExpression()
	{
		return languageExpression;
	}

	public void setLanguageExpression(JRExpression languageExpression)
	{
		Object old = this.languageExpression;
		this.languageExpression = languageExpression;
		getEventSupport().firePropertyChange(PROPERTY_LANGUAGE_EXPRESSION, old, this.languageExpression);
	}

	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}

	public void setEvaluationTime(EvaluationTimeEnum evaluationTimeValue)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTimeValue;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}

	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}

	public void setEvaluationGroup(String evaluationGroup)
	{
		Object old = this.evaluationGroup;
		this.evaluationGroup = evaluationGroup;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP, 
				old, this.evaluationGroup);
	}
	
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
	
	public Object clone()
	{
		StandardMapComponent clone = null;
		try
		{
			clone = (StandardMapComponent) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.latitudeExpression = JRCloneUtils.nullSafeClone(latitudeExpression);
		clone.longitudeExpression = JRCloneUtils.nullSafeClone(longitudeExpression);
		clone.zoomExpression = JRCloneUtils.nullSafeClone(zoomExpression);
		clone.languageExpression = JRCloneUtils.nullSafeClone(languageExpression);
		clone.markerData = JRCloneUtils.nullSafeClone(markerData);
		clone.eventSupport = null;
		return clone;
	}

	public MapTypeEnum getMapType() {
		return mapType;
	}

	public void setMapType(MapTypeEnum mapType) {
		Object old = this.mapType;
		this.mapType = mapType;
		getEventSupport().firePropertyChange(PROPERTY_MAP_TYPE, old, this.mapType);
	}

	public MapScaleEnum getMapScale() {
		return mapScale;
	}
	
	public void setMapScale(MapScaleEnum mapScale) {
		Object old = this.mapScale;
		this.mapScale = mapScale;
		getEventSupport().firePropertyChange(PROPERTY_MAP_SCALE, old, this.mapScale);
	}
	
	public MapImageTypeEnum getImageType() {
		return imageType;
	}

	public void setImageType(MapImageTypeEnum imageType) {
		Object old = this.imageType;
		this.imageType = imageType;
		getEventSupport().firePropertyChange(PROPERTY_IMAGE_TYPE, old, this.imageType);
	}

	/**
	 * @deprecated Replaced by {@link #getMarkerData()}.
	 */
	public MarkerDataset getMarkerDataset() {
		return markerDataset; //FIXMEMAP make dummy marker dataset
	}

	/**
	 * @deprecated Replaced by {@link #setMarkerData(ItemData)}.
	 */
	public void setMarkerDataset(MarkerDataset markerDataset) {
		setMarkerData(StandardMarkerDataset.getItemData(markerDataset));
	}

	public ItemData getMarkerData() {
		return markerData;
	}

	public void setMarkerData(ItemData markerData) {
		Object old = this.markerData;
		this.markerData = markerData;
		getEventSupport().firePropertyChange(PROPERTY_MARKER_DATA, old, this.markerData);
		
	}


	/*
	 * These fields are only for serialization backward compatibility.
	 */
	private int PSEUDO_SERIAL_VERSION_UID = JRConstants.PSEUDO_SERIAL_VERSION_UID; //NOPMD
	/**
	 * @deprecated
	 */
	private MarkerDataset markerDataset;
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if (PSEUDO_SERIAL_VERSION_UID < JRConstants.PSEUDO_SERIAL_VERSION_UID_3_1_0)
		{
			if (markerDataset != null)
			{
				markerData = StandardMarkerDataset.getItemData(markerDataset);
			}
			markerDataset = null;
		}
	}
}
