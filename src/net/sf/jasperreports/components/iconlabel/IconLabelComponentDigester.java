/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2011 Jaspersoft Corporation. All rights reserved.
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

import net.sf.jasperreports.engine.component.XmlDigesterConfigurer;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.xml.XmlConstantPropertyRule;

import org.apache.commons.digester.Digester;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: TextInputComponentDigester.java 5922 2013-02-19 11:03:27Z teodord $
 */
public class IconLabelComponentDigester implements XmlDigesterConfigurer
{
	public void configureDigester(Digester digester)
	{
		addIconLabelComponentRules(digester);
	}

	public static void addIconLabelComponentRules(Digester digester)
	{
		String componentNamespace = digester.getRuleNamespaceURI();
		
		String iconLabelComponentPattern = "*/componentElement/iconLabel";
//		digester.addObjectCreate(iconLabelComponentPattern, IconLabelComponent.class.getName());
		digester.addFactoryCreate(iconLabelComponentPattern, IconLabelComponentFactory.class.getName());

		digester.addSetProperties(iconLabelComponentPattern, new String[] {
				IconLabelComponent.PROPERTY_ICON_POSITION,
				IconLabelComponent.PROPERTY_LABEL_FILL,
				IconLabelComponent.PROPERTY_HORIZONTAL_ALIGN,
				IconLabelComponent.PROPERTY_VERTICAL_ALIGN }, 
				new String[0]);
		
		digester.addRule(iconLabelComponentPattern, 
				new XmlConstantPropertyRule(
						IconLabelComponent.PROPERTY_ICON_POSITION,//FIXMESORT move attributes to icon tag
						IconPositionEnum.values()));
		digester.addRule(iconLabelComponentPattern, 
				new XmlConstantPropertyRule(
						IconLabelComponent.PROPERTY_LABEL_FILL,
						ContainerFillEnum.values()));
		digester.addRule(iconLabelComponentPattern, 
				new XmlConstantPropertyRule(
						IconLabelComponent.PROPERTY_HORIZONTAL_ALIGN,
						HorizontalAlignEnum.values()));
		digester.addRule(iconLabelComponentPattern, 
				new XmlConstantPropertyRule(
						IconLabelComponent.PROPERTY_VERTICAL_ALIGN,
						VerticalAlignEnum.values()));
		
		digester.setRuleNamespaceURI(JRXmlConstants.JASPERREPORTS_NAMESPACE);

		digester.addFactoryCreate(iconLabelComponentPattern + "/label/textField", LabelTextFieldFactory.class.getName());
		digester.addFactoryCreate(iconLabelComponentPattern + "/icon/textField", IconTextFieldFactory.class.getName());

		digester.setRuleNamespaceURI(componentNamespace);
	}

}
