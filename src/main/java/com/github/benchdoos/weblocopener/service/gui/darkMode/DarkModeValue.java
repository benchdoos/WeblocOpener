/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.service.gui.darkMode;

public class DarkModeValue {
    private TimeRange previous;
    private TimeRange next;
    private double longitude;
    private double latitude;

    public DarkModeValue(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public DarkModeValue(TimeRange previous, TimeRange next) {
        this.previous = previous;
        this.next = next;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public TimeRange getNext() {
        return next;
    }

    public void setNext(TimeRange next) {
        this.next = next;
    }

    public TimeRange getPrevious() {
        return previous;
    }

    public void setPrevious(TimeRange previous) {
        this.previous = previous;
    }
}
