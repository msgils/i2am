/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.apache.storm.perf.spout;


import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConstSpout extends BaseRichSpout {

    private static final String DEFAUT_FIELD_NAME = "str";
    private String value;
    private String fieldName = DEFAUT_FIELD_NAME;
    private SpoutOutputCollector collector = null;
    private int count=0;

    public ConstSpout(String value) {
        this.value = value;
    }

    public ConstSpout withOutputFields(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(fieldName));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        List<Object> tuple = Collections.singletonList(value);
        collector.emit(tuple, count++);
    }

    @Override
    public void ack(Object msgId) {
        super.ack(msgId);
    }

}
