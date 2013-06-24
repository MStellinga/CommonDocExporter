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
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.*;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.*;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.*;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.list.*;
import org.odftoolkit.simple.text.list.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Format used to create an ODT file.
 */
public class ODTExportFormat implements ExportFormat {

    private final static Logger logger = Logger.getLogger(ODTExportFormat.class);

    private Paragraph currentParagraph = null;
    private java.util.List<List> lists = new ArrayList<>();

    private TextDocument document;

    private java.util.List<ODFStyleWrapper> styles = new ArrayList<>();
    private Set<String> styleNames = new HashSet<>();

    private int sectionStyleCounter = 0;
    private OdfStyle newSectionStyle = null;
    private float indentSizePt;
    private float firstLineIndentPt;
    private LineSpacing lineSpacing;
    private Map<String, OdfStyle> paragraphStyles = new HashMap<>();

    private DecimalFormat numberFormat = new DecimalFormat("0.000");
    private final static String TABLE_TOP_LINE = "TTL";
    private final static String TABLE_BOTTOM_LINE = "TBL";
    private final static String TABLE_BOTH_LINES = "TBL";
    private final static String CELL_CENTER = "CellCenter";
    private final static String CELL_RIGHT = "CellRight";
    private SectionStyle currentSectionStyle;

    private int imageCounter = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void startNewFile(File targetFile, String author, String title, String language) throws IOException {
        logger.debug("Start file " + targetFile);

        try {
            document = TextDocument.newTextDocument();

            OdfStyle tableTopLine = getAutoStyles().newStyle(OdfStyleFamily.TableCell);
            tableTopLine.setStyleNameAttribute(TABLE_TOP_LINE);
            tableTopLine.setStyleDisplayNameAttribute("Table top line");
            tableTopLine.setProperty(OdfTableCellProperties.BorderBottom, "none");
            tableTopLine.setProperty(OdfTableCellProperties.BorderTop, "0.05pt solid #000000");
            tableTopLine.setProperty(OdfTableCellProperties.BorderLeft, "none");
            tableTopLine.setProperty(OdfTableCellProperties.BorderRight, "none");

            OdfStyle tableBottomLine = getAutoStyles().newStyle(OdfStyleFamily.TableCell);
            tableBottomLine.setStyleNameAttribute(TABLE_BOTTOM_LINE);
            tableBottomLine.setStyleDisplayNameAttribute("Table bottom line");
            tableBottomLine.setProperty(OdfTableCellProperties.BorderBottom, "0.05pt solid #000000");
            tableBottomLine.setProperty(OdfTableCellProperties.BorderTop, "none");
            tableBottomLine.setProperty(OdfTableCellProperties.BorderLeft, "none");
            tableBottomLine.setProperty(OdfTableCellProperties.BorderRight, "none");

            OdfStyle tableBothLines = getAutoStyles().newStyle(OdfStyleFamily.TableCell);
            tableBothLines.setStyleNameAttribute(TABLE_BOTH_LINES);
            tableBothLines.setStyleDisplayNameAttribute("Table both lines");
            tableBothLines.setProperty(OdfTableCellProperties.BorderBottom, "0.05pt solid #000000");
            tableBothLines.setProperty(OdfTableCellProperties.BorderTop, "0.05pt solid #000000");
            tableBothLines.setProperty(OdfTableCellProperties.BorderLeft, "none");
            tableBothLines.setProperty(OdfTableCellProperties.BorderRight, "none");

            OdfStyle cellCenter = getAutoStyles().newStyle(OdfStyleFamily.Paragraph);
            cellCenter.setStyleNameAttribute(CELL_CENTER);
            cellCenter.setStyleDisplayNameAttribute("Cell center");
            cellCenter.setProperty(OdfParagraphProperties.TextAlign, "center");

            OdfStyle cellRight = getAutoStyles().newStyle(OdfStyleFamily.Paragraph);
            cellRight.setStyleNameAttribute(CELL_RIGHT);
            cellRight.setStyleDisplayNameAttribute("Cell right");
            cellRight.setProperty(OdfParagraphProperties.TextAlign, "end");

        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endFile(File targetFile, boolean firstPass) throws IOException {
        logger.debug("End file");
        try {
            document.save(targetFile);
            reset();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            reset();
        }
    }

    private void reset() {
        styles.clear();
        styleNames.clear();
        currentParagraph = null;
        lists.clear();
        sectionStyleCounter = 0;
        newSectionStyle = null;
        indentSizePt = 0.0f;
        firstLineIndentPt = 0.0f;
        lineSpacing = null;
        paragraphStyles.clear();
        imageCounter = 0;
    }

    /**
     * @param toConvert color to convert
     * @return the six character hex string (rrggbb) for the color
     */
    private static String rgbHexValue(Color toConvert) {
        StringBuilder result = new StringBuilder();

        result.append(Integer.toHexString(toConvert.getRed()));
        if (result.length() < 2) {
            result.insert(0, '0');
        }

        result.append(Integer.toHexString(toConvert.getGreen()));
        if (result.length() < 4) {
            result.insert(2, '0');
        }

        result.append(Integer.toHexString(toConvert.getBlue()));
        if (result.length() < 6) {
            result.insert(4, '0');
        }

        return result.toString();
    }

    private ODFStyleWrapper getOrCreateStyle(Style style) {
        ODFStyleWrapper found = null;
        if (style == null) {
            return found;
        }
        int index = 0;
        while (found == null && index < styles.size()) {
            ODFStyleWrapper current = styles.get(index);
            if (current.style!=null && style.equals(current.style)) {
                found = current;
            }
            index++;
        }
        if (found == null) {
            // Create stylename
            String stylename = "SDNL style 1";
            if (style.getName() != null && style.getName().length() > 0 && !"Default".equalsIgnoreCase(style.getName())) {
                stylename = style.getName();
            }
            String basename = stylename;
            int counter = 1;
            while (styleNames.contains(stylename)) {
                stylename = basename + " " + counter;
                counter++;
            }
            styleNames.add(stylename);
            // Create style
            OdfStyle odfStyle = document.getOrCreateDocumentStyles().newStyle(stylename, OdfStyleFamily.Text);
            odfStyle.setStyleDisplayNameAttribute(stylename);
            found = new ODFStyleWrapper();
            found.style = style;
            found.name = stylename;
            found.odfStyle = odfStyle;
            found.applyToStyle();
            styles.add(found);
        }
        return found;
    }

    private void createSectionStyle(SectionStyle sectionStyle) throws DocumentExportException {
        sectionStyleCounter++;
        String styleName = "SDNL" + sectionStyleCounter;
        String styleDisplayName = "SDNL Section " + sectionStyleCounter;
        String stylePageLayoutName = "SDNLSS" + sectionStyleCounter;
        String styleTableName = "Table" + sectionStyleCounter;
        String styleTableColumnName = "Table" + sectionStyleCounter + ".A";
        String paragraphStyleName = "S" + styleName; // Style for first paragraph of new section

        // Page layout
        StylePageLayoutElement pageLayoutStyle = getAutoStyles().newStylePageLayoutElement(stylePageLayoutName);
        // Page settings
        float marginLeft = DistanceUnit.IN.fromPoints(sectionStyle.getMarginLeft());
        float marginTop = DistanceUnit.IN.fromPoints(sectionStyle.getMarginTop());
        float marginRight = DistanceUnit.IN.fromPoints(sectionStyle.getMarginRight());
        float marginBottom = DistanceUnit.IN.fromPoints(sectionStyle.getMarginBottom());
        float pageWidth, pageHeight;
        if (sectionStyle.getOrientation() == Orientation.PORTRAIT) {
            pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "portrait");
            pageWidth = sectionStyle.getPageSize().getWidthInch();
            pageHeight = sectionStyle.getPageSize().getHeightInch();
        } else {
            pageLayoutStyle.setProperty(OdfPageLayoutProperties.PrintOrientation, "landscape");
            pageWidth = sectionStyle.getPageSize().getHeightInch();
            pageHeight = sectionStyle.getPageSize().getWidthInch();
        }
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageWidth, "" + numberFormat.format(pageWidth) + "in");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.PageHeight, "" + numberFormat.format(pageHeight) + "in");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.MarginLeft, "" + numberFormat.format(marginLeft) + "in");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.MarginTop, "" + numberFormat.format(marginTop) + "in");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.MarginRight, "" + numberFormat.format(marginRight) + "in");
        pageLayoutStyle.setProperty(OdfPageLayoutProperties.MarginBottom, "" + numberFormat.format(marginBottom) + "in");

        // Create header/footer table style
        float tableWidth = pageWidth - marginLeft - marginRight;
        OdfStyle tableStyle = getAutoStyles().newStyle(OdfStyleFamily.Table);
        tableStyle.setStyleNameAttribute(styleTableName);
        tableStyle.setProperty(OdfTableProperties.Width, "" + numberFormat.format(tableWidth) + "in");
        tableStyle.setProperty(OdfTableProperties.Align, "margins");
        tableStyle.setProperty(OdfTableProperties.BorderModel, "separating");
        OdfStyle tableColumnStyle = getAutoStyles().newStyle(OdfStyleFamily.TableColumn);
        tableColumnStyle.setStyleNameAttribute(styleTableColumnName);
        tableColumnStyle.setProperty(OdfTableProperties.Width, "" + numberFormat.format(tableWidth / 3.0f) + "in");

        // Create master page layoyut
        MultiPageType pageType = sectionStyle.getMultiPageType();
        StyleMasterPageElement masterPageStyle = getOrCreateMasterPageStyle(styleName, styleDisplayName, stylePageLayoutName);
        if (pageType == MultiPageType.DIFFERENT_ODD_AND_EVEN || pageType == MultiPageType.DIFFERENT_FIRST_ODD_AND_EVEN) {
            // Add odd and even header
            if (sectionStyle.hasHeader(PageType.ODD_PAGE)) {
                addHeaderTable(styleTableName, masterPageStyle.newStyleHeaderElement(), sectionStyle, PageType.ODD_PAGE);
            }
            if (sectionStyle.hasFooter(PageType.ODD_PAGE)) {
                addFooterTable(styleTableName, masterPageStyle.newStyleFooterElement(), sectionStyle, PageType.ODD_PAGE);
            }
            if (sectionStyle.hasHeader(PageType.EVEN_PAGE)) {
                addHeaderTable(styleTableName, masterPageStyle.newStyleHeaderLeftElement(), sectionStyle, PageType.ODD_PAGE);
            }
            if (sectionStyle.hasFooter(PageType.EVEN_PAGE)) {
                addFooterTable(styleTableName, masterPageStyle.newStyleFooterLeftElement(), sectionStyle, PageType.ODD_PAGE);
            }

        } else {
            // Add normal header and footer
            if (sectionStyle.hasHeader(PageType.ODD_PAGE)) {
                addHeaderTable(styleTableName, masterPageStyle.newStyleHeaderElement(), sectionStyle, PageType.ODD_PAGE);
            }
            if (sectionStyle.hasFooter(PageType.ODD_PAGE)) {
                addFooterTable(styleTableName, masterPageStyle.newStyleFooterElement(), sectionStyle, PageType.ODD_PAGE);
            }
        }
        if (pageType == MultiPageType.DIFFERENT_FIRST || pageType == MultiPageType.DIFFERENT_FIRST_ODD_AND_EVEN) {
            StyleMasterPageElement firstPageStyle = getOrCreateMasterPageStyle(
                    "first_page_" + styleName,
                    "First Page " + styleDisplayName,
                    "first_page_" + stylePageLayoutName);
            // Add first page, with a next style pointing at the Standard style
            if (sectionStyle.hasHeader(PageType.FIRST_PAGE_ODD)) {
                addHeaderTable(styleTableName, firstPageStyle.newStyleHeaderElement(), sectionStyle, PageType.FIRST_PAGE_ODD);
            }
            if (sectionStyle.hasFooter(PageType.FIRST_PAGE_ODD)) {
                addFooterTable(styleTableName, firstPageStyle.newStyleFooterElement(), sectionStyle, PageType.FIRST_PAGE_ODD);
            }
        }

        //  Columns
        if (sectionStyle.getColumns() > 1) {
            StylePageLayoutPropertiesElement props = (StylePageLayoutPropertiesElement) pageLayoutStyle.getOrCreatePropertiesElement(OdfStylePropertiesSet.PageLayoutProperties);
            props.newStyleColumnsElement(sectionStyle.getColumns());
        }

        newSectionStyle = document.getOrCreateDocumentStyles().newStyle(paragraphStyleName, OdfStyleFamily.Paragraph);
        newSectionStyle.setStyleDisplayNameAttribute(styleDisplayName);
        newSectionStyle.setStyleMasterPageNameAttribute(styleName);
    }

    private void addHeaderTable(String styleTableName, StyleHeaderElement header, SectionStyle sectionStyle,
                                PageType pageType) {
        String left = sectionStyle.getHeaderLeft(pageType);
        Style leftStyle = sectionStyle.getHeaderLeftStyle(pageType);
        String center = sectionStyle.getHeaderCenter(pageType);
        Style centerStyle = sectionStyle.getHeaderCenterStyle(pageType);
        String right = sectionStyle.getHeaderRight(pageType);
        Style rightStyle = sectionStyle.getHeaderRightStyle(pageType);
        TableTableElement table = header.newTableTableElement();
        boolean lineAbove = sectionStyle.isLineAboveHeader(pageType);
        boolean lineBelow = sectionStyle.isLineBelowHeader(pageType);
        fillTable(styleTableName, table, leftStyle, left, centerStyle, center, rightStyle, right, lineAbove, lineBelow);
    }

    private void addFooterTable(String styleTableName, StyleFooterElement footer, SectionStyle sectionStyle,
                                PageType pageType) {
        String left = sectionStyle.getFooterLeft(pageType);
        Style leftStyle = sectionStyle.getFooterLeftStyle(pageType);
        String center = sectionStyle.getFooterCenter(pageType);
        Style centerStyle = sectionStyle.getFooterCenterStyle(pageType);
        String right = sectionStyle.getFooterRight(pageType);
        Style rightStyle = sectionStyle.getFooterRightStyle(pageType);
        TableTableElement table = footer.newTableTableElement();
        boolean lineAbove = sectionStyle.isLineAboveFooter(pageType);
        boolean lineBelow = sectionStyle.isLineBelowFooter(pageType);
        fillTable(styleTableName, table, leftStyle, left, centerStyle, center, rightStyle, right, lineAbove, lineBelow);
    }

    private void addHeaderTable(String styleTableName, StyleHeaderLeftElement header, SectionStyle sectionStyle,
                                PageType pageType) {
        String left = sectionStyle.getHeaderLeft(pageType);
        Style leftStyle = sectionStyle.getHeaderLeftStyle(pageType);
        String center = sectionStyle.getHeaderCenter(pageType);
        Style centerStyle = sectionStyle.getHeaderCenterStyle(pageType);
        String right = sectionStyle.getHeaderRight(pageType);
        Style rightStyle = sectionStyle.getHeaderRightStyle(pageType);
        TableTableElement table = header.newTableTableElement();
        boolean lineAbove = sectionStyle.isLineAboveHeader(pageType);
        boolean lineBelow = sectionStyle.isLineBelowHeader(pageType);
        fillTable(styleTableName, table, leftStyle, left, centerStyle, center, rightStyle, right, lineAbove, lineBelow);
    }

    private void addFooterTable(String styleTableName, StyleFooterLeftElement footer, SectionStyle sectionStyle,
                                PageType pageType) {
        String left = sectionStyle.getFooterLeft(pageType);
        Style leftStyle = sectionStyle.getFooterLeftStyle(pageType);
        String center = sectionStyle.getFooterCenter(pageType);
        Style centerStyle = sectionStyle.getFooterCenterStyle(pageType);
        String right = sectionStyle.getFooterRight(pageType);
        Style rightStyle = sectionStyle.getFooterRightStyle(pageType);
        TableTableElement table = footer.newTableTableElement();
        boolean lineAbove = sectionStyle.isLineAboveFooter(pageType);
        boolean lineBelow = sectionStyle.isLineBelowFooter(pageType);
        fillTable(styleTableName, table, leftStyle, left, centerStyle, center, rightStyle, right, lineAbove, lineBelow);
    }

    private void fillTable(String styleTableName, TableTableElement table, final Style leftStyle, String left,
                           final Style centerStyle, String center, final Style rightStyle, String right,
                           boolean lineAbove, boolean lineBelow) {

        table.setTableStyleNameAttribute(styleTableName);

        String cellStyleName = null;
        if (lineAbove && lineBelow) {
            cellStyleName = TABLE_BOTH_LINES;
        } else if (lineAbove) {
            cellStyleName = TABLE_TOP_LINE;
        } else if (lineBelow) {
            cellStyleName = TABLE_BOTTOM_LINE;
        }

        // Add columns with width style
        TableTableColumnElement columns = table.newTableTableColumnElement();
        columns.setTableNumberColumnsRepeatedAttribute(3);
        columns.setTableStyleNameAttribute(styleTableName + ".A");

        // Row
        TableTableRowElement row = table.newTableTableRowElement();

        TableTableCellElement cell = row.newTableTableCellElement(0.0, "string");
        if (cellStyleName != null) {
            cell.setStyleName(cellStyleName);
        }
        TextPElement paragraph = cell.newTextPElement();
        final Paragraph paragraphLeft = Paragraph.getInstanceof(paragraph);
        addStyledTextToParagraph(left, leftStyle, paragraphLeft);

        cell = row.newTableTableCellElement(0.0, "string");
        if (cellStyleName != null) {
            cell.setStyleName(cellStyleName);
        }
        paragraph = cell.newTextPElement();
        paragraph.setStyleName(CELL_CENTER);
        final Paragraph paragraphCenter = Paragraph.getInstanceof(paragraph);
        addStyledTextToParagraph(center, centerStyle, paragraphCenter);

        cell = row.newTableTableCellElement(0.0, "string");
        if (cellStyleName != null) {
            cell.setStyleName(cellStyleName);
        }
        paragraph = cell.newTextPElement();
        paragraph.setStyleName(CELL_RIGHT);
        final Paragraph paragraphRight = Paragraph.getInstanceof(paragraph);
        addStyledTextToParagraph(right, rightStyle, paragraphRight);
    }

    /**
     * Fetch a master page style. To prevent a section break on the first page,
     * try to retrieve 'Standard' for the first item.
     */
    private StyleMasterPageElement getOrCreateMasterPageStyle(String styleName, String styleDisplayName, String stylePageLayoutName) throws DocumentExportException {
        try {
            OfficeDocumentStylesElement rootElement = document.getStylesDom().getRootElement();
            OfficeMasterStylesElement masterStyles = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class,
                    rootElement);
            if (masterStyles == null) {
                masterStyles = rootElement.newOfficeMasterStylesElement();
            }
            StyleMasterPageElement masterPageStyle = null;
            if (sectionStyleCounter == 1) {
                NodeList lastMasterPages = masterStyles.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(),
                        "master-page");
                if (lastMasterPages != null && lastMasterPages.getLength() > 0) {
                    for (int i = 0; i < lastMasterPages.getLength(); i++) {
                        StyleMasterPageElement masterPage = (StyleMasterPageElement) lastMasterPages.item(i);
                        String styleName2 = masterPage.getStyleNameAttribute();
                        if ("Standard".equals(styleName2)) {
                            masterPageStyle = masterPage;
                            masterPageStyle.setStylePageLayoutNameAttribute(stylePageLayoutName);
                            break;
                        }
                    }
                }
            }
            if (masterPageStyle == null) {
                masterPageStyle = masterStyles.newStyleMasterPageElement(styleName, stylePageLayoutName);
                masterPageStyle.setStyleDisplayNameAttribute(styleDisplayName);
            }
            return masterPageStyle;
        } catch (Exception e) {
            throw new DocumentExportException("Error editing master styles", e);
        }
    }

    private OdfOfficeAutomaticStyles getAutoStyles() throws DocumentExportException {
        try {
            return document.getStylesDom().getAutomaticStyles();
        } catch (Exception e) {
            throw new DocumentExportException("Error editing master styles", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeText(String text, Style style, Object bookmarkId, String bookmarkName) throws DocumentExportException {
        logger.debug("Write " + style + ":" + text);
        Paragraph paragraph = currentParagraph;
        addStyledTextToParagraph(text, style, paragraph);
    }

    private void addPageNumberToParagraph(Paragraph paragraph) {
        OdfFileDom odfFileDom = (OdfFileDom) paragraph.getOdfElement().getOwnerDocument();
        TextPageNumberElement number = odfFileDom.newOdfElement(TextPageNumberElement.class);
        paragraph.getOdfElement().appendChild(number);
    }

    private void addPageCountToParagraph(Paragraph paragraph) {
        OdfFileDom odfFileDom = (OdfFileDom) paragraph.getOdfElement().getOwnerDocument();
        TextPageCountElement number = odfFileDom.newOdfElement(TextPageCountElement.class);
        paragraph.getOdfElement().appendChild(number);
    }

    private void addStyledTextToParagraph(String text, Style style, Paragraph paragraph) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        ODFStyleWrapper styleWrapper = getOrCreateStyle(style);
        // Wow, OdfToolkit is a great high-level API :(
        OdfFileDom odfFileDom = (OdfFileDom) paragraph.getOdfElement().getOwnerDocument();
        TextSpanElement span = odfFileDom.newOdfElement(TextSpanElement.class);
        if (styleWrapper != null) {
            span.setTextStyleNameAttribute(styleWrapper.name);
        }
        span.setTextContent(text);
        paragraph.getOdfElement().appendChild(span);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void writePageBreak() throws DocumentExportException {
//        document.addPageBreak();
//        currentParagraph = null;
        startSection(currentSectionStyle);
    }

    private void initParagraphStyle(OdfStyle style, int indent, Alignment alignment) {
        float indentSize = (float) indent * indentSizePt;
        if (indent > 0) {
            float indentSizeIn = DistanceUnit.IN.fromPoints(indentSize);
            style.setProperty(OdfParagraphProperties.MarginLeft, "" + indentSizeIn + "in");
        }
        if (firstLineIndentPt > 0) {
            float firstLineIndentIn = DistanceUnit.IN.fromPoints(firstLineIndentPt);
            style.setProperty(OdfParagraphProperties.TextIndent, "" + firstLineIndentIn + "in");
        }
        if (lineSpacing != null && lineSpacing != LineSpacing.SINGLE) {
            style.setProperty(OdfParagraphProperties.LineHeight, "" + lineSpacing.getPercentage() + "%");
        }
        if (alignment != Alignment.LEFT) {
            style.setProperty(OdfParagraphProperties.TextAlign, alignment.name().toLowerCase());
        }
    }

    private OdfStyle getOrCreateParagraphStyle(int indent, Alignment alignment) throws DocumentExportException {
        String name = "TPS" + sectionStyleCounter + alignment + indent;
        OdfStyle style = paragraphStyles.get(name);
        if (style == null) {
            style = document.getOrCreateDocumentStyles().newStyle(name, OdfStyleFamily.Paragraph);
//            style = getAutoStyles().newStyle(OdfStyleFamily.Paragraph);
//            style.setStyleNameAttribute(name);
            String displayName = "Section " + sectionStyleCounter + " paragraph " + alignment.toString().toLowerCase();
            if (indent != 0) {
                displayName += indent;
            }
            style.setStyleDisplayNameAttribute(displayName);
            initParagraphStyle(style, indent, alignment);
            paragraphStyles.put(name, style);
        }
        return style;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newParagraph(int indent, Alignment alignment, ListType listType,
                             float firstLineIndent, float indentSize, LineSpacing spacing) throws DocumentExportException {
        logger.debug("New paragraph " + indent + ":" + alignment + ":" + listType);
        if (listType == null) {
            currentParagraph = document.addParagraph("");
            if (newSectionStyle != null) {
                // Start new Section
                initParagraphStyle(newSectionStyle, indent, alignment);
                currentParagraph.getOdfElement().setTextStyleNameAttribute(newSectionStyle.getStyleNameAttribute());
                newSectionStyle = null;
            } else {
                OdfStyle style = getOrCreateParagraphStyle(indent, alignment);
                currentParagraph.getOdfElement().setTextStyleNameAttribute(style.getStyleNameAttribute());
            }
            lists.clear();
        } else {
            if (newSectionStyle != null) {
                // Start new Section                       -
                currentParagraph = document.addParagraph("");
                initParagraphStyle(newSectionStyle, indent, alignment);
                currentParagraph.setStyleName(newSectionStyle.getStyleNameAttribute());
                newSectionStyle = null;
            } else {
                OdfStyle style = getOrCreateParagraphStyle(indent, alignment);

                currentParagraph.getOdfElement().setTextStyleNameAttribute(style.getStyleNameAttribute());
            }
            // Remove if too many nested lists
            for (int i = lists.size() - 1; i > indent - 1; i--) {
                lists.remove(i);
            }
            // Add if no list
            List list;
            if (lists.size() == 0) {
                list = document.addList(createDecorator(listType));
                lists.add(list);
            } else {
                list = lists.get(lists.size() - 1);
            }
            // Add if too few nested lists
            for (int i = lists.size(); i <= indent - 1; i++) {
                ListItem currentListItem;
                if (list.getItems().size() > 0) {
                    currentListItem = list.getItem(list.getItems().size() - 1);
                } else {
                    currentListItem = list.addItem("");
                }
                list = currentListItem.addList(createDecorator(listType));
                lists.add(list);
            }
            ListItem currentListItem = list.addItem("");
            currentParagraph = Paragraph.getInstanceof((TextParagraphElementBase) currentListItem.getOdfElement().getLastChild());
        }
    }

    private ListDecorator createDecorator(ListType listType) {
        switch (listType) {
            case BULLET:
                return new BulletDecorator(document);
            case NUMBER:
                return new NumberDecorator(document);
            default:
                return new BulletDecorator(document);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void startSection(SectionStyle sectionStyle) throws DocumentExportException {
        logger.debug("start section " + sectionStyle);
        currentSectionStyle = sectionStyle;
        paragraphStyles.clear();
        createSectionStyle(sectionStyle);
        indentSizePt = sectionStyle.getIndentSize();
        firstLineIndentPt = sectionStyle.getIndentSize();
        lineSpacing = sectionStyle.getLineSpacing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endSection() throws DocumentExportException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNote(String title, String body, String author, String authorInitials) throws DocumentExportException {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeImage(ImageIcon image, boolean scaleToPage) throws DocumentExportException {
        if(scaleToPage){
            logger.debug("Add image " + image);
        } else {
            logger.debug("Add scaled image " + image);
        }
        try {
            if(currentParagraph == null) {
                newParagraph(0,Alignment.CENTER,null,0.0f,0.0f,LineSpacing.SINGLE);
            }
            // Again, we have to do this by hand. Go ODF toolkit :(
            OdfFileDom odfFileDom = (OdfFileDom) currentParagraph.getOdfElement().getOwnerDocument();
            OdfDrawFrame frame = odfFileDom.newOdfElement(OdfDrawFrame.class);

            int imgWidth = 0;
            int imgHeight = 0;
            if(scaleToPage) {
                frame.setTextAnchorTypeAttribute("paragraph");
                int pgWidth = (int)currentSectionStyle.getPageSize().getWidthPoints(72.0f);
                int pgHeight = (int)currentSectionStyle.getPageSize().getHeightPoints(72.0f);
                if(currentSectionStyle.getOrientation() == Orientation.LANDSCAPE ||
                        currentSectionStyle.getOrientation() == Orientation.REVERSE_LANDSCAPE) {
                    // Landscape, switch width and height
                    int tmp = pgWidth;
                    pgWidth = pgHeight;
                    pgHeight = tmp;
                }
                Dimension page = new Dimension(pgWidth, pgHeight);
                Insets margins = new Insets((int)(currentSectionStyle.getMarginTop()),
                        (int)(currentSectionStyle.getMarginLeft()),
                        (int)(currentSectionStyle.getMarginBottom()),
                        (int)(currentSectionStyle.getMarginRight()));
                Dimension pic = new Dimension(image.getIconWidth(), image.getIconHeight());
                // TODO Correct scaling???
            } else {
                frame.setTextAnchorTypeAttribute("as-char");
                imgWidth = image.getIconWidth();
                imgHeight = image.getIconHeight();
            }

            frame.setSvgWidthAttribute(""+DistanceUnit.IN.fromPoints(imgWidth)+"in");
            frame.setSvgHeightAttribute("" + DistanceUnit.IN.fromPoints(imgHeight) + "in");

            OdfDrawImage imageElement = (OdfDrawImage)frame.newDrawImageElement();
            byte[] bytes = imageToBytes(image, "png", Color.white, imgWidth, imgHeight);
            imageCounter++;
            imageElement.newImage(new ByteArrayInputStream(bytes),"Pictures/image"+imageCounter+".png", "image/png");

            currentParagraph.getOdfElement().appendChild(frame);

        } catch (Exception e) {
            throw new DocumentExportException("Could not write image", e);
        }
    }

    /**
     * TODO Move to utility class?
     * Extract the content data (bytes) from the given image.
     * @param icon image to get bytes for
     * @param format desired format for image
     * @param background Color to use as background if transparency in image
     * @param width Desired width of the image in the byte array
     * @param height Desired width of the image in the byte array
     * @return the bytes of the image
     * @throws IOException
     */
    private byte[] imageToBytes(ImageIcon icon, String format, Color background, int width, int height) throws IOException {
        Iterator writers = ImageIO.getImageWritersByFormatName(format);
        ImageWriter writer = (ImageWriter)writers.next();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        writer.setOutput(ios);
        BufferedImage img;
        if("png".equalsIgnoreCase(format) || "gif".equalsIgnoreCase(format)){
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        WaitingObserver observer = new WaitingObserver();
        if(!img.getGraphics().drawImage(icon.getImage(), 0, 0, width,height, background, observer)) {
            try {
                observer.waitForDone();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return new byte[0];
            }
        }
        IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(img), writer.getDefaultWriteParam());
        if(format.equalsIgnoreCase("jpg")) {
            Element tree = (Element)metadata.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", Integer.toString(72));
            jfif.setAttribute("Ydensity", Integer.toString(72));
            jfif.setAttribute("resUnits", "1");
            metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
        } else {
            double dotsPerMilli = 7.2 / 2.54;
            IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
            horiz.setAttribute("value", Double.toString(dotsPerMilli));
            IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
            vert.setAttribute("value", Double.toString(dotsPerMilli));
            IIOMetadataNode dim = new IIOMetadataNode("Dimension");
            dim.appendChild(horiz);
            dim.appendChild(vert);
            IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
            root.appendChild(dim);
            metadata.mergeTree("javax_imageio_1.0", root);
        }

        writer.write(null, new IIOImage(img,null,metadata),null);
        return output.toByteArray();
    }

    private static class ODFStyleWrapper {
        private Style style;
        private String name;
        private OdfStyle odfStyle;

        public void applyToStyle() {
            if (style.isBold()) {
                odfStyle.setProperty(OdfTextProperties.FontWeight, "bold");
            }
            if (style.isItalic()) {
                odfStyle.setProperty(OdfTextProperties.FontStyle, "italic");
            }
            if (style.isUnderline()) {
                odfStyle.setProperty(OdfTextProperties.TextUnderlineStyle, "solid");
                odfStyle.setProperty(OdfTextProperties.TextUnderlineWidth, "auto");
                odfStyle.setProperty(OdfTextProperties.TextUnderlineColor, "font-color");

            }
            if (StringUtils.isNotEmpty(style.getFontName())) {
                odfStyle.setProperty(OdfTextProperties.FontFamily, style.getFontName());
            }
            if (style.getFontSize() != null && style.getFontSize() > 0) {
                odfStyle.setProperty(OdfTextProperties.FontSize, "" + style.getFontSize());
            }
            if (style.getColor() != null) {
                Color color = new Color(style.getColor());
                odfStyle.setProperty(OdfTextProperties.Color, "#" + rgbHexValue(color));
            }
        }
    }

    private static class WaitingObserver implements ImageObserver {

        private final CountDownLatch latch = new CountDownLatch(1);

        public void waitForDone() throws InterruptedException {
            synchronized (latch) {
                latch.wait();
            }
        }

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if ((infoflags & (ALLBITS)) != 0) {
                latch.countDown();
            }
            return (infoflags & (ALLBITS|ABORT)) == 0;
        }
    }
}
