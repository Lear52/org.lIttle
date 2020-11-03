package org.little.smtpcln.rr;

import io.netty.buffer.ByteBuf;

public class LastSmtpContent extends SmtpContent {

       /**
        * Creates a new instance using the given data.
        */
       public LastSmtpContent(ByteBuf data) {
           super(data);
       }
      
       @Override
       public LastSmtpContent copy() {
           return (LastSmtpContent) super.copy();
       }
      
       @Override
       public LastSmtpContent duplicate() {
           return (LastSmtpContent) super.duplicate();
       }
      
       @Override
       public LastSmtpContent retainedDuplicate() {
           return (LastSmtpContent) super.retainedDuplicate();
       }
      
       @Override
       public LastSmtpContent replace(ByteBuf content) {
           return new LastSmtpContent(content);
       }
      
       @Override
       public LastSmtpContent retain() {
           super.retain();
           return this;
       }
      
       @Override
       public LastSmtpContent retain(int increment) {
           super.retain(increment);
           return this;
       }
      
       @Override
       public LastSmtpContent touch() {
           super.touch();
           return this;
       }
      
       @Override
       public LastSmtpContent touch(Object hint) {
           super.touch(hint);
           return this;
       }
}
