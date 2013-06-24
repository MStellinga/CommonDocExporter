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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * A helper class to hold all the style settings for a section.
 */
public class SectionStyle {

    private Logger logger = Logger.getLogger(SectionStyle.class);

    private Orientation orientation = Orientation.PORTRAIT;
    private PageSize pageSize = PageSize.A4;
    private int columns = 1;
    private float marginLeft = 72.0f;
    private float marginTop = 72.0f;
    private float marginRight = 72.0f;
    private float marginBottom = 72.0f;

    private LineSpacing lineSpacing;
    private float firstLineIndent;
    private float indentSize;

    private MultiPageType multiPageType;
    private PageSettings allPageSettings;
    private PageSettings firstPageSettings;
    private PageSettings oddPageSettings;
    private PageSettings evenPageSettings;

    public SectionStyle() {
    }

    public MultiPageType getMultiPageType() {
        return multiPageType;
    }

    public void setMultiPageType(MultiPageType multiPageType) {
        this.multiPageType = multiPageType;
    }

    public void setAllPageSettings(PageSettings allPageSettings) {
        this.allPageSettings = allPageSettings;
    }

    public void setFirstPageSettings(PageSettings firstPageSettings) {
        this.firstPageSettings = firstPageSettings;
    }

    public void setOddPageSettings(PageSettings oddPageSettings) {
        this.oddPageSettings = oddPageSettings;
    }

    public void setEvenPageSettings(PageSettings evenPageSettings) {
        this.evenPageSettings = evenPageSettings;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        if (orientation == null) {
            orientation = Orientation.PORTRAIT;
        }
        this.orientation = orientation;
    }

    public PageSize getPageSize() {
        return pageSize;
    }

    public void setPageSize(PageSize pageSize) {
        if (pageSize == null) {
            pageSize = PageSize.A4;
        }
        this.pageSize = pageSize;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        if (columns < 1) {
            columns = 1;
        }
        this.columns = columns;
    }

    public LineSpacing getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(LineSpacing lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(Float firstLineIndent) {
        this.firstLineIndent = firstLineIndent == null ? 0.0f : firstLineIndent;
    }

    public float getIndentSize() {
        return indentSize;
    }

    public void setIndentSize(Float indentSize) {
        this.indentSize = indentSize == null ? 0.0f : indentSize;
    }

    public String getHeaderLeft(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderLeft();
    }

    public String getHeaderCenter(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderCenter();
    }

    public String getHeaderRight(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderRight();
    }

    public boolean hasHeader(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return StringUtils.isNotEmpty(settings.getHeaderLeft()) ||
                StringUtils.isNotEmpty(settings.getHeaderCenter()) ||
                StringUtils.isNotEmpty(settings.getHeaderRight());
    }

    public Style getHeaderLeftStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderLeftStyle();
    }

    public Style getHeaderCenterStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderCenterStyle();
    }

    public Style getHeaderRightStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getHeaderRightStyle();
    }

    public boolean isLineAboveHeader(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.isLineAboveHeader();
    }

    public boolean isLineBelowHeader(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.isLineBelowHeader();
    }

    public String getFooterLeft(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterLeft();
    }

    public String getFooterCenter(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterCenter();
    }

    public String getFooterRight(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterRight();
    }

    public Style getFooterLeftStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterLeftStyle();
    }

    public Style getFooterCenterStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterCenterStyle();
    }

    public Style getFooterRightStyle(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.getFooterRightStyle();
    }

    public boolean hasFooter(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return StringUtils.isNotEmpty(settings.getFooterLeft()) ||
                StringUtils.isNotEmpty(settings.getFooterCenter()) ||
                StringUtils.isNotEmpty(settings.getFooterRight());
    }

    public boolean isLineAboveFooter(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.isLineAboveFooter();
    }

    public boolean isLineBelowFooter(PageType pageType) {
        PageSettings settings = getSettings(pageType);
        return settings.isLineBelowFooter();
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    private PageSettings getSettings(PageType type) {
        PageSettings result = null;
        switch (multiPageType) {
            case ALL_EQUAL:
                result = allPageSettings;
                break;
            case DIFFERENT_FIRST:
                if (type == PageType.FIRST_PAGE_EVEN || type == PageType.FIRST_PAGE_ODD) {
                    result = firstPageSettings;
                } else {
                    result = allPageSettings;
                }
                break;
            case DIFFERENT_FIRST_ODD_AND_EVEN:
                if (type == PageType.FIRST_PAGE_EVEN || type == PageType.FIRST_PAGE_ODD) {
                    result = firstPageSettings;
                } else if (type == PageType.ODD_PAGE) {
                    result = oddPageSettings;
                } else if (type == PageType.EVEN_PAGE) {
                    result = evenPageSettings;
                }
                break;
            case DIFFERENT_ODD_AND_EVEN:
                if (type == PageType.FIRST_PAGE_EVEN || type == PageType.EVEN_PAGE) {
                    result = evenPageSettings;
                } else if (type == PageType.ODD_PAGE || type == PageType.FIRST_PAGE_ODD) {
                    result = oddPageSettings;
                }
                break;
        }
        if (result != null) {
            return result;
        } else {
            logger.warn("Page Settings are empty");
            return new PageSettings();
        }

    }

    @Override
    public String toString() {
        return "" + orientation + " " + pageSize +
                " [" + marginTop + "," + marginRight + "," + marginBottom + "," + marginLeft +
                "] " + columns;
    }
}
