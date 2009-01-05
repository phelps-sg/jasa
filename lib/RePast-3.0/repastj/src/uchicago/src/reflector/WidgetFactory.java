/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.reflector;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Collier
 * @version $Revision$ $Date$
 */
public class WidgetFactory {

  private Introspector spector;
  private List valueFields;
  private Map descriptors;

  public WidgetFactory(Introspector spector, List valueFields, Map descriptors) {
    this.spector = spector;
    this.valueFields = valueFields;
    this.descriptors = descriptors;
  }

  public PropertyWidget getWidget(String propertyName) throws IllegalAccessException, InvocationTargetException {
    if (spector.isWriteOnly(propertyName)) {
      return getWriteOnlyWidget();
    } else {
      // property can be read -- is it read only and string able?
      boolean isStringable = spector.isPropertyStringable(propertyName);
      boolean isReadOnly = spector.isReadOnly(propertyName);
      if (isStringable && isReadOnly) {
        return getStringableReadOnlyWidget(propertyName);
      } else {
        PropertyDescriptor pd = (PropertyDescriptor) descriptors.get(propertyName);
        if (pd != null) {
          return getPropertyDescriptorWidget(pd, propertyName);
        } else if (spector.isPropertyBoolean(propertyName)) {
          return getBooleanWidget(propertyName);
        } else if (isStringable) {
          return getSimpleLabelWidget(propertyName);
         
        } else if (hasWidgetInfo(propertyName)) {
          return getWidgetInfoPropertyWidget(propertyName);
        
        } else if (spector.getPropertyValue(propertyName) == null) {
          PropertyWidget widget = new PropertyLabel("Null");
          valueFields.add(widget);
          return widget;
        } else {
          return getPropertyButtonWidget(propertyName, isReadOnly);
        }
      }
    }
  }

  private PropertyWidget getWidgetInfoPropertyWidget(String propertyName) throws IllegalAccessException, InvocationTargetException {
    Class clazz = spector.getPropertyClass(propertyName);
    PropertyWidget widget;
    try {
      Class widgetInfoClass = Class.forName(clazz.getName() + "WidgetInfo");
      WidgetInfo info = (WidgetInfo) widgetInfoClass.newInstance();
      widget = info.getPropertyWidget();
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("WidgetInfo for '" + propertyName + "' not found.");
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Unable to instantiate WidgetInfo for '" + propertyName + "' not found.");
    }
    
    widget.setValue(spector.getPropertyValue(propertyName));
    valueFields.add(widget);
    return widget;
  }

  private boolean hasWidgetInfo(String propertyName) {
    Class clazz = spector.getPropertyClass(propertyName);
    try {
      Class.forName(clazz.getName() + "WidgetInfo");
    } catch (ClassNotFoundException e) {
      return false;
    }
    return true;
  }

  private PropertyWidget getPropertyButtonWidget(String propertyName, boolean isReadOnly) throws IllegalAccessException, InvocationTargetException {
    PropertyWidget widget = new PropertyButton(spector.getPropertyValue(propertyName), !isReadOnly);
    valueFields.add(widget);
    return widget;
  }

  private PropertyWidget getSimpleLabelWidget(String propertyName) throws IllegalAccessException, InvocationTargetException {
    PropertyWidget widget = new PropertyTextField(8);
    widget.setValue(spector.getPropertyValue(propertyName));
    valueFields.add(widget);
    return widget;
  }

  private PropertyWidget getPropertyDescriptorWidget(PropertyDescriptor pd, String name) throws IllegalAccessException, InvocationTargetException {
    PropertyWidget widget = pd.getWidget();
    widget.setValue(spector.getPropertyValue(name));
    valueFields.add(widget);
    return widget;
  }

  private PropertyWidget getBooleanWidget(String name) throws IllegalAccessException, InvocationTargetException {
    PropertyWidget widget = new BooleanPropertyDescriptor(name, true).getWidget();
    widget.setValue(spector.getPropertyValue(name));
    valueFields.add(widget);
    return widget;
  }

  private PropertyWidget getStringableReadOnlyWidget(String propertyName) throws IllegalAccessException, InvocationTargetException {
    String propValue = spector.getPropertyAsString(propertyName);
    PropertyLabel value = new PropertyLabel(propValue);
    value.setPropertyName(propertyName);
    valueFields.add(value);
    return value;
  }

  private PropertyWidget getWriteOnlyWidget() {
    return new PropertyLabel("Is Write-only");
  }
}
