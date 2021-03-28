package org.little.mail.imap.command;

import java.util.ArrayList;
import java.util.List;

import org.little.mail.imap.command.cmd.AppendCommand;
import org.little.mail.imap.command.cmd.AuthenticateCommand;
import org.little.mail.imap.command.cmd.CapabilityCommand;
import org.little.mail.imap.command.cmd.CheckCommand;
import org.little.mail.imap.command.cmd.CloseCommand;
import org.little.mail.imap.command.cmd.CopyCommand;
import org.little.mail.imap.command.cmd.CreateCommand;
import org.little.mail.imap.command.cmd.DeleteCommand;
import org.little.mail.imap.command.cmd.ExamineCommand;
import org.little.mail.imap.command.cmd.ExpungeCommand;
import org.little.mail.imap.command.cmd.FetchCommand;
import org.little.mail.imap.command.cmd.ListCommand;
import org.little.mail.imap.command.cmd.LoginCommand;
import org.little.mail.imap.command.cmd.LogoutCommand;
import org.little.mail.imap.command.cmd.LsubCommand;
import org.little.mail.imap.command.cmd.NoopCommand;
import org.little.mail.imap.command.cmd.QuotaCommand;
import org.little.mail.imap.command.cmd.QuotaRootCommand;
import org.little.mail.imap.command.cmd.RenameCommand;
import org.little.mail.imap.command.cmd.SearchCommand;
import org.little.mail.imap.command.cmd.SelectCommand;
import org.little.mail.imap.command.cmd.SetQuotaCommand;
import org.little.mail.imap.command.cmd.SortCommand;
import org.little.mail.imap.command.cmd.StatusCommand;
import org.little.mail.imap.command.cmd.StoreCommand;
import org.little.mail.imap.command.cmd.SubscribeCommand;
import org.little.mail.imap.command.cmd.UidCommand;
import org.little.mail.imap.command.cmd.UnsubscribeCommand;
import org.little.util.Logger;
import org.little.util.LoggerFactory;


public class ImapCommandBuilder {
       private static final Logger logger = LoggerFactory.getLogger(ImapCommandBuilder.class);

       private String tag;
       private String command;
       private List<ImapCommandParameter> params = new ArrayList<>(30);

       public ImapCommandBuilder tag(String tag) {
              this.tag = tag;
              return this;
       }

       public ImapCommandBuilder command(String command) {
              this.command = command;
              return this;
       }

       public ImapCommand build() {
               if(AppendCommand.NAME.equalsIgnoreCase(command)       ) return new AppendCommand      (tag, command, new ArrayList<>(params));
               else
               if(AuthenticateCommand.NAME.equalsIgnoreCase(command) ) return new AuthenticateCommand(tag, command, new ArrayList<>(params));
               else
               if(CapabilityCommand.NAME.equalsIgnoreCase(command)   ) return new CapabilityCommand  (tag, command, new ArrayList<>(params));
               else
               if(CheckCommand.NAME.equalsIgnoreCase(command)        ) return new CheckCommand       (tag, command, new ArrayList<>(params));
               else
               if(CloseCommand.NAME.equalsIgnoreCase(command)        ) return new CloseCommand       (tag, command, new ArrayList<>(params));
               else
               if(CopyCommand.NAME.equalsIgnoreCase(command)         ) return new CopyCommand        (tag, command, new ArrayList<>(params));
               else
               if(CreateCommand.NAME.equalsIgnoreCase(command)       ) return new CreateCommand      (tag, command, new ArrayList<>(params));
               else
               if(DeleteCommand.NAME.equalsIgnoreCase(command)       ) return new DeleteCommand      (tag, command, new ArrayList<>(params));
               else
               if(ExamineCommand.NAME.equalsIgnoreCase(command)      ) return new ExamineCommand     (tag, command, new ArrayList<>(params));
               else
               if(ExpungeCommand.NAME.equalsIgnoreCase(command)      ) return new ExpungeCommand     (tag, command, new ArrayList<>(params));
               else
               if(FetchCommand.NAME.equalsIgnoreCase(command)        ) return new FetchCommand       (tag, command, new ArrayList<>(params));
               else
               if(ListCommand.NAME.equalsIgnoreCase(command)         ) return new ListCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LoginCommand.NAME.equalsIgnoreCase(command)        ) return new LoginCommand       (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LogoutCommand.NAME.equalsIgnoreCase(command)       ) return new LogoutCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LsubCommand.NAME.equalsIgnoreCase(command)         ) return new LsubCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(NoopCommand.NAME.equalsIgnoreCase(command)         ) return new NoopCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(QuotaCommand.NAME.equalsIgnoreCase(command)        ) return new QuotaCommand       (tag, command, new ArrayList<>(params));
               else
               if(QuotaRootCommand.NAME.equalsIgnoreCase(command)    ) return new QuotaRootCommand   (tag, command, new ArrayList<>(params));
               else
               if(RenameCommand.NAME.equalsIgnoreCase(command)       ) return new RenameCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SearchCommand.NAME.equalsIgnoreCase(command)       ) return new SearchCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SelectCommand.NAME.equalsIgnoreCase(command)       ) return new SelectCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SetQuotaCommand.NAME.equalsIgnoreCase(command)     ) return new SetQuotaCommand    (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SortCommand.NAME.equalsIgnoreCase(command)         ) return new SortCommand        (tag, command, new ArrayList<>(params));
               else                                       
               if(StatusCommand.NAME.equalsIgnoreCase(command)       ) return new StatusCommand      (tag, command, new ArrayList<>(params));
               else                                       
               if(StoreCommand.NAME.equalsIgnoreCase(command)        ) return new StoreCommand       (tag, command, new ArrayList<>(params));
               else                                       
               if(SubscribeCommand.NAME.equalsIgnoreCase(command)    ) return new SubscribeCommand   (tag, command, new ArrayList<>(params));
               else                                       
               if(UidCommand.NAME.equalsIgnoreCase(command)          ) return new UidCommand         (tag, command, new ArrayList<>(params));
               else
               if(UnsubscribeCommand.NAME.equalsIgnoreCase(command)  ) return new UnsubscribeCommand (tag, command, new ArrayList<>(params));
               else {
                   logger.error("unknown command:"+command);
                   return new ImapCommand(tag, command, new ArrayList<>(params));
               }
       }

       public void addParam(ImapCommandParameter param) {
              params.add(param);
       }
}
