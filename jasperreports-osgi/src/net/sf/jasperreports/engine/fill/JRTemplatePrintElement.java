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
package net.sf.jasperreports.engine.fill;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;
import net.sf.jasperreports.engine.virtualization.VirtualizationSerializable;


/**
 * Base implementation of {@link net.sf.jasperreports.engine.JRPrintElement} that uses
 * a {@link net.sf.jasperreports.engine.fill.JRTemplateElement} instance to
 * store common attributes. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: JRTemplatePrintElement.java 6168 2013-05-16 17:27:14Z lucianc $
 */
public class JRTemplatePrintElement implements JRPrintElement, Serializable, VirtualizationSerializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final int SERIALIZATION_FLAG_CACHED_TEMPLATE = 1;
	private static final int SERIALIZATION_FLAG_HAS_UUID = 1 << 1;
	private static final int SERIALIZATION_FLAG_HAS_PROPERTIES = 1 << 2;
	private static final int SERIALIZATION_FLAG_CUSTOM_PROPERTIES = 1 << 3;

	/**
	 *
	 */
	protected JRTemplateElement template;

	private UUID uuid;
	private int x;
	private int y;
	private int height;
	private int width;

	private JRPropertiesMap properties;
	//FIXME do we need both uuid and sourceElementId?
	private int sourceElementId;
	
	public JRTemplatePrintElement()
	{
		// used internally
	}
	
	/**
	 *
	 * @deprecated provide a source Id via {@link #JRTemplatePrintElement(JRTemplateElement, int)}
	 */
	protected JRTemplatePrintElement(JRTemplateElement element)
	{
		this(element, UNSET_SOURCE_ELEMENT_ID);
	}
	
	/**
	 * 
	 * @param element
	 * @param sourceElementId the Id of the source element
	 */
	protected JRTemplatePrintElement(JRTemplateElement element, int sourceElementId)
	{
		template = element;
		this.sourceElementId = sourceElementId;
	}

	/**
	 * Updates the template used by this element.
	 * 
	 * @param elementTemplate the new element template
	 */
	protected void updateElementTemplate(JRTemplateElement elementTemplate)
	{
		this.template = elementTemplate;
	}
	
	/**
	 *
	 */
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return template.getDefaultStyleProvider();
	}
	
	/**
	 *
	 */
	public UUID getUUID()
	{
		return uuid;
	}

	/**
	 *
	 */
	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	/**
	 *
	 */
	public JROrigin getOrigin()
	{
		return template.getOrigin();
	}
	
	/**
	 *
	 */
	public JRStyle getStyle()
	{
		return template.getStyle();
	}
	
	/**
	 *
	 */
	public void setStyle(JRStyle style)
	{
	}
	
	/**
	 *
	 */
	public ModeEnum getModeValue()
	{
		return template.getModeValue();
	}
	
	/**
	 *
	 */
	public ModeEnum getOwnModeValue()
	{
		return template.getOwnModeValue();
	}
	
	/**
	 *
	 */
	public void setMode(ModeEnum modeValue)
	{
	}
	
	/**
	 *
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 *
	 */
	public void setX(int x)
	{
		this.x = x;
	}
	
	/**
	 *
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 *
	 */
	public void setY(int y)
	{
		this.y = y;
	}
	
	/**
	 *
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 *
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	/**
	 *
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 *
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	/**
	 *
	 */
	public Color getForecolor()
	{
		return template.getForecolor();
	}
	
	/**
	 *
	 */
	public Color getOwnForecolor()
	{
		return template.getOwnForecolor();
	}
	
	/**
	 *
	 */
	public void setForecolor(Color color)
	{
	}
	
	/**
	 *
	 */
	public Color getBackcolor()
	{
		return template.getBackcolor();
	}

	/**
	 *
	 */
	public Color getOwnBackcolor()
	{
		return template.getOwnBackcolor();
	}

	/**
	 *
	 */
	public void setBackcolor(Color color)
	{
	}

	
	public JRTemplateElement getTemplate()
	{
		return template;
	}

	public void setTemplate(JRTemplateElement template)
	{
		this.template = template;
		
		if (properties != null)
		{
			if (template != null && template.hasProperties())
			{
				properties.setBaseProperties(template.getPropertiesMap());
			}
			else
			{
				properties.setBaseProperties(null);
			}
		}
	}

	public String getKey()
	{
		return template.getKey();
	}

	/**
	 * Returns null as external style references are not allowed for print objects.
	 */
	public String getStyleNameReference()
	{
		return null;
	}

	/**
	 * 
	 */
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	//FIXME lucianc optimize by making unsynchronized 
	public synchronized boolean hasProperties()
	{
		return properties != null && properties.hasProperties()
				|| template.hasProperties();
	}

	public synchronized JRPropertiesMap getPropertiesMap()
	{
		if (properties == null)
		{
			//FIXME avoid this on read only calls
			properties = new JRPropertiesMap();
			
			if (template.hasProperties())
			{
				properties.setBaseProperties(template.getPropertiesMap());
			}
		}
		
		return properties;
	}

	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	private synchronized void writeObject(ObjectOutputStream out) throws IOException
	{
		if (properties != null && !properties.hasOwnProperties())
		{
			properties = null;
		}
		
		out.defaultWriteObject();
	}

	// we need to implement this method because the class is not abstract
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		throw new UnsupportedOperationException();
	}

	public int getSourceElementId()
	{
		return sourceElementId;
	}

	/**
	 * Sets the source/fill element Id for the print element.
	 * 
	 * @param sourceElementId
	 * @see #getSourceElementId()
	 */
	public void setSourceElementId(int sourceElementId)
	{
		this.sourceElementId = sourceElementId;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if (sourceElementId == 0 && template != null)
		{
			// if no element Id was written, use the template hash as Id in order
			// to preserve the old functionality of keep.first export filters
			sourceElementId = template.hashCode();
			if (sourceElementId == UNSET_SOURCE_ELEMENT_ID)
			{
				// collision with the unset value, using a different value
				sourceElementId = Integer.MIN_VALUE;
			}
		}
	}
	
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		JRVirtualizationContext virtualizationContext = out.getVirtualizationContext();
		String templateId = template.getId();
		boolean hasCachedTemplate = templateId != null && virtualizationContext.hasCachedTemplate(templateId);
		boolean hasUUID = uuid != null;
		
		boolean hasProperties = properties != null && properties.hasProperties();
		boolean customProperties = false;
		if (hasProperties)
		{
			if (!properties.getClass().equals(JRPropertiesMap.class))
			{
				customProperties = true;
			}
			else
			{
				// check whether the base properties are the same as the template properties
				JRPropertiesMap baseProperties = properties.getBaseProperties();
				customProperties = baseProperties == null ? template.hasProperties()
						: baseProperties != template.getPropertiesMap();// object identity
			}
		}
		
		int flags = 0;
		if (hasCachedTemplate)
		{
			flags |= SERIALIZATION_FLAG_CACHED_TEMPLATE;
		}
		if (hasUUID)
		{
			flags |= SERIALIZATION_FLAG_HAS_UUID;
		}
		if (hasProperties)
		{
			flags |= SERIALIZATION_FLAG_HAS_PROPERTIES;
		}
		if (customProperties)
		{
			flags |= SERIALIZATION_FLAG_CUSTOM_PROPERTIES;
		}
		
		out.writeByte(flags);
		
		if (hasCachedTemplate)
		{
			out.writeJRObject(templateId);
		}
		else
		{
			// not usually the case
			// TODO lucianc happens (FirstJasper)
			out.writeJRObject(template);
		}
		
		if (hasUUID)
		{
			// usually the case
			// FIXME uuids generally repeat, should we keep them in memory?
			out.writeJRObject(uuid);
		}
		
		out.writeIntCompressed(sourceElementId);
		out.writeIntCompressed(x);
		out.writeIntCompressed(y);
		out.writeIntCompressed(height);
		out.writeIntCompressed(width);
		
		if (hasProperties)
		{
			if (customProperties)
			{
				out.writeJRObject(properties);
			}
			else
			{
				//FIXME property name sets usually repeat, store in memory?
				String[] names = properties.getOwnPropertyNames();
				out.writeIntCompressed(names.length);
				for (int i = 0; i < names.length; i++)
				{
					String propName = names[i];
					out.writeJRObject(propName);
					String value = properties.getProperty(propName);
					out.writeJRObject(value);
				}
			}
		}
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		JRVirtualizationContext virtualizationContext = in.getVirtualizationContext();
		
		int flags = in.readUnsignedByte();
		if ((flags & SERIALIZATION_FLAG_CACHED_TEMPLATE) != 0)
		{
			String templateId = (String) in.readJRObject();
			template = virtualizationContext.getCachedTemplate(templateId);
			if (template == null)
			{
				throw new JRRuntimeException("Did not find template with id " + templateId);
			}
		}
		else
		{
			template = (JRTemplateElement) in.readJRObject();
		}
		
		if ((flags & SERIALIZATION_FLAG_HAS_UUID) != 0)
		{
			uuid = (UUID) in.readJRObject();
		}

		sourceElementId = in.readIntCompressed();
		x = in.readIntCompressed();
		y = in.readIntCompressed();
		height = in.readIntCompressed();
		width = in.readIntCompressed();
		
		if ((flags & SERIALIZATION_FLAG_HAS_PROPERTIES) != 0)
		{
			if ((flags & SERIALIZATION_FLAG_CUSTOM_PROPERTIES) != 0)
			{
				properties = (JRPropertiesMap) in.readJRObject();
			}
			else
			{
				int propSize = in.readIntCompressed();
				if (propSize > 0)
				{
					properties = new JRPropertiesMap();
					properties.setBaseProperties(template.hasProperties() ? template.getPropertiesMap() : null);
					
					for (int i = 0; i < propSize; i++)
					{
						String propName = (String) in.readJRObject();
						String value = (String) in.readJRObject();
						properties.setProperty(propName, value);
					}
				}
			}
		}
	}
}
