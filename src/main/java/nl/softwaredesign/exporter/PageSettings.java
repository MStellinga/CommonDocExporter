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
 * Settings object for representing page settings: header, footer, margins, orientation, etc.
 */
public class PageSettings {

    private Integer id = null;

    // text of header and/or footer
    private String headerLeft;
    private String headerCenter;
    private String headerRight;
    private String footerLeft;
    private String footerCenter;
    private String footerRight;

    // Style of header and/or footer
    private Style headerLeftStyle;
    private Style headerCenterStyle;
    private Style headerRightStyle;
    private Style footerLeftStyle;
    private Style footerCenterStyle;
    private Style footerRightStyle;

    // Draw lines above/below header or footer
    private boolean lineBelowHeader;
    private boolean lineAboveHeader;
    private boolean lineBelowFooter;
    private boolean lineAboveFooter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeaderLeft() {
        return headerLeft;
    }

    public void setHeaderLeft(String headerLeft) {
        this.headerLeft = headerLeft;
    }

    public String getHeaderCenter() {
        return headerCenter;
    }

    public void setHeaderCenter(String headerCenter) {
        this.headerCenter = headerCenter;
    }

    public String getHeaderRight() {
        return headerRight;
    }

    public void setHeaderRight(String headerRight) {
        this.headerRight = headerRight;
    }

    public String getFooterLeft() {
        return footerLeft;
    }

    public void setFooterLeft(String footerLeft) {
        this.footerLeft = footerLeft;
    }

    public String getFooterCenter() {
        return footerCenter;
    }

    public void setFooterCenter(String footerCenter) {
        this.footerCenter = footerCenter;
    }

    public String getFooterRight() {
        return footerRight;
    }

    public void setFooterRight(String footerRight) {
        this.footerRight = footerRight;
    }

    public Style getHeaderLeftStyle() {
        return headerLeftStyle;
    }

    public void setHeaderLeftStyle(Style headerLeftStyle) {
        this.headerLeftStyle = headerLeftStyle;
    }

    public Style getHeaderCenterStyle() {
        return headerCenterStyle;
    }

    public void setHeaderCenterStyle(Style headerCenterStyle) {
        this.headerCenterStyle = headerCenterStyle;
    }

    public Style getHeaderRightStyle() {
        return headerRightStyle;
    }

    public void setHeaderRightStyle(Style headerRightStyle) {
        this.headerRightStyle = headerRightStyle;
    }

    public Style getFooterLeftStyle() {
        return footerLeftStyle;
    }

    public void setFooterLeftStyle(Style footerLeftStyle) {
        this.footerLeftStyle = footerLeftStyle;
    }

    public Style getFooterCenterStyle() {
        return footerCenterStyle;
    }

    public void setFooterCenterStyle(Style footerCenterStyle) {
        this.footerCenterStyle = footerCenterStyle;
    }

    public Style getFooterRightStyle() {
        return footerRightStyle;
    }

    public void setFooterRightStyle(Style footerRightStyle) {
        this.footerRightStyle = footerRightStyle;
    }

    public boolean isLineBelowHeader() {
        return lineBelowHeader;
    }

    public void setLineBelowHeader(boolean lineBelowHeader) {
        this.lineBelowHeader = lineBelowHeader;
    }

    public boolean isLineAboveHeader() {
        return lineAboveHeader;
    }

    public void setLineAboveHeader(boolean lineAboveHeader) {
        this.lineAboveHeader = lineAboveHeader;
    }

    public boolean isLineBelowFooter() {
        return lineBelowFooter;
    }

    public void setLineBelowFooter(boolean lineBelowFooter) {
        this.lineBelowFooter = lineBelowFooter;
    }

    public boolean isLineAboveFooter() {
        return lineAboveFooter;
    }

    public void setLineAboveFooter(boolean lineAboveFooter) {
        this.lineAboveFooter = lineAboveFooter;
    }
}
