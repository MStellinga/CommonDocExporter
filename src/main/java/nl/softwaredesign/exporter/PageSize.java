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
