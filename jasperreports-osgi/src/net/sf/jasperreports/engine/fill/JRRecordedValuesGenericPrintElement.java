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

import java.io.IOException;
import java.util.Set;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;

/**
 * Generic print element implementation that supports recorded values.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: JRRecordedValuesGenericPrintElement.java 6168 2013-05-16 17:27:14Z lucianc $
 */
public class JRRecordedValuesGenericPrintElement extends
		JRTemplateGenericPrintElement implements JRRecordedValuesPrintElement
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private JRRecordedValues recordedValues;

	public JRRecordedValuesGenericPrintElement()
	{
	}
	
	/**
	 * Creates a generic print element.
	 * 
	 * @param template the element template to be used by the element
	 * @deprecated provide a source Id via {@link #JRRecordedValuesGenericPrintElement(JRTemplateGenericElement, int)}
	 */
	public JRRecordedValuesGenericPrintElement(JRTemplateGenericElement template)
	{
		super(template);
	}

	/**
	 * Creates a generic print element.
	 * 
	 * @param template the element template to be used by the element
	 * @param sourceElementId the Id of the source element
	 */
	public JRRecordedValuesGenericPrintElement(JRTemplateGenericElement template, int sourceElementId)
	{
		super(template, sourceElementId);
	}

	/**
	 * Creates a generic print element.
	 * 
	 * @param template the element template to be used by the element
	 * @param sourceElementId the Id of the source element
	 * @param parameterCount the number of parameters that the element will have
	 */
	public JRRecordedValuesGenericPrintElement(JRTemplateGenericElement template, int sourceElementId,
			int parameterCount)
	{
		super(template, sourceElementId, parameterCount);
	}

	public JRRecordedValues getRecordedValues()
	{
		return recordedValues;
	}

	public void deleteRecordedValues()
	{
		recordedValues = null;
	}

	public void initRecordedValues(Set<JREvaluationTime> evaluationTimes)
	{
		recordedValues = new JRRecordedValues(evaluationTimes);
	}

	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		super.writeVirtualized(out);
		
		out.writeJRObject(recordedValues);
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		super.readVirtualized(in);
		
		recordedValues = (JRRecordedValues) in.readJRObject();
	}

}
