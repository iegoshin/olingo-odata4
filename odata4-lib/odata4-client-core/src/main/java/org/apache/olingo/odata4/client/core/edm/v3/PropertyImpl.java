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
package org.apache.olingo.odata4.client.core.edm.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.olingo.odata4.client.api.edm.v3.Property;
import org.apache.olingo.odata4.client.core.edm.AbstractProperty;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmContentKind;

public class PropertyImpl extends AbstractProperty implements Property {

  private static final long serialVersionUID = 6224524803474652100L;

  @JsonProperty("FC_SourcePath")
  private String fcSourcePath;

  @JsonProperty("FC_TargetPath")
  private String fcTargetPath;

  @JsonProperty("FC_ContentKind")
  private EdmContentKind fcContentKind = EdmContentKind.text;

  @JsonProperty("FC_NsPrefix")
  private String fcNSPrefix;

  @JsonProperty("FC_NsUri")
  private String fcNSURI;

  @JsonProperty("FC_KeepInContent")
  private boolean fcKeepInContent = true;

  public String getFcSourcePath() {
    return fcSourcePath;
  }

  public void setFcSourcePath(final String fcSourcePath) {
    this.fcSourcePath = fcSourcePath;
  }

  public String getFcTargetPath() {
    return fcTargetPath;
  }

  public void setFcTargetPath(final String fcTargetPath) {
    this.fcTargetPath = fcTargetPath;
  }

  public EdmContentKind getFcContentKind() {
    return fcContentKind;
  }

  public void setFcContentKind(final EdmContentKind fcContentKind) {
    this.fcContentKind = fcContentKind;
  }

  public String getFcNSPrefix() {
    return fcNSPrefix;
  }

  public void setFcNSPrefix(final String fcNSPrefix) {
    this.fcNSPrefix = fcNSPrefix;
  }

  public String getFcNSURI() {
    return fcNSURI;
  }

  public void setFcNSURI(final String fcNSURI) {
    this.fcNSURI = fcNSURI;
  }

  public boolean isFcKeepInContent() {
    return fcKeepInContent;
  }

  public void setFcKeepInContent(final boolean fcKeepInContent) {
    this.fcKeepInContent = fcKeepInContent;
  }

}
