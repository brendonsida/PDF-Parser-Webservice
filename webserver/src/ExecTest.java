import java.io.IOException;
import java.util.*;
 
public class ExecTest
{
  private String output = "It ran";
   
  public static String main(String[] args) throws Exception
  {
    ExecTest exec = new ExecTest(args[0],args[1]);
    return exec.toString();
  }

  public ExecTest(String path, String args) 
  throws IOException, InterruptedException
  {
    // determine the number of processes running on the current
    // linux, unix, or mac os x system.
    List<String> commands = new ArrayList<String>();
    System.out.print(args);
    commands.add("java");
    commands.add("-jar");
    commands.add(path);
    commands.add("-g"+args);
 
    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
    int result = commandExecutor.executeCommand();
 
    // stdout and stderr of the command are returned as StringBuilder objects
    StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
    StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
    this.output = stdout.toString();
  }
  
  public String toString(){
      return output;
  }
}
