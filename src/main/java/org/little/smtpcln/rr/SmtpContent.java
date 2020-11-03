package org.little.smtpcln.rr;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

/**
 * Default implementation of {@link _SmtpContent} that does no validation of the raw data passed in.
 */

public class SmtpContent extends DefaultByteBufHolder implements SmtpElement /*implements _SmtpContent */{

       private int                      id;
       public  byte []                  body;
      
       /**
        * Creates a new instance using the given data.
        */
       public SmtpContent(ByteBuf data) {
              super(data);
       }
      
       public SmtpContent copy() {
              return (SmtpContent) super.copy();
       }

       public void set(byte []  _body) {
              body=_body;
       }

      
       public SmtpContent duplicate() {
              return (SmtpContent) super.duplicate();
       }
      
       public SmtpContent retainedDuplicate() {
              return (SmtpContent) super.retainedDuplicate();
       }
      
       public SmtpContent replace(ByteBuf content) {
              return new SmtpContent(content);
       }
      
       public SmtpContent retain() {
              super.retain();
              return this;
       }
      
       public SmtpContent retain(int increment) {
              super.retain(increment);
              return this;
       }
      
       public SmtpContent touch() {
              super.touch();
              return this;
       }
      
       public SmtpContent touch(Object hint) {
              super.touch(hint);
              return this;
       }

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int _id) {
		id=_id;
		
	}
}
