package nl.softwaredesign.exporter;

/**
 * Style for use in a document
 */
public class Style {

    private String name;

    private boolean bold;
    private boolean italic;
    private boolean underline;

    private String fontName;
    private Integer fontSize;

    private Integer color = null;

    public Style() {
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public int hashCode() {
        int hashcode = name == null ? 0 : name.hashCode();
        if (bold) {
            hashcode += 3;
        }
        if (italic) {
            hashcode += 5;
        }
        if (underline) {
            hashcode += 7;
        }
        return hashcode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Style)) {
            return false;
        }
        Style other = (Style) obj;
        if (bold && !other.bold) {
            return false;
        }
        if (underline && !other.underline) {
            return false;
        }
        if (italic && !other.italic) {
            return false;
        }
        if(name == null && other.name!=null){
            return false;
        } else if(name != null && other.name==null){
            return false;
        } else if(name == null && other.name==null){
            return true;
        }
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return "Style " + name + "[" + (bold ? "b" : "") + (italic ? "i" : "") + (underline ? "u" : "") + "|" + fontName + "]";
    }
}
