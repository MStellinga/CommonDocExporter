/*
Copyright 2012-2013 Martijn Stellinga

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package nl.softwaredesign.exporter;

/**
 * Enumeration for different types of Linespacing
 */
public enum LineSpacing {

    SINGLE(100),
    ONE_AND_A_HALF(150),
    DOUBLE(200),
    DOUBLE_AND_A_HALF(250),
    TRIPLE(300);

    private final int percentage;

    private LineSpacing(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }

}
