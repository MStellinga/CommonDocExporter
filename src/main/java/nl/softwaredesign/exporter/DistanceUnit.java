package nl.softwaredesign.exporter;

/**
 * A printing distance unit: cm, inch, or point.<br>
 * At 72 DPI (dots per inch): 72 points = 1 inch = 2,54 cm
 */
public enum DistanceUnit {

    PT { // Points

        public float toPoints(float value) {
            return value;
        }

        public float fromPoints(float value) {
            return value;
        }
    },
    IN { // Inch

        public float toPoints(float value) {
            return value * 72.0f;
        }

        public float fromPoints(float value) {
            return value / 72.0f;
        }
    },
    CM { // Cm

        public float toPoints(float value) {
            return value * 72.0f / 2.54f;
        }

        public float fromPoints(float value) {
            return value * 2.54f / 72.0f;
        }
    };


    public abstract float toPoints(float value);

    public abstract float fromPoints(float value);
}
