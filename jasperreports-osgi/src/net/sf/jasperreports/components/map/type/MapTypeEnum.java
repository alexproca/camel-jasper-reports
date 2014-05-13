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
package net.sf.jasperreports.components.map.type;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.JREnum;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: MapTypeEnum.java 5877 2013-01-07 19:51:14Z teodord $
 */
public enum MapTypeEnum implements JREnum
{
	/**
	 * The roadmap type
	 */
	ROADMAP((byte)0, "roadmap"),

	/**
	 * The satellite map type
	 */
	SATELLITE((byte)1, "satellite"),

	/**
	 * The terrain map type
	 */
	TERRAIN((byte)2, "terrain"),

	/**
	 * The hybrid type
	 */
	HYBRID((byte)3, "hybrid");

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private final transient byte value;
	private final transient String name;

	private MapTypeEnum(byte value, String name)
	{
		this.value = value;
		this.name = name;
	}

	/**
	 *
	 */
	public Byte getValueByte()
	{
		return new Byte(value);
	}
	
	/**
	 *
	 */
	public final byte getValue()
	{
		return value;
	}
	
	/**
	 *
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static MapTypeEnum getByName(String name)
	{
		return (MapTypeEnum)EnumUtil.getByName(values(), name);
	}
	
	/**
	 *
	 */
	public static MapTypeEnum getByValue(Byte value)
	{
		return (MapTypeEnum)EnumUtil.getByValue(values(), value);
	}
	
	/**
	 *
	 */
	public static MapTypeEnum getByValue(byte value)
	{
		return getByValue(new Byte(value));
	}
	
}
