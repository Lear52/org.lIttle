package org.little.imap.command.cmd.fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.little.util.Logger;
import org.little.util.LoggerFactory;

/**
 * Represents a range of UID values.
 */
public class IdRange {
	private static final Logger logger = LoggerFactory.getLogger(IdRange.class);
	/** Matches a sequence of a single id or id range */
    public static final Pattern SEQUENCE = Pattern.compile("\\d+|\\d+\\:\\d+");
    private long lowVal;
    private long highVal;

    public IdRange(long singleVal) {lowVal = singleVal;  highVal = singleVal;}
    public IdRange(long lowVal, long highVal) { this.lowVal = lowVal; this.highVal = highVal; }

    
    public long    getLowVal() { return lowVal; }
    public long    getHighVal() {return highVal;}

    public boolean includes(long uid) {return lowVal <= uid && uid <= highVal;}

    public static boolean includes(long uid, List<IdRange> list) {
     	   //logger.trace("range size:"+list.size());
           for(int i=0;i<list.size();i++) {
        	   IdRange range=list.get(i);
        	   //logger.trace("range:"+range+" id:"+uid);
        	   if(range.includes(uid))return true;
           }
           return false;
    }

    /**
     * Parses a uid sequence, a comma separated list of uid ranges.
     * <p/>
     * Example: 1 2:5 8:*
     *
     * @param idRangeSequence the sequence
     * @return a list of ranges, never null.
     */
    public static List<IdRange> parseRangeSequence(String idRangeSequence) {
        StringTokenizer tokenizer = new StringTokenizer(idRangeSequence, " ");
        
        List<IdRange>  ranges = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            ranges.add(parseRange(tokenizer.nextToken()));
        }
        return ranges;
    }
    public static List<IdRange> parseRangeSequence(String idRangeSequence,List<IdRange> ranges) {
        StringTokenizer tokenizer = new StringTokenizer(idRangeSequence, " ,");
        
        if(ranges==null ) ranges = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            ranges.add(parseRange(tokenizer.nextToken()));
        }
        return ranges;
    }
    public String toString() {return " min:"+lowVal+" max:"+highVal;}
    /**
     * Parses a single id range, eg "1" or "1:2" or "4:*".
     *
     * @param range the range.
     * @return the parsed id range.
     */
    public static IdRange parseRange(String range) {
    	if("*".contentEquals(range)) {
    		return new IdRange(0, Integer.MAX_VALUE);
    	}
        int pos = range.indexOf(':');
        try {
            if (pos == -1) {
                long value = parseLong(range);
                return new IdRange(value);
            } else {
                long lowVal = parseLong(range.substring(0, pos));
                String hi=range.substring(pos + 1);
                long highVal;
                if("*".contentEquals(hi))highVal=Integer.MAX_VALUE;
                else highVal = parseLong(hi);
                return new IdRange(lowVal, highVal);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid message set " + range);
            return new IdRange(0, 0);
        }
    }

    private static long parseLong(String value) {
        if (value.length() == 1 && value.charAt(0) == '*') {
            return Long.MAX_VALUE;
        }
        return Long.parseLong(value);
    }

 
}
