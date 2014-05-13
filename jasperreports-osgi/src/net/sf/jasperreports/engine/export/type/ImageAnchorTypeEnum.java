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
package net.sf.jasperreports.engine.export.type;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.type.JREnum;
import net.sf.jasperreports.engine.type.EnumUtil;


/**
 * @author sanda zaharia (shertage@users.sourceforge.net)
 * @version $Id: ImageAnchorTypeEnum.java 5890 2013-01-14 12:01:19Z shertage $
 */
public enum ImageAnchorTypeEnum implements JREnum
{
	/**
	 * Constant useful for specifying the <code>Move and size with cells</code> anchor type in Excel.
	 */
	MOVE_SIZE((byte)0, "MoveSize"),

	/**
	 * Constant useful for specifying the <code>Move but don't size with cells</code> anchor type in Excel.
	 */
	MOVE_NO_SIZE((byte)2, "MoveNoSize"),
	
	/**
	 * Constant useful for specifying the <code>Don't move or size with cells</code> anchor type in Excel.
	 */
	NO_MOVE_NO_SIZE((byte)3, "NoMoveNoSize");
	
	
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private final transient byte value;
	private final transient String name;

	private ImageAnchorTypeEnum(byte value, String name)
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
	public static ImageAnchorTypeEnum getByName(String name)
	{
		return (ImageAnchorTypeEnum)EnumUtil.getByName(values(), name);
	}
	
	/**
	 *
	 */
	public static ImageAnchorTypeEnum getByValue(Byte value)
	{
		return (ImageAnchorTypeEnum)EnumUtil.getByValue(values(), value);
	}
	
	/**
	 *
	 */
	public static ImageAnchorTypeEnum getByValue(byte value)
	{
		return getByValue(new Byte(value));
	}

}
