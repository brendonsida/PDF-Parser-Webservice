package team.frontend.app;

import java.io.IOException;
import java.util.*;
 
public class ExecTest
{
  private String output = "It ran";
   
  public static String main(String[] args) throws Exception
  {
    ExecTest exec = new ExecTest(args[0],args[1],args[2],args[3]);
    return exec.toString();
  }

  public ExecTest(String path, String args1, String args2, String file) 
  throws IOException, InterruptedException
  {
    // determine the number of processes running on the current
    // linux, unix, or mac os x system.
    List<String> commands = new ArrayList<String>();
    System.out.print(args1+" "+args2);
    commands.add("java");
    commands.add("-jar");
    commands.add(path);
    commands.add(args1);
    commands.add(args2);
    commands.add(file);
 
    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
    int result = commandExecutor.executeCommand();
 
    // stdout and stderr of the command are returned as StringBuilder objects
    StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
    StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
    System.out.println(stderr.toString());
    this.output = stdout.toString();
  }
  
  public String toString(){
      return output;
  }
}
