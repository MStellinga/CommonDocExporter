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
 * A printing distance unit: cm, inch, or point.<br>
 * At 72 DPI (dots per inch): 72 points = 1 inch = 2,54 cm
 */
public enum DistanceUnit {

    PT { // Points

        public float toPoints(float value) {
            return value;
        }

        public float fromPoints(float value) {
            return value;
        }
    },
    IN { // Inch

        public float toPoints(float value) {
            return value * 72.0f;
        }

        public float fromPoints(float value) {
            return value / 72.0f;
        }
    },
    CM { // Cm

        public float toPoints(float value) {
            return value * 72.0f / 2.54f;
        }

        public float fromPoints(float value) {
            return value * 2.54f / 72.0f;
        }
    };


    public abstract float toPoints(float value);

    public abstract float fromPoints(float value);
}
