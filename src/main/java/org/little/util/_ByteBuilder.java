package org.little.util;

public class _ByteBuilder{

    private byte[] value;
    private int    count;


    private void _size(int len){
            value = new byte[len];
            count=0;
    }
    private void _addsize(int len){
        if((count + len)>value.length){
            byte []new_value = new byte[count + len+100];
            if(value!=null)System.arraycopy(value,0,new_value,0,value.length);
            value=new_value;
        }
    }
    public int capacity() {
        return value.length;
    }
    public int length() {
        return count;
    }

    public _ByteBuilder  append(byte [] buf){
        if (buf == null) return this;
        int len = buf.length;
        _addsize(len);
        System.arraycopy(buf, 0, value, count, len);
        count += len;
        return this;
    }

    public _ByteBuilder() {
           _size(100);
    }

    public _ByteBuilder(int len) {
           _size(len);
    }

    /**
     * Constructs a string builder initialized to the contents of the
     * specified string. The initial capacity of the string builder is
     * {@code 16} plus the length of the string argument.
     *
     * @param   buf   the initial contents of the buffer.
     */
    public _ByteBuilder(byte [] buf) {
        _size(buf.length + 160);
        append(buf);
    }

    private byte[] getValue() {
        return value;
    }

    public _ByteBuilder append(_ByteBuilder buf) {
        return append(buf.getValue());
    }
    
    //private _ByteBuilder append(byte [] s, int start, int end) {
        //super.append(s, start, end);
    //    return this;
    //}
    
    public _ByteBuilder append(byte c) {
         _addsize(1);
         value[count]=c;
         count++;
         return this;
    }

    public _ByteBuilder append(int i) {
        return append((byte)i);
    }

    //private _ByteBuilder delete(int start, int end) {
    //    return this;
    //}
  
    public byte [] getBytes() {
           byte []new_value = new byte[count];
           if(value!=null)System.arraycopy(value,0,new_value,0,count);
           return new_value;
    }

    @Override
    public String toString() {
           return new String(getBytes());
    }

    public void write(java.io.OutputStream s) throws java.io.IOException {
            s.write(getValue(), 0, count);
    }

    public int read(java.io.InputStream s)   throws java.io.IOException{
            int len=s.available();
            if(len<=0)return 0;
            _addsize(len);
            int ret=s.read(value, count, len);
            if(ret>0)count+=ret;
            return ret;
    }

    public static byte[] toByte(final java.io.InputStream in) throws java.io.IOException{
           _ByteBuilder buf=new _ByteBuilder(10240);
           int ret=0;
           do{ ret=buf.read(in);}while(ret>0);
           return buf.getBytes();
    }


}
