package nl.softwaredesign.exporter;

/**
 * Multi-page settings:<ul>
 * <li>All pages equal
 * <li>Different first page
 * <li>Different odd and even pages
 * <li>Different first page, odd pages, and even pages
 * </ul>
 */
public enum MultiPageType {

    ALL_EQUAL,
    DIFFERENT_FIRST,
    DIFFERENT_ODD_AND_EVEN,
    DIFFERENT_FIRST_ODD_AND_EVEN;
}
