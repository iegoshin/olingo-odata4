/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.serialization;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Operation;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Reads JSON string into an entity set.
 * <br/>
 * If metadata information is available, the corresponding entity fields and content will be populated.
 */
public class JsonEntitySetDeserializer extends JsonDeserializer {

  public JsonEntitySetDeserializer(final boolean serverMode) {
    super(serverMode);
  }

  protected ResWrap<EntityCollection> doDeserialize(final JsonParser parser) throws IOException {

    final ObjectNode tree = (ObjectNode) parser.getCodec().readTree(parser);

    if (!tree.has(Constants.VALUE)) {
      return null;
    }

    final EntityCollection entitySet = new EntityCollection();

    URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = getUri(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = getUri(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      entitySet.setBaseURI(getUri(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA)));
    }

    final String metadataETag;
    if (tree.hasNonNull(Constants.JSON_METADATA_ETAG)) {
      metadataETag = tree.get(Constants.JSON_METADATA_ETAG).textValue();
      tree.remove(Constants.JSON_METADATA_ETAG);
    } else {
      metadataETag = null;
    }

    if (tree.hasNonNull(Constants.JSON_COUNT)) {
      entitySet.setCount(tree.get(Constants.JSON_COUNT).asInt());
      tree.remove(Constants.JSON_COUNT);
    }
    if (tree.hasNonNull(Constants.JSON_NEXT_LINK)) {
      entitySet.setNext(getUri(tree.get(Constants.JSON_NEXT_LINK).textValue()));
      tree.remove(Constants.JSON_NEXT_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_DELTA_LINK)) {
      entitySet.setDeltaLink(getUri(tree.get(Constants.JSON_DELTA_LINK).textValue()));
      tree.remove(Constants.JSON_DELTA_LINK);
    }

    if (tree.hasNonNull(Constants.VALUE)) {
      final JsonEntityDeserializer entityDeserializer = new JsonEntityDeserializer(serverMode);
      for (JsonNode jsonNode : tree.get(Constants.VALUE)) {
        entitySet.getEntities().add(
            entityDeserializer.doDeserialize(jsonNode.traverse(parser.getCodec())).getPayload());
      }
      tree.remove(Constants.VALUE);
    }
    final Set<String> toRemove = new HashSet<>();
    // any remaining entry is supposed to be an annotation or is ignored
    for (final Iterator<Map.Entry<String, JsonNode>> itor = tree.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();
      if (field.getKey().charAt(0) == '@') {
        final Annotation annotation = new Annotation();
        annotation.setTerm(field.getKey().substring(1));

        try {
          value(annotation, field.getValue(), parser.getCodec());
        } catch (final EdmPrimitiveTypeException e) {
          throw new IOException(e);
        }
        entitySet.getAnnotations().add(annotation);
      } else if (field.getKey().charAt(0) == '#') {
        final Operation operation = new Operation();
        operation.setMetadataAnchor(field.getKey());

        final ObjectNode opNode = (ObjectNode) tree.get(field.getKey());
        operation.setTitle(opNode.get(Constants.ATTR_TITLE).asText());
        operation.setTarget(getUri(opNode.get(Constants.ATTR_TARGET).asText()));
        entitySet.getOperations().add(operation);
        toRemove.add(field.getKey());
      }
    }
    tree.remove(toRemove);
    return new ResWrap<>(contextURL, metadataETag, entitySet);
  }
  
  private URI getUri(String str) throws IOException {
    try {
      URL url = new URL(str);
      String scheme = url.getProtocol();
      String host = url.getHost();
      int port = url.getPort();
      String path = url.getPath();
      String baseUrl = scheme + "://" + host + (port != -1 ? ":" + port : "") + path;
      String query = url.getQuery();
      String anchor = StringUtils.substringAfterLast(str, "#");

      if (query == null || query.isEmpty()) {
        return URI.create(str);
      }

      StringBuilder fixedQuery = new StringBuilder();
      for (String param : query.split("&")) {
        int idx = param.indexOf('=');
        if (idx > 0) {
          String key = param.substring(0, idx);
          String value = param.substring(idx + 1);

          key = URLDecoder.decode(key, "UTF-8");
          value = URLDecoder.decode(value, "UTF-8");

          if (!key.startsWith("$")) {
            key = "$" + key;
          }

          String encodedKey = URLEncoder.encode(key, "UTF-8").replace("+", "%20");
          String encodedValue = URLEncoder.encode(value, "UTF-8").replace("+", "%20");

          if (fixedQuery.length() > 0) {
            fixedQuery.append("&");
          }

          fixedQuery.append(encodedKey).append("=").append(encodedValue);
        } else {
          String decoded = URLDecoder.decode(param, "UTF-8");
          fixedQuery.append(URLEncoder.encode(decoded, "UTF-8").replace("+", "%20"));
        }
      }
      
      if (StringUtils.isNotBlank(anchor)) {
        anchor = URLDecoder.decode(anchor, "UTF-8");
        String encodedAnchor = URLEncoder.encode(anchor, "UTF-8").replace("+", "%20");
        fixedQuery.append("#").append(encodedAnchor);
      }

      return new URI(baseUrl + "?" + fixedQuery);

    } catch (Exception e) {
      throw new IOException("Failed to normalize URI: " + str, e);
    }
  }
}
