/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.mongodb.common.mapper;

import org.apache.storm.mongodb.common.MongoUtils;
import org.apache.storm.tuple.ITuple;
import org.bson.Document;

import java.util.List;

public class SimpleMongoMapper implements MongoMapper {

    private String[] fields;

    public SimpleMongoMapper(String... fields) {
        this.fields = fields;
    }

    @Override
    public Document toDocument(ITuple tuple) {
        Document document = new Document();
        for(String field : fields){
            document.append(field, tuple.getValueByField(field));
        }
        return document;
    }

    @Override
    public Document toDocumentByKeys(List<Object> keys) {
        Document document = new Document();
        document.append("_id", MongoUtils.getID(keys));
        return document;
    }

    public SimpleMongoMapper withFields(String... fields) {
        this.fields = fields;
        return this;
    }
}
