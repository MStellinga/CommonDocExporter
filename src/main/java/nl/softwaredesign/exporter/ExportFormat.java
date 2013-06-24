package nl.softwaredesign.exporter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Implementations of this interface are responsible
 * for exporting data to a specific file format.
 *
 * To create a document, call:
 * <ol>
 * <li>startNewFile
 * <li>startSection (for each new section)
 * <li>newParagraph (for each new paragraph in the section)
 * <li>writeText (for all the text to add to the paragraph)
 * <li>page break (for all page breaks)
 * <li>endSection
 * <li>endFile
 * </ol>
 */
public interface ExportFormat {

    /**
     * Open the file for writing
     *
     * @param targetFile file to write to
     * @param author author of the project
     * @param title title of the project
     * @param language the project language
     */
    void startNewFile(File targetFile, String author,String title, String language) throws IOException;

    /**
     * Close the file
     *
     * @param targetFile stream to write to
     */
    public void endFile(File targetFile, boolean firstPass) throws IOException;

    /**
     * Start a new section of the document
     *
     * @param sectionStyle section settings
     */
    void startSection(SectionStyle sectionStyle) throws DocumentExportException;

    /**
     * End section of the document
     */
    void endSection() throws DocumentExportException;

    /**
     * Write text to output
     *
     * @param text  text to add
     * @param style style to use for the text
     * @param bookmarkId ID of bookmark as return by #addTOCItem
     * @param bookmarkName the text of this bookmark (if any)
     */
    void writeText(String text, Style style, Object bookmarkId, String bookmarkName) throws DocumentExportException;

    /**
     * Start a new page in the exported document
     */
    void writePageBreak() throws DocumentExportException;

    /**
     * Start a new paragraph in the exported document
     *
     * @param indent    0 means no indent for the paragraph, 1 means one level of indent, etc.
     * @param alignment the alignment of the new paragraph
     * @param listType  the type of list (or null) of this paragraph
     * @param firstLineIndent indent size for first line of this paragraph in points
     * @param indentSize indent size for this paragraph in points
     * @param spacing line spacing of this paragraph
     */
    void newParagraph(int indent, Alignment alignment, ListType listType,
                      float firstLineIndent, float indentSize, LineSpacing spacing) throws DocumentExportException;


    /**
     * Add a note as annotation at this point in the text
     * @param title title of the inline note
     * @param body body text of the note
     * @param author the document author
     * @param authorInitials
     */
    void addNote(String title, String body, String author, String authorInitials) throws DocumentExportException;

    /**
     * Add an image to the document
     * @param image image to add
     * @param scaleToPage should the image be scaled to fit the page (constraining proportions)
     */
    void writeImage(ImageIcon image, boolean scaleToPage) throws DocumentExportException;
}
