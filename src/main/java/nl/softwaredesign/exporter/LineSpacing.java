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
