package org.little.util.string;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;
//import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String LF = "\n";
	public static final String CR = "\r";
	public static final char _LF = 10;
	public static final char _CR = 13;
	public static final char _NUL = 0;
	public static final int INDEX_NOT_FOUND = -1;

	public StringUtils() {
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}

	public static boolean isBlank(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0)
			return true;
		for (int i = 0; i < strLen; i++)
			if (!Character.isWhitespace(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	public static String trim(String str) {
		return str != null ? str.trim() : null;
	}

	public static String trimToNull(String str) {
		String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	public static String trimToEmpty(String str) {
		return str != null ? str.trim() : "";
	}

	public static String truncate(String str, int maxWidth) {
		return truncate(str, 0, maxWidth);
	}

	public static String truncate(String str, int offset, int maxWidth) {
		if (offset < 0)
			throw new IllegalArgumentException("offset cannot be negative");
		if (maxWidth < 0)
			throw new IllegalArgumentException("maxWith cannot be negative");
		if (str == null)
			return null;
		if (offset > str.length())
			return "";
		if (str.length() > maxWidth) {
			int ix = offset + maxWidth <= str.length() ? offset + maxWidth : str.length();
			return str.substring(offset, ix);
		} else {
			return str.substring(offset);
		}
	}

	public static String strip(String str) {
		return strip(str, null);
	}

	public static String stripToNull(String str) {
		if (str == null) {
			return null;
		} else {
			str = strip(str, null);
			return str.isEmpty() ? null : str;
		}
	}

	public static String stripToEmpty(String str) {
		return str != null ? strip(str, null) : "";
	}

	public static String strip(String str, String stripChars) {
		if (isEmpty(str)) {
			return str;
		} else {
			str = stripStart(str, stripChars);
			return stripEnd(str, stripChars);
		}
	}

	public static String stripStart(String str, String stripChars) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return str;
		int start = 0;
		if (stripChars == null) {
			for (; start != strLen && Character.isWhitespace(str.charAt(start)); start++)
				;
		} else {
			if (stripChars.isEmpty())
				return str;
			for (; start != strLen && stripChars.indexOf(str.charAt(start)) != -1; start++)
				;
		}
		return str.substring(start);
	}

	public static String stripEnd(String str, String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0)
			return str;
		if (stripChars == null) {
			for (; end != 0 && Character.isWhitespace(str.charAt(end - 1)); end--)
				;
		} else {
			if (stripChars.isEmpty())
				return str;
			for (; end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1; end--)
				;
		}
		return str.substring(0, end);
	}

	public static String[] stripAll(String strs[]) {
		return stripAll(strs, null);
	}

	public static String[] stripAll(String strs[], String stripChars) {
		int strsLen;
		if (strs == null || (strsLen = strs.length) == 0)
			return strs;
		String newArr[] = new String[strsLen];
		for (int i = 0; i < strsLen; i++)
			newArr[i] = strip(strs[i], stripChars);

		return newArr;
	}

	public static String stripAccents(String input) {
		if (input == null) {
			return null;
		} else {
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, java.text.Normalizer.Form.NFD));
			convertRemainingAccentCharacters(decomposed);
			return pattern.matcher(decomposed).replaceAll("");
		}
	}

	private static void convertRemainingAccentCharacters(StringBuilder decomposed) {
		for (int i = 0; i < decomposed.length(); i++)
			if (decomposed.charAt(i) == '\u0141') {
				decomposed.deleteCharAt(i);
				decomposed.insert(i, 'L');
			} else if (decomposed.charAt(i) == '\u0142') {
				decomposed.deleteCharAt(i);
				decomposed.insert(i, 'l');
			}

	}

	public static boolean equals(CharSequence cs1, CharSequence cs2) {
		if (cs1 == cs2)
			return true;
		if (cs1 == null || cs2 == null)
			return false;
		if (cs1.length() != cs2.length())
			return false;
		if ((cs1 instanceof String) && (cs2 instanceof String))
			return cs1.equals(cs2);
		int length = cs1.length();
		for (int i = 0; i < length; i++)
			if (cs1.charAt(i) != cs2.charAt(i))
				return false;

		return true;
	}

	public static int compare(String str1, String str2) {
		return compare(str1, str2, true);
	}

	public static int compare(String str1, String str2, boolean nullIsLess) {
		if (str1 == str2)
			return 0;
		if (str1 == null)
			return nullIsLess ? -1 : 1;
		if (str2 == null)
			return nullIsLess ? 1 : -1;
		else
			return str1.compareTo(str2);
	}

	public static int compareIgnoreCase(String str1, String str2) {
		return compareIgnoreCase(str1, str2, true);
	}

	public static int compareIgnoreCase(String str1, String str2, boolean nullIsLess) {
		if (str1 == str2)
			return 0;
		if (str1 == null)
			return nullIsLess ? -1 : 1;
		if (str2 == null)
			return nullIsLess ? 1 : -1;
		else
			return str1.compareToIgnoreCase(str2);
	}

	public static boolean containsWhitespace(CharSequence seq) {
		if (isEmpty(seq))
			return false;
		int strLen = seq.length();
		for (int i = 0; i < strLen; i++)
			if (Character.isWhitespace(seq.charAt(i)))
				return true;

		return false;
	}

	public static boolean containsNone(CharSequence cs, char searchChars[]) {
		if (cs == null || searchChars == null)
			return true;
		int csLen = cs.length();
		int csLast = csLen - 1;
		int searchLen = searchChars.length;
		int searchLast = searchLen - 1;
		for (int i = 0; i < csLen; i++) {
			char ch = cs.charAt(i);
			for (int j = 0; j < searchLen; j++)
				if (searchChars[j] == ch)
					if (Character.isHighSurrogate(ch)) {
						if (j == searchLast)
							return false;
						if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1))
							return false;
					} else {
						return false;
					}

		}

		return true;
	}

	public static boolean containsNone(CharSequence cs, String invalidChars) {
		if (cs == null || invalidChars == null)
			return true;
		else
			return containsNone(cs, invalidChars.toCharArray());
	}

	public static String substring(String str, int start) {
		if (str == null)
			return null;
		if (start < 0)
			start = str.length() + start;
		if (start < 0)
			start = 0;
		if (start > str.length())
			return "";
		else
			return str.substring(start);
	}

	public static String substring(String str, int start, int end) {
		if (str == null)
			return null;
		if (end < 0)
			end = str.length() + end;
		if (start < 0)
			start = str.length() + start;
		if (end > str.length())
			end = str.length();
		if (start > end)
			return "";
		if (start < 0)
			start = 0;
		if (end < 0)
			end = 0;
		return str.substring(start, end);
	}

	public static String left(String str, int len) {
		if (str == null)
			return null;
		if (len < 0)
			return "";
		if (str.length() <= len)
			return str;
		else
			return str.substring(0, len);
	}

	public static String right(String str, int len) {
		if (str == null)
			return null;
		if (len < 0)
			return "";
		if (str.length() <= len)
			return str;
		else
			return str.substring(str.length() - len);
	}

	public static String mid(String str, int pos, int len) {
		if (str == null)
			return null;
		if (len < 0 || pos > str.length())
			return "";
		if (pos < 0)
			pos = 0;
		if (str.length() <= pos + len)
			return str.substring(pos);
		else
			return str.substring(pos, pos + len);
	}
	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null)
			return str;
		if (separator.isEmpty())
			return "";
		int pos = str.indexOf(separator);
		if (pos == -1)
			return str;
		else
			return str.substring(0, pos);
	}

	public static String substringAfter(String str, String separator) {
		if (isEmpty(str))
			return str;
		if (separator == null)
			return "";
		int pos = str.indexOf(separator);
		if (pos == -1)
			return "";
		else
			return str.substring(pos + separator.length());
	}

	public static String substringBeforeLast(String str, String separator) {
		if (isEmpty(str) || isEmpty(separator))
			return str;
		int pos = str.lastIndexOf(separator);
		if (pos == -1)
			return str;
		else
			return str.substring(0, pos);
	}

	public static String substringAfterLast(String str, String separator) {
		if (isEmpty(str))
			return str;
		if (isEmpty(separator))
			return "";
		int pos = str.lastIndexOf(separator);
		if (pos == -1 || pos == str.length() - separator.length())
			return "";
		else
			return str.substring(pos + separator.length());
	}

	public static String substringBetween(String str, String tag) {
		return substringBetween(str, tag, tag);
	}

	public static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null)
			return null;
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1)
				return str.substring(start + open.length(), end);
		}
		return null;
	}
	public static String deleteWhitespace(String str) {
		if (isEmpty(str))
			return str;
		int sz = str.length();
		char chs[] = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++)
			if (!Character.isWhitespace(str.charAt(i)))
				chs[count++] = str.charAt(i);

		if (count == sz)
			return str;
		else
			return new String(chs, 0, count);
	}

	public static String removeStart(String str, String remove) {
		if (isEmpty(str) || isEmpty(remove))
			return str;
		if (str.startsWith(remove))
			return str.substring(remove.length());
		else
			return str;
	}

	public static String removeEnd(String str, String remove) {
		if (isEmpty(str) || isEmpty(remove))
			return str;
		if (str.endsWith(remove))
			return str.substring(0, str.length() - remove.length());
		else
			return str;
	}

	public static String remove(String str, String remove) {
		if (isEmpty(str) || isEmpty(remove))
			return str;
		else
			return replace(str, remove, "", -1);
	}

	public static String removeIgnoreCase(String str, String remove) {
		if (isEmpty(str) || isEmpty(remove))
			return str;
		else
			return replaceIgnoreCase(str, remove, "", -1);
	}

	public static String remove(String str, char remove) {
		if (isEmpty(str) || str.indexOf(remove) == -1)
			return str;
		char chars[] = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++)
			if (chars[i] != remove)
				chars[pos++] = chars[i];

		return new String(chars, 0, pos);
	}

	public static String replaceOnce(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, 1);
	}

	public static String replaceOnceIgnoreCase(String text, String searchString, String replacement) {
		return replaceIgnoreCase(text, searchString, replacement, 1);
	}

	public static String replace(String text, String searchString, String replacement) {
		return replace(text, searchString, replacement, -1);
	}

	public static String replaceIgnoreCase(String text, String searchString, String replacement) {
		return replaceIgnoreCase(text, searchString, replacement, -1);
	}

	public static String replace(String text, String searchString, String replacement, int max) {
		return replace(text, searchString, replacement, max, false);
	}

	private static String replace(String text, String searchString, String replacement, int max, boolean ignoreCase) {
		if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0)
			return text;
		String searchText = text;
		if (ignoreCase) {
			searchText = text.toLowerCase();
			searchString = searchString.toLowerCase();
		}
		int start = 0;
		int end = searchText.indexOf(searchString, start);
		if (end == -1)
			return text;
		int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase >= 0 ? increase : 0;
		increase *= max >= 0 ? max <= 64 ? max : 64 : 16;
		StringBuilder buf = new StringBuilder(text.length() + increase);
		for (; end != -1; end = searchText.indexOf(searchString, start)) {
			buf.append(text, start, end).append(replacement);
			start = end + replLength;
			if (--max == 0)
				break;
		}

		buf.append(text, start, text.length());
		return buf.toString();
	}

	public static String replaceIgnoreCase(String text, String searchString, String replacement, int max) {
		return replace(text, searchString, replacement, max, true);
	}

	public static String replaceEach(String text, String searchList[], String replacementList[]) {
		return replaceEach(text, searchList, replacementList, false, 0);
	}

	public static String replaceEachRepeatedly(String text, String searchList[], String replacementList[]) {
		int timeToLive = searchList != null ? searchList.length : 0;
		return replaceEach(text, searchList, replacementList, true, timeToLive);
	}

	private static String replaceEach(String text, String searchList[], String replacementList[], boolean repeat,
			int timeToLive) {
		if (text == null || text.isEmpty() || searchList == null || searchList.length == 0 || replacementList == null
				|| replacementList.length == 0)
			return text;
		if (timeToLive < 0)
			throw new IllegalStateException(
					"Aborting to protect against StackOverflowError - output of one loop is the input of another");
		int searchLength = searchList.length;
		int replacementLength = replacementList.length;
		if (searchLength != replacementLength)
			throw new IllegalArgumentException((new StringBuilder("Search and Replace array lengths don't match: "))
					.append(searchLength).append(" vs ").append(replacementLength).toString());
		boolean noMoreMatchesForReplIndex[] = new boolean[searchLength];
		int textIndex = -1;
		int replaceIndex = -1;
		int tempIndex = -1;
		for (int i = 0; i < searchLength; i++)
			if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && !searchList[i].isEmpty()
					&& replacementList[i] != null) {
				tempIndex = text.indexOf(searchList[i]);
				if (tempIndex == -1)
					noMoreMatchesForReplIndex[i] = true;
				else if (textIndex == -1 || tempIndex < textIndex) {
					textIndex = tempIndex;
					replaceIndex = i;
				}
			}

		if (textIndex == -1)
			return text;
		int start = 0;
		int increase = 0;
		for (int i = 0; i < searchList.length; i++)
			if (searchList[i] != null && replacementList[i] != null) {
				int greater = replacementList[i].length() - searchList[i].length();
				if (greater > 0)
					increase += 3 * greater;
			}

		increase = Math.min(increase, text.length() / 5);
		StringBuilder buf = new StringBuilder(text.length() + increase);
		while (textIndex != -1) {
			for (int i = start; i < textIndex; i++)
				buf.append(text.charAt(i));

			buf.append(replacementList[replaceIndex]);
			start = textIndex + searchList[replaceIndex].length();
			textIndex = -1;
			replaceIndex = -1;
			tempIndex = -1;
			for (int i = 0; i < searchLength; i++)
				if (!noMoreMatchesForReplIndex[i] && searchList[i] != null && !searchList[i].isEmpty()
						&& replacementList[i] != null) {
					tempIndex = text.indexOf(searchList[i], start);
					if (tempIndex == -1)
						noMoreMatchesForReplIndex[i] = true;
					else if (textIndex == -1 || tempIndex < textIndex) {
						textIndex = tempIndex;
						replaceIndex = i;
					}
				}

		}
		int textLength = text.length();
		for (int i = start; i < textLength; i++)
			buf.append(text.charAt(i));

		String result = buf.toString();
		if (!repeat)
			return result;
		else
			return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
	}

	public static String replaceChars(String str, char searchChar, char replaceChar) {
		if (str == null)
			return null;
		else
			return str.replace(searchChar, replaceChar);
	}

	public static String replaceChars(String str, String searchChars, String replaceChars) {
		if (isEmpty(str) || isEmpty(searchChars))
			return str;
		if (replaceChars == null)
			replaceChars = "";
		boolean modified = false;
		int replaceCharsLength = replaceChars.length();
		int strLength = str.length();
		StringBuilder buf = new StringBuilder(strLength);
		for (int i = 0; i < strLength; i++) {
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0) {
				modified = true;
				if (index < replaceCharsLength)
					buf.append(replaceChars.charAt(index));
			} else {
				buf.append(ch);
			}
		}

		if (modified)
			return buf.toString();
		else
			return str;
	}

	public static String overlay(String str, String overlay, int start, int end) {
		if (str == null)
			return null;
		if (overlay == null)
			overlay = "";
		int len = str.length();
		if (start < 0)
			start = 0;
		if (start > len)
			start = len;
		if (end < 0)
			end = 0;
		if (end > len)
			end = len;
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		return (new StringBuilder(String.valueOf(str.substring(0, start)))).append(overlay).append(str.substring(end))
				.toString();
	}

	public static String chomp(String str) {
		if (isEmpty(str))
			return str;
		if (str.length() == 1) {
			char ch = str.charAt(0);
			if (ch == '\r' || ch == '\n')
				return "";
			else
				return str;
		}
		int lastIdx = str.length() - 1;
		char last = str.charAt(lastIdx);
		if (last == '\n') {
			if (str.charAt(lastIdx - 1) == '\r')
				lastIdx--;
		} else if (last != '\r')
			lastIdx++;
		return str.substring(0, lastIdx);
	}

	/**
	 * @deprecated Method chomp is deprecated
	 */

	public static String chomp(String str, String separator) {
		return removeEnd(str, separator);
	}

	public static String chop(String str) {
		if (str == null)
			return null;
		int strLen = str.length();
		if (strLen < 2)
			return "";
		int lastIdx = strLen - 1;
		String ret = str.substring(0, lastIdx);
		char last = str.charAt(lastIdx);
		if (last == '\n' && ret.charAt(lastIdx - 1) == '\r')
			return ret.substring(0, lastIdx - 1);
		else
			return ret;
	}

	public static String repeat(String str, int repeat) {
		if (str == null)
			return null;
		if (repeat <= 0)
			return "";
		int inputLength = str.length();
		if (repeat == 1 || inputLength == 0)
			return str;
		if (inputLength == 1 && repeat <= 8192)
			return repeat(str.charAt(0), repeat);
		int outputLength = inputLength * repeat;
		switch (inputLength) {
		case 1: // '\001'
			return repeat(str.charAt(0), repeat);

		case 2: // '\002'
			char ch0 = str.charAt(0);
			char ch1 = str.charAt(1);
			char output2[] = new char[outputLength];
			for (int i = repeat * 2 - 2; i >= 0; i--) {
				output2[i] = ch0;
				output2[i + 1] = ch1;
				i--;
			}

			return new String(output2);
		}
		StringBuilder buf = new StringBuilder(outputLength);
		for (int i = 0; i < repeat; i++)
			buf.append(str);

		return buf.toString();
	}

	public static String repeat(String str, String separator, int repeat) {
		if (str == null || separator == null) {
			return repeat(str, repeat);
		} else {
			String result = repeat((new StringBuilder(String.valueOf(str))).append(separator).toString(), repeat);
			return removeEnd(result, separator);
		}
	}

	public static String repeat(char ch, int repeat) {
		if (repeat <= 0)
			return "";
		char buf[] = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--)
			buf[i] = ch;

		return new String(buf);
	}

	public static String rightPad(String str, int size) {
		return rightPad(str, size, ' ');
	}

	public static String rightPad(String str, int size, char padChar) {
		if (str == null)
			return null;
		int pads = size - str.length();
		if (pads <= 0)
			return str;
		if (pads > 8192)
			return rightPad(str, size, String.valueOf(padChar));
		else
			return str.concat(repeat(padChar, pads));
	}

	public static String rightPad(String str, int size, String padStr) {
		if (str == null)
			return null;
		if (isEmpty(padStr))
			padStr = " ";
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
			return str;
		if (padLen == 1 && pads <= 8192)
			return rightPad(str, size, padStr.charAt(0));
		if (pads == padLen)
			return str.concat(padStr);
		if (pads < padLen)
			return str.concat(padStr.substring(0, pads));
		char padding[] = new char[pads];
		char padChars[] = padStr.toCharArray();
		for (int i = 0; i < pads; i++)
			padding[i] = padChars[i % padLen];

		return str.concat(new String(padding));
	}

	public static String leftPad(String str, int size) {
		return leftPad(str, size, ' ');
	}

	public static String leftPad(String str, int size, char padChar) {
		if (str == null)
			return null;
		int pads = size - str.length();
		if (pads <= 0)
			return str;
		if (pads > 8192)
			return leftPad(str, size, String.valueOf(padChar));
		else
			return repeat(padChar, pads).concat(str);
	}

	public static String leftPad(String str, int size, String padStr) {
		if (str == null)
			return null;
		if (isEmpty(padStr))
			padStr = " ";
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
			return str;
		if (padLen == 1 && pads <= 8192)
			return leftPad(str, size, padStr.charAt(0));
		if (pads == padLen)
			return padStr.concat(str);
		if (pads < padLen)
			return padStr.substring(0, pads).concat(str);
		char padding[] = new char[pads];
		char padChars[] = padStr.toCharArray();
		for (int i = 0; i < pads; i++)
			padding[i] = padChars[i % padLen];

		return (new String(padding)).concat(str);
	}

	public static int length(CharSequence cs) {
		return cs != null ? cs.length() : 0;
	}

	public static String center(String str, int size) {
		return center(str, size, ' ');
	}

	public static String center(String str, int size, char padChar) {
		if (str == null || size <= 0)
			return str;
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		} else {
			str = leftPad(str, strLen + pads / 2, padChar);
			str = rightPad(str, size, padChar);
			return str;
		}
	}

	public static String center(String str, int size, String padStr) {
		if (str == null || size <= 0)
			return str;
		if (isEmpty(padStr))
			padStr = " ";
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0) {
			return str;
		} else {
			str = leftPad(str, strLen + pads / 2, padStr);
			str = rightPad(str, size, padStr);
			return str;
		}
	}

	public static String upperCase(String str) {
		if (str == null)
			return null;
		else
			return str.toUpperCase();
	}

	public static String upperCase(String str, Locale locale) {
		if (str == null)
			return null;
		else
			return str.toUpperCase(locale);
	}

	public static String lowerCase(String str) {
		if (str == null)
			return null;
		else
			return str.toLowerCase();
	}

	public static String lowerCase(String str, Locale locale) {
		if (str == null)
			return null;
		else
			return str.toLowerCase(locale);
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return str;
		int firstCodepoint = str.codePointAt(0);
		int newCodePoint = Character.toTitleCase(firstCodepoint);
		if (firstCodepoint == newCodePoint)
			return str;
		int newCodePoints[] = new int[strLen];
		int outOffset = 0;
		newCodePoints[outOffset++] = newCodePoint;
		int codepoint;
		for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; inOffset += Character
				.charCount(codepoint)) {
			codepoint = str.codePointAt(inOffset);
			newCodePoints[outOffset++] = codepoint;
		}

		return new String(newCodePoints, 0, outOffset);
	}

	public static String uncapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0)
			return str;
		int firstCodepoint = str.codePointAt(0);
		int newCodePoint = Character.toLowerCase(firstCodepoint);
		if (firstCodepoint == newCodePoint)
			return str;
		int newCodePoints[] = new int[strLen];
		int outOffset = 0;
		newCodePoints[outOffset++] = newCodePoint;
		int codepoint;
		for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; inOffset += Character
				.charCount(codepoint)) {
			codepoint = str.codePointAt(inOffset);
			newCodePoints[outOffset++] = codepoint;
		}

		return new String(newCodePoints, 0, outOffset);
	}

	public static String swapCase(String str) {
		if (isEmpty(str))
			return str;
		int strLen = str.length();
		int newCodePoints[] = new int[strLen];
		int outOffset = 0;
		int newCodePoint;
		for (int i = 0; i < strLen; i += Character.charCount(newCodePoint)) {
			int oldCodepoint = str.codePointAt(i);
			if (Character.isUpperCase(oldCodepoint))
				newCodePoint = Character.toLowerCase(oldCodepoint);
			else if (Character.isTitleCase(oldCodepoint))
				newCodePoint = Character.toLowerCase(oldCodepoint);
			else if (Character.isLowerCase(oldCodepoint))
				newCodePoint = Character.toUpperCase(oldCodepoint);
			else
				newCodePoint = oldCodepoint;
			newCodePoints[outOffset++] = newCodePoint;
		}

		return new String(newCodePoints, 0, outOffset);
	}

	public static int countMatches(CharSequence str, char ch) {
		if (isEmpty(str))
			return 0;
		int count = 0;
		for (int i = 0; i < str.length(); i++)
			if (ch == str.charAt(i))
				count++;

		return count;
	}

	public static boolean isAlpha(CharSequence cs) {
		if (isEmpty(cs))
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isLetter(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isAlphaSpace(CharSequence cs) {
		if (cs == null)
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isLetter(cs.charAt(i)) && cs.charAt(i) != ' ')
				return false;

		return true;
	}

	public static boolean isAlphanumeric(CharSequence cs) {
		if (isEmpty(cs))
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isLetterOrDigit(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isAlphanumericSpace(CharSequence cs) {
		if (cs == null)
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isLetterOrDigit(cs.charAt(i)) && cs.charAt(i) != ' ')
				return false;

		return true;
	}

	public static boolean isNumeric(CharSequence cs) {
		if (isEmpty(cs))
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isDigit(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isNumericSpace(CharSequence cs) {
		if (cs == null)
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != ' ')
				return false;

		return true;
	}

	public static String getDigits(String str) {
		if (isEmpty(str))
			return str;
		int sz = str.length();
		StringBuilder strDigits = new StringBuilder(sz);
		for (int i = 0; i < sz; i++) {
			char tempChar = str.charAt(i);
			if (Character.isDigit(tempChar))
				strDigits.append(tempChar);
		}

		return strDigits.toString();
	}

	public static boolean isWhitespace(CharSequence cs) {
		if (cs == null)
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isWhitespace(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isAllLowerCase(CharSequence cs) {
		if (cs == null || isEmpty(cs))
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isLowerCase(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isAllUpperCase(CharSequence cs) {
		if (cs == null || isEmpty(cs))
			return false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++)
			if (!Character.isUpperCase(cs.charAt(i)))
				return false;

		return true;
	}

	public static boolean isMixedCase(CharSequence cs) {
		if (isEmpty(cs) || cs.length() == 1)
			return false;
		boolean containsUppercase = false;
		boolean containsLowercase = false;
		int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (containsUppercase && containsLowercase)
				return true;
			if (Character.isUpperCase(cs.charAt(i)))
				containsUppercase = true;
			else if (Character.isLowerCase(cs.charAt(i)))
				containsLowercase = true;
		}

		return containsUppercase && containsLowercase;
	}

	public static String defaultString(String str) {
		return defaultString(str, "");
	}

	public static String defaultString(String str, String defaultStr) {
		return str != null ? str : defaultStr;
	}

	public static CharSequence firstNonBlank(CharSequence values[]) {
		if (values != null) {
			CharSequence acharsequence[];
			int j = (acharsequence = values).length;
			for (int i = 0; i < j; i++) {
				CharSequence val = acharsequence[i];
				if (isNotBlank(val))
					return val;
			}

		}
		return null;
	}

	public static CharSequence firstNonEmpty(CharSequence values[]) {
		if (values != null) {
			CharSequence acharsequence[];
			int j = (acharsequence = values).length;
			for (int i = 0; i < j; i++) {
				CharSequence val = acharsequence[i];
				if (isNotEmpty(val))
					return val;
			}

		}
		return null;
	}

	public static CharSequence defaultIfBlank(CharSequence str, CharSequence defaultStr) {
		return isBlank(str) ? defaultStr : str;
	}

	public static CharSequence defaultIfEmpty(CharSequence str, CharSequence defaultStr) {
		return isEmpty(str) ? defaultStr : str;
	}

	public static String rotate(String str, int shift) {
		if (str == null)
			return null;
		int strLen = str.length();
		if (shift == 0 || strLen == 0 || shift % strLen == 0) {
			return str;
		} else {
			StringBuilder builder = new StringBuilder(strLen);
			int offset = -(shift % strLen);
			builder.append(substring(str, offset));
			builder.append(substring(str, 0, offset));
			return builder.toString();
		}
	}

	public static String reverse(String str) {
		if (str == null)
			return null;
		else
			return (new StringBuilder(str)).reverse().toString();
	}
	public static String difference(String str1, String str2) {
		if (str1 == null)
			return str2;
		if (str2 == null)
			return str1;
		int at = indexOfDifference(str1, str2);
		if (at == -1)
			return "";
		else
			return str2.substring(at);
	}

	public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
		if (cs1 == cs2)
			return -1;
		if (cs1 == null || cs2 == null)
			return 0;
		int i;
		for (i = 0; i < cs1.length() && i < cs2.length(); i++)
			if (cs1.charAt(i) != cs2.charAt(i))
				break;

		if (i < cs2.length() || i < cs1.length())
			return i;
		else
			return -1;
	}

	public static int indexOfDifference(CharSequence css[]) {
		if (css == null || css.length <= 1)
			return -1;
		boolean anyStringNull = false;
		boolean allStringsNull = true;
		int arrayLen = css.length;
		int shortestStrLen = 0x7fffffff;
		int longestStrLen = 0;
		CharSequence acharsequence[];
		int j = (acharsequence = css).length;
		for (int i = 0; i < j; i++) {
			CharSequence cs = acharsequence[i];
			if (cs == null) {
				anyStringNull = true;
				shortestStrLen = 0;
			} else {
				allStringsNull = false;
				shortestStrLen = Math.min(cs.length(), shortestStrLen);
				longestStrLen = Math.max(cs.length(), longestStrLen);
			}
		}

		if (allStringsNull || longestStrLen == 0 && !anyStringNull)
			return -1;
		if (shortestStrLen == 0)
			return 0;
		int firstDiff = -1;
		for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
			char comparisonChar = css[0].charAt(stringPos);
			for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
				if (css[arrayPos].charAt(stringPos) == comparisonChar)
					continue;
				firstDiff = stringPos;
				break;
			}

			if (firstDiff != -1)
				break;
		}

		if (firstDiff == -1 && shortestStrLen != longestStrLen)
			return shortestStrLen;
		else
			return firstDiff;
	}

	public static String getCommonPrefix(String strs[]) {
		if (strs == null || strs.length == 0)
			return "";
		int smallestIndexOfDiff = indexOfDifference(strs);
		if (smallestIndexOfDiff == -1)
			if (strs[0] == null)
				return "";
			else
				return strs[0];
		if (smallestIndexOfDiff == 0)
			return "";
		else
			return strs[0].substring(0, smallestIndexOfDiff);
	}

	/**
	 * @deprecated Method getLevenshteinDistance is deprecated
	 */

	public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
		if (s == null || t == null)
			throw new IllegalArgumentException("Strings must not be null");
		int n = s.length();
		int m = t.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		if (n > m) {
			CharSequence tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}
		int p[] = new int[n + 1];
		for (int i = 0; i <= n; i++)
			p[i] = i;

		for (int j = 1; j <= m; j++) {
			int upper_left = p[0];
			char t_j = t.charAt(j - 1);
			p[0] = j;
			for (int i = 1; i <= n; i++) {
				int upper = p[i];
				int cost = s.charAt(i - 1) != t_j ? 1 : 0;
				p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
				upper_left = upper;
			}

		}

		return p[n];
	}

	/**
	 * @deprecated Method getLevenshteinDistance is deprecated
	 */

	public static int getLevenshteinDistance(CharSequence s, CharSequence t, int threshold) {
		if (s == null || t == null)
			throw new IllegalArgumentException("Strings must not be null");
		if (threshold < 0)
			throw new IllegalArgumentException("Threshold must not be negative");
		int n = s.length();
		int m = t.length();
		if (n == 0)
			return m > threshold ? -1 : m;
		if (m == 0)
			return n > threshold ? -1 : n;
		if (Math.abs(n - m) > threshold)
			return -1;
		if (n > m) {
			CharSequence tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length();
		}
		int p[] = new int[n + 1];
		int d[] = new int[n + 1];
		int boundary = Math.min(n, threshold) + 1;
		for (int i = 0; i < boundary; i++)
			p[i] = i;

		Arrays.fill(p, boundary, p.length, 0x7fffffff);
		Arrays.fill(d, 0x7fffffff);
		for (int j = 1; j <= m; j++) {
			char t_j = t.charAt(j - 1);
			d[0] = j;
			int min = Math.max(1, j - threshold);
			int max = j <= 0x7fffffff - threshold ? Math.min(n, j + threshold) : n;
			if (min > max)
				return -1;
			if (min > 1)
				d[min - 1] = 0x7fffffff;
			for (int i = min; i <= max; i++)
				if (s.charAt(i - 1) == t_j)
					d[i] = p[i - 1];
				else
					d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);

			int _d[] = p;
			p = d;
			d = _d;
		}

		if (p[n] <= threshold)
			return p[n];
		else
			return -1;
	}

/*
	private static int[] matches(CharSequence first, CharSequence second) {
		CharSequence max;
		CharSequence min;
		if (first.length() > second.length()) {
			max = first;
			min = second;
		} else {
			max = second;
			min = first;
		}
		int range = Math.max(max.length() / 2 - 1, 0);
		int matchIndexes[] = new int[min.length()];
		Arrays.fill(matchIndexes, -1);
		boolean matchFlags[] = new boolean[max.length()];
		int matches = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			char c1 = min.charAt(mi);
			int xi = Math.max(mi - range, 0);
			for (int xn = Math.min(mi + range + 1, max.length()); xi < xn; xi++) {
				if (matchFlags[xi] || c1 != max.charAt(xi))
					continue;
				matchIndexes[mi] = xi;
				matchFlags[xi] = true;
				matches++;
				break;
			}

		}

		char ms1[] = new char[matches];
		char ms2[] = new char[matches];
		int i = 0;
		int si = 0;
		for (; i < min.length(); i++)
			if (matchIndexes[i] != -1) {
				ms1[si] = min.charAt(i);
				si++;
			}

		i = 0;
		si = 0;
		for (; i < max.length(); i++)
			if (matchFlags[i]) {
				ms2[si] = max.charAt(i);
				si++;
			}

		int transpositions = 0;
		for (int mi = 0; mi < ms1.length; mi++)
			if (ms1[mi] != ms2[mi])
				transpositions++;

		int prefix = 0;
		for (int mi = 0; mi < min.length(); mi++) {
			if (first.charAt(mi) != second.charAt(mi))
				break;
			prefix++;
		}

		return (new int[] { matches, transpositions / 2, prefix, max.length() });
	}

*/
	public static String normalizeSpace(String str) {
		if (isEmpty(str))
			return str;
		int size = str.length();
		char newChars[] = new char[size];
		int count = 0;
		int whitespacesCount = 0;
		boolean startWhitespaces = true;
		for (int i = 0; i < size; i++) {
			char actualChar = str.charAt(i);
			boolean isWhitespace = Character.isWhitespace(actualChar);
			if (isWhitespace) {
				if (whitespacesCount == 0 && !startWhitespaces)
					newChars[count++] = " ".charAt(0);
				whitespacesCount++;
			} else {
				startWhitespaces = false;
				newChars[count++] = actualChar != '\240' ? actualChar : ' ';
				whitespacesCount = 0;
			}
		}

		if (startWhitespaces)
			return "";
		else
			return (new String(newChars, 0, count - (whitespacesCount <= 0 ? 0 : 1))).trim();
	}

	public static String toEncodedString(byte bytes[], Charset charset) {
		return new String(bytes, charset == null ? Charset.defaultCharset() : charset);
	}

	public static String wrap(String str, char wrapWith) {
		if (isEmpty(str) || wrapWith == 0)
			return str;
		else
			return (new StringBuilder(String.valueOf(wrapWith))).append(str).append(wrapWith).toString();
	}

	public static String wrap(String str, String wrapWith) {
		if (isEmpty(str) || isEmpty(wrapWith))
			return str;
		else
			return wrapWith.concat(str).concat(wrapWith);
	}

	public static String wrapIfMissing(String str, char wrapWith) {
		if (isEmpty(str) || wrapWith == 0)
			return str;
		StringBuilder builder = new StringBuilder(str.length() + 2);
		if (str.charAt(0) != wrapWith)
			builder.append(wrapWith);
		builder.append(str);
		if (str.charAt(str.length() - 1) != wrapWith)
			builder.append(wrapWith);
		return builder.toString();
	}

	public static String wrapIfMissing(String str, String wrapWith) {
		if (isEmpty(str) || isEmpty(wrapWith))
			return str;
		StringBuilder builder = new StringBuilder(str.length() + wrapWith.length() + wrapWith.length());
		if (!str.startsWith(wrapWith))
			builder.append(wrapWith);
		builder.append(str);
		if (!str.endsWith(wrapWith))
			builder.append(wrapWith);
		return builder.toString();
	}

	public static String unwrap(String str, char wrapChar) {
		if (isEmpty(str) || wrapChar == 0)
			return str;
		if (str.charAt(0) == wrapChar && str.charAt(str.length() - 1) == wrapChar) {
			//int startIndex = 0;
			int endIndex = str.length() - 1;
			if (endIndex != -1)
				return str.substring(1, endIndex);
		}
		return str;
	}

	public static String valueOf(char value[]) {
		return value != null ? String.valueOf(value) : null;
	}

}
