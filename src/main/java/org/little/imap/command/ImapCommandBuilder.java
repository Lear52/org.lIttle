package org.little.imap.command;

import java.util.ArrayList;
import java.util.List;

import org.little.imap.command.cmd.AppendCommand;
import org.little.imap.command.cmd.AuthenticateCommand;
import org.little.imap.command.cmd.CapabilityCommand;
import org.little.imap.command.cmd.CheckCommand;
import org.little.imap.command.cmd.CloseCommand;
import org.little.imap.command.cmd.CopyCommand;
import org.little.imap.command.cmd.CreateCommand;
import org.little.imap.command.cmd.DeleteCommand;
import org.little.imap.command.cmd.ExamineCommand;
import org.little.imap.command.cmd.ExpungeCommand;
import org.little.imap.command.cmd.FetchCommand;
import org.little.imap.command.cmd.ListCommand;
import org.little.imap.command.cmd.LoginCommand;
import org.little.imap.command.cmd.LogoutCommand;
import org.little.imap.command.cmd.LsubCommand;
import org.little.imap.command.cmd.NoopCommand;
import org.little.imap.command.cmd.QuotaCommand;
import org.little.imap.command.cmd.QuotaRootCommand;
import org.little.imap.command.cmd.RenameCommand;
import org.little.imap.command.cmd.SearchCommand;
import org.little.imap.command.cmd.SelectCommand;
import org.little.imap.command.cmd.SetQuotaCommand;
import org.little.imap.command.cmd.SortCommand;
import org.little.imap.command.cmd.StatusCommand;
import org.little.imap.command.cmd.StoreCommand;
import org.little.imap.command.cmd.SubscribeCommand;
import org.little.imap.command.cmd.UidCommand;
import org.little.imap.command.cmd.UnsubscribeCommand;
//import org.little.util.Logger;
//import org.little.util.LoggerFactory;
//import org.little.imap.command.cmd.*;


public class ImapCommandBuilder {

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
               if(AppendCommand.NAME.equals(command)       ) return new AppendCommand      (tag, command, new ArrayList<>(params));
               else
               if(AuthenticateCommand.NAME.equals(command) ) return new AuthenticateCommand(tag, command, new ArrayList<>(params));
               else
               if(CapabilityCommand.NAME.equals(command)   ) return new CapabilityCommand  (tag, command, new ArrayList<>(params));
               else
               if(CheckCommand.NAME.equals(command)        ) return new CheckCommand       (tag, command, new ArrayList<>(params));
               else
               if(CloseCommand.NAME.equals(command)        ) return new CloseCommand       (tag, command, new ArrayList<>(params));
               else
               if(CopyCommand.NAME.equals(command)         ) return new CopyCommand        (tag, command, new ArrayList<>(params));
               else
               if(CreateCommand.NAME.equals(command)       ) return new CreateCommand      (tag, command, new ArrayList<>(params));
               else
               if(DeleteCommand.NAME.equals(command)       ) return new DeleteCommand      (tag, command, new ArrayList<>(params));
               else
               if(ExamineCommand.NAME.equals(command)      ) return new ExamineCommand     (tag, command, new ArrayList<>(params));
               else
               if(ExpungeCommand.NAME.equals(command)      ) return new ExpungeCommand     (tag, command, new ArrayList<>(params));
               else
               if(FetchCommand.NAME.equals(command)        ) return new FetchCommand       (tag, command, new ArrayList<>(params));
               else
               if(ListCommand.NAME.equals(command)         ) return new ListCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LoginCommand.NAME.equals(command)        ) return new LoginCommand       (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LogoutCommand.NAME.equals(command)       ) return new LogoutCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(LsubCommand.NAME.equals(command)         ) return new LsubCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(NoopCommand.NAME.equals(command)         ) return new NoopCommand        (tag, command, new ArrayList<>(params));
               else                                                                       
               if(QuotaCommand.NAME.equals(command)        ) return new QuotaCommand       (tag, command, new ArrayList<>(params));
               else
               if(QuotaRootCommand.NAME.equals(command)    ) return new QuotaRootCommand   (tag, command, new ArrayList<>(params));
               else
               if(RenameCommand.NAME.equals(command)       ) return new RenameCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SearchCommand.NAME.equals(command)       ) return new SearchCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SelectCommand.NAME.equals(command)       ) return new SelectCommand      (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SetQuotaCommand.NAME.equals(command)     ) return new SetQuotaCommand    (tag, command, new ArrayList<>(params));
               else                                                                       
               if(SortCommand.NAME.equals(command)         ) return new SortCommand        (tag, command, new ArrayList<>(params));
               else                                       
               if(StatusCommand.NAME.equals(command)       ) return new StatusCommand      (tag, command, new ArrayList<>(params));
               else                                       
               if(StoreCommand.NAME.equals(command)        ) return new StoreCommand       (tag, command, new ArrayList<>(params));
               else                                       
               if(SubscribeCommand.NAME.equals(command)    ) return new SubscribeCommand   (tag, command, new ArrayList<>(params));
               else                                       
               if(UidCommand.NAME.equals(command)          ) return new UidCommand         (tag, command, new ArrayList<>(params));
               else
               if(UnsubscribeCommand.NAME.equals(command)  ) return new UnsubscribeCommand (tag, command, new ArrayList<>(params));
               else
               return new ImapCommand(tag, command, new ArrayList<>(params));
       }

       public void addParam(ImapCommandParameter param) {
              params.add(param);
       }
}
