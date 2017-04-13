/*
** Copyright (C) 2013 Mellanox Technologies
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at:
**
** http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
** either express or implied. See the License for the specific language
** governing permissions and  limitations under the License.
**
*/
package org.apache.storm.messaging.org.accelio.jxio.impl;


public class EventSession extends Event {
	private int errorType;
	private int reason;

	public EventSession(int eventType, long id, int error, int r) {
		super(eventType, id);
		this.errorType = error;
		this.reason = r;
	}

	public int getErrorType() {
		return errorType;
	}

	public int getReason() {
		return reason;
	}
}
