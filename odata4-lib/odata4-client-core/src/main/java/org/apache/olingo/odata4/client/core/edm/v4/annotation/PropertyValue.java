/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.client.core.edm.v4.annotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.olingo.odata4.client.api.edm.v4.annotation.ExprConstruct;

@JsonDeserialize(using = PropertyValueDeserializer.class)
public class PropertyValue extends AnnotatedDynExprConstruct {

  private static final long serialVersionUID = 3081968466425707461L;

  private String property;

  private ExprConstruct value;

  public String getProperty() {
    return property;
  }

  public void setProperty(final String property) {
    this.property = property;
  }

  public ExprConstruct getValue() {
    return value;
  }

  public void setValue(final ExprConstruct value) {
    this.value = value;
  }

}
