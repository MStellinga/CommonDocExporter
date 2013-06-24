package nl.softwaredesign.exporter;

/**
 * Enumeration for types of lists
 */
public enum ListType {
    NONE{
        public String getValueFor(int number) {
            return "";
        }
    },
    BULLET {
        public String getValueFor(int number) {
            return "-";
        }
    },
    NUMBER {
        public String getValueFor(int number) {
            return "" + number;
        }
    },
    LETTER {
        public String getValueFor(int number) {
            StringBuffer buf = new StringBuffer();
            while (number > 0) {
                int t = (number - 1) % 26;
                int a = (int) 'a';
                buf.append((char) (a + t));
                number = number / 26;
            }
            return buf.toString();
        }
    },
    CAPITAL_LETTER {
        public String getValueFor(int number) {
            return LETTER.getValueFor(number).toUpperCase();
        }
    };

    public abstract String getValueFor(int number);

}
