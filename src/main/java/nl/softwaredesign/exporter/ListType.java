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
 * Enumeration for types of lists
 */
public enum ListType {
    NONE{
        public String getValueFor(int number) {
            return "";
        }
    },
    BULLET {
        public String getValueFor(int number) {
            return "-";
        }
    },
    NUMBER {
        public String getValueFor(int number) {
            return "" + number;
        }
    },
    LETTER {
        public String getValueFor(int number) {
            StringBuffer buf = new StringBuffer();
            while (number > 0) {
                int t = (number - 1) % 26;
                int a = (int) 'a';
                buf.append((char) (a + t));
                number = number / 26;
            }
            return buf.toString();
        }
    },
    CAPITAL_LETTER {
        public String getValueFor(int number) {
            return LETTER.getValueFor(number).toUpperCase();
        }
    };

    public abstract String getValueFor(int number);

}
