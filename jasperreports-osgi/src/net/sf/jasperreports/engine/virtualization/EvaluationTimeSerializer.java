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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JREvaluationTime;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: EvaluationTimeSerializer.java 6168 2013-05-16 17:27:14Z lucianc $
 */
public class EvaluationTimeSerializer implements ObjectSerializer<JREvaluationTime>
{

	@Override
	public int typeValue()
	{
		return SerializationConstants.OBJECT_TYPE_EVALUATION_TIME;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.OBJECT;
	}

	@Override
	public boolean defaultStoreReference()
	{
		return true;
	}

	@Override
	public void write(JREvaluationTime value, VirtualizationOutput out) throws IOException
	{
		//FIXME we should have keep these in memory and only write an ID/index
		EvaluationTimeEnum type = value.getType();
		out.writeByte(type.getValue());
		if (type == EvaluationTimeEnum.BAND)
		{
			out.writeInt(value.getBandId());
		}
		else if (type == EvaluationTimeEnum.GROUP)
		{
			out.writeJRObject(value.getGroupName());
		}
	}

	@Override
	public JREvaluationTime read(VirtualizationInput in) throws IOException
	{
		byte byteType = in.readByte();
		EvaluationTimeEnum type = EvaluationTimeEnum.getByValue(byteType);
		JREvaluationTime value;
		switch (type)
		{
		case NOW:
			value = JREvaluationTime.EVALUATION_TIME_NOW;
			break;
		case REPORT:
			value = JREvaluationTime.EVALUATION_TIME_REPORT;
			break;
		case PAGE:
			value = JREvaluationTime.EVALUATION_TIME_PAGE;
			break;
		case COLUMN:
			value = JREvaluationTime.EVALUATION_TIME_COLUMN;
			break;
		case BAND:
			int bandId = in.readInt();
			value = JREvaluationTime.getBandEvaluationTime(bandId);
			break;
		case GROUP:
			String groupName = (String) in.readJRObject();
			value = JREvaluationTime.getGroupEvaluationTime(groupName);
			break;
		case AUTO:
		default:
			throw new JRRuntimeException("Unknown evaluation time " + type);
		}
		return value;
	}

}
