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
 * Multi-page settings:<ul>
 * <li>All pages equal
 * <li>Different first page
 * <li>Different odd and even pages
 * <li>Different first page, odd pages, and even pages
 * </ul>
 */
public enum MultiPageType {

    ALL_EQUAL,
    DIFFERENT_FIRST,
    DIFFERENT_ODD_AND_EVEN,
    DIFFERENT_FIRST_ODD_AND_EVEN;
}
