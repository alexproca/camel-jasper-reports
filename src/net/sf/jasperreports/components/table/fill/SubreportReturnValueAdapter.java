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
package net.sf.jasperreports.components.table.fill;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.type.CalculationEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: SubreportReturnValueAdapter.java 5877 2013-01-07 19:51:14Z teodord $
 */
public class SubreportReturnValueAdapter implements JRSubreportReturnValue
{
	
	private final ReturnValue returnValue;
	
	public SubreportReturnValueAdapter(ReturnValue returnValue)
	{
		this.returnValue = returnValue;
	}

	@Override
	public String getSubreportVariable()
	{
		return returnValue.getFromVariable();
	}

	@Override
	public String getToVariable()
	{
		return returnValue.getToVariable();
	}

	@Override
	public CalculationEnum getCalculationValue()
	{
		return returnValue.getCalculation();
	}

	@Override
	public String getIncrementerFactoryClassName()
	{
		return returnValue.getIncrementerFactoryClassName();
	}

	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
	}

}
