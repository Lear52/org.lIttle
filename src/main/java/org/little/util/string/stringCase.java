package org.little.util.string;

public final class stringCase {

    public static final stringCase SENSITIVE   = new stringCase("Sensitive"  , true);
    public static final stringCase INSENSITIVE = new stringCase("Insensitive", false);

    /** The enumeration name. */
    private final String name;
    
    /** The sensitivity flag. */
    private final transient boolean sensitive;

    //-----------------------------------------------------------------------
    public static stringCase forName(String name) {
        if (stringCase.SENSITIVE.name.equals(name)){
            return stringCase.SENSITIVE;
        }
        if (stringCase.INSENSITIVE.name.equals(name)){
            return stringCase.INSENSITIVE;
        }
        throw new IllegalArgumentException("Invalid stringCase name: " + name);
    }

    //-----------------------------------------------------------------------
    /**
     * Private constructor.
     * 
     * @param name  the name
     * @param sensitive  the sensitivity
     */
    private stringCase(String name, boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }

    /**
     * Replaces the enumeration from the stream with a real one.
     * This ensures that the correct flag is set for SYSTEM.
     * 
     * @return the resolved object
     */
    private Object readResolve() {
        return forName(name);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name of the constant.
     * 
     * @return the name of the constant
     */
    public String getName() {
        return name;
    }

    /**
     * Does the object represent case sensitive comparison.
     * 
     * @return true if case sensitive
     */
    public boolean isCaseSensitive() {
        return sensitive;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares two strings using the case-sensitivity rule.
     * <p>
     * This method mimics {@link String#compareTo} but takes case-sensitivity
     * into account.
     * 
     * @param str1  the first string to compare, not null
     * @param str2  the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public int checkCompareTo(String str1, String str2) {
        if (str1 == null || str2 == null) {
            throw new NullPointerException("The strings must not be null");
        }
        return sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
    }

    /**
     * Compares two strings using the case-sensitivity rule.
     * <p>
     * This method mimics {@link String#equals} but takes case-sensitivity
     * into account.
     * 
     * @param str1  the first string to compare, not null
     * @param str2  the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkEquals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            throw new NullPointerException("The strings must not be null");
        }
        return sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
    }

    /**
     * Checks if one string starts with another using the case-sensitivity rule.
     * <p>
     * This method mimics {@link String#startsWith(String)} but takes case-sensitivity
     * into account.
     * 
     * @param str  the string to check, not null
     * @param start  the start to compare against, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkStartsWith(String str, String start) {
        return str.regionMatches(!sensitive, 0, start, 0, start.length());
    }

    /**
     * Checks if one string ends with another using the case-sensitivity rule.
     * <p>
     * This method mimics {@link String#endsWith} but takes case-sensitivity
     * into account.
     * 
     * @param str  the string to check, not null
     * @param end  the end to compare against, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkEndsWith(String str, String end) {
        int endLen = end.length();
        return str.regionMatches(!sensitive, str.length() - endLen, end, 0, endLen);
    }

    /**
     * Checks if one string contains another starting at a specific index using the
     * case-sensitivity rule.
     * <p>
     * This method mimics parts of {@link String#indexOf(String, int)} 
     * but takes case-sensitivity into account.
     * 
     * @param str  the string to check, not null
     * @param strStartIndex  the index to start at in str
     * @param search  the start to search for, not null
     * @return the first index of the search String,
     *  -1 if no match or {@code null} string input
     * @throws NullPointerException if either string is null
     * @since 2.0
     */
    public int checkIndexOf(String str, int strStartIndex, String search) {
        int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; i++) {
                if (checkRegionMatches(str, i, search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Checks if one string contains another at a specific index using the case-sensitivity rule.
     * <p>
     * This method mimics parts of {@link String#regionMatches(boolean, int, String, int, int)} 
     * but takes case-sensitivity into account.
     * 
     * @param str  the string to check, not null
     * @param strStartIndex  the index to start at in str
     * @param search  the start to search for, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkRegionMatches(String str, int strStartIndex, String search) {
        return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a string describing the sensitivity.
     * 
     * @return a string describing the sensitivity
     */
    @Override
    public String toString() {
        return name;
    }

}
