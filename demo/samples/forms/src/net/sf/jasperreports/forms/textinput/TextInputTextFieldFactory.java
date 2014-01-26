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
package net.sf.jasperreports.forms.textinput;

import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.xml.JRTextFieldFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: TextInputTextFieldFactory.java 5921 2013-02-19 10:23:06Z teodord $
 */
public class TextInputTextFieldFactory extends JRTextFieldFactory
{

	/**
	 *
	 */
	public JRDesignTextField getTextField()
	{
		TextInputComponent textInputComponent = (TextInputComponent)digester.peek();
		JRDesignTextField textField = (JRDesignTextField)textInputComponent.getTextField();
		return textField;
	}

}
