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
 * An enumeration of print page sizes
 */
public enum PageSize {

    A0(841f, 1189f),
    A1(594f, 841f),
    A2(420f, 594f),
    A3(297f, 420f),
    A4(210f, 297f),
    A5(148f, 210f),
    A6(105f, 148f),
    A7(74f, 105f),
    A8(52f, 74f),
    A9(37f, 52f),
    A10(26f, 37f),
    LETTER(215.9f, 279.4f);

    private float widthInch;
    private float heightInch;
    private float widthMM;
    private float heightMM;

    public float getWidthInch() {
        return widthInch;
    }

    public float getHeightInch() {
        return heightInch;
    }

    public float getWidthMM() {
        return widthMM;
    }

    public float getHeightMM() {
        return heightMM;
    }

    public float getWidthPoints(float dpi) {
        return widthInch * dpi;
    }

    public float getHeightPoints(float dpi) {
        return heightInch * dpi;
    }

    private PageSize(float widthMM, float heightMM) {
        this.widthMM = widthMM;
        this.heightMM = heightMM;
        this.widthInch = widthMM / 25.4f;
        this.heightInch = heightMM / 25.4f;
    }
}
