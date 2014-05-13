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
package net.sf.jasperreports.components.iconlabel;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.JREnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HorizontalAlignEnum.java 5878 2013-01-07 20:23:13Z teodord $
 */
public enum ContainerFillEnum implements JREnum
{
	/**
	 *
	 */ 
	NONE((byte)1, "None"),
	
	/**
	 *
	 */ 
	HORIZONTAL((byte)2, "Horizontal"),
	
	/**
	 *
	 */ 
	VERTICAL((byte)3, "Vertical"),
	
	/**
	 *
	 */ 
	BOTH((byte)3, "Both");
	
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private final transient byte value;
	private final transient String name;

	private ContainerFillEnum(byte value, String name)
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
	public static ContainerFillEnum getByName(String name)
	{
		return (ContainerFillEnum)EnumUtil.getByName(values(), name);
	}
	
	/**
	 *
	 */
	public static ContainerFillEnum getByValue(Byte value)
	{
		return (ContainerFillEnum)EnumUtil.getByValue(values(), value);
	}
	
	/**
	 *
	 */
	public static ContainerFillEnum getByValue(byte value)
	{
		return getByValue(new Byte(value));
	}

}
