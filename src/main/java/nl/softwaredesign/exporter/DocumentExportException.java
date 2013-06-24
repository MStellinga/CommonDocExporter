package nl.softwaredesign.exporter;

/**
 * An exception for problems generating a document using an export format
 */
public class DocumentExportException extends Exception {

    public DocumentExportException() {
    }

    public DocumentExportException(String message) {
        super(message);
    }

    public DocumentExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
