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
package net.sf.jasperreports.components.headertoolbar;

import java.awt.Color;

import net.sf.jasperreports.components.headertoolbar.actions.ConditionalFormattingCommand;
import net.sf.jasperreports.components.headertoolbar.actions.ConditionalFormattingData;
import net.sf.jasperreports.components.headertoolbar.actions.FormatCondition;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.style.StyleProvider;
import net.sf.jasperreports.engine.style.StyleProviderContext;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.SortFieldTypeEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.web.util.JacksonUtil;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: HeaderToolbarConditionalStyleProvider.java 5877 2013-01-07 19:51:14Z teodord $
 */
public class HeaderToolbarConditionalStyleProvider implements StyleProvider
{
	private final StyleProviderContext context;
	private JasperReportsContext jasperreportsContext;
	
	public HeaderToolbarConditionalStyleProvider(StyleProviderContext context, JasperReportsContext jasperreportsContext)
	{
		this.context = context;
		this.jasperreportsContext = jasperreportsContext;
	}

	@Override
	public JRStyle getStyle(byte evaluation) 
	{
		if (context.getElement().getPropertiesMap() != null)
		{
			String srlzdConditionalFormattingData = context.getElement().getPropertiesMap().getProperty(ConditionalFormattingCommand.COLUMN_CONDITIONAL_FORMATTING_PROPERTY);
			if (srlzdConditionalFormattingData != null)
			{
				JRStyle style = null;
				
				ConditionalFormattingData cfd = JacksonUtil.getInstance(jasperreportsContext).loadObject(srlzdConditionalFormattingData, ConditionalFormattingData.class);
				if (cfd.getConditions().size() > 0) {
					SortFieldTypeEnum columnType = SortFieldTypeEnum.getByName(cfd.getColumnType());
					Object compareTo = columnType.equals(SortFieldTypeEnum.FIELD) ? context.getFieldValue(cfd.getFieldOrVariableName(), evaluation) : context.getVariableValue(cfd.getFieldOrVariableName(), evaluation);
					boolean bgColorSet = false;
					boolean fontBoldSet = false;
					boolean fontItalicSet = false;
					boolean fontUnderlineSet = false;
					boolean foreColorSet = false;
					boolean modeSet = false;
					for (FormatCondition condition: cfd.getConditions()) 
					{
						if(condition.matches(compareTo, cfd.getConditionType(), cfd.getConditionPattern(), condition.getConditionTypeOperator())) 
						{
							if (style == null) 
							{
								style = new JRBaseStyle();
							}
							
							if (condition.isConditionFontBold() != null && !fontBoldSet) 
							{
								style.setBold(condition.isConditionFontBold());
								fontBoldSet = true;
							}
							if (condition.isConditionFontItalic() != null && !fontItalicSet)
							{
								style.setItalic(condition.isConditionFontItalic());
								fontItalicSet = true;
							}
							if (condition.isConditionFontUnderline() != null && !fontUnderlineSet)
							{
								style.setUnderline(condition.isConditionFontUnderline());
								fontUnderlineSet = true;
							}
							if (condition.getConditionFontColor() != null && !foreColorSet) 
							{
								style.setForecolor(JRColorUtil.getColor("#" + condition.getConditionFontColor(), Color.black));
								foreColorSet = true;
							}
							if (condition.getConditionMode() != null && !modeSet)
							{
								style.setMode(ModeEnum.getByName(condition.getConditionMode()));
								modeSet = true;
							}
							if (condition.getConditionFontBackColor() != null && !bgColorSet) 
							{
								style.setBackcolor(JRColorUtil.getColor("#" + condition.getConditionFontBackColor(), Color.white));
								bgColorSet = true;
							}
						}
					}
				}
				
				return style;
			}
		}
		return null;
	}

	@Override
	public String[] getFields() 
	{
		return null;
	}

	@Override
	public String[] getVariables() 
	{
		return null;
	}

}
