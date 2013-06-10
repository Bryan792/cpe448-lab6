/*
 * Team 6
 * Andrew Nguyen
 * Bryan Ching
 * Matt Crussell
 * CPE 448 Bioinformatics
 * NaiveSuffixTree
 */
import java.util.ArrayList;
import java.util.HashMap;

public class ConflictParser
{
  public static HashMap<String, ArrayList<String>> parse(String[] lines)
  {
    HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
    ArrayList<String> returnStrings = null;
    String Aname = "", A = "", Bname = "", B = "", reference = "";
    int steps = 0;

    for (int i = 0; i<lines.length; i++)
    {
      String line = lines[i];
      if (line.length() == 0 || line.charAt(0) == '\n' || line.charAt(0) == '-'
          || line.charAt(0) == 'R')
        continue;
      if (line.charAt(0) == '/')
      {
        line = line.substring(1);
        // returnStrings.add("PATH");
        if (hm.containsKey(line))
        {
          returnStrings = hm.get(line);
        }
        else
        {
          returnStrings = new ArrayList<String>();
          hm.put(line, returnStrings);
        }
//        returnStrings.add(line);
//        System.out.print("Start: ");
      }
      else
      {
        switch (steps)
        {
        case (0):
          Aname = line;
          // System.out.print("Aname: ");
          break;
        case (1):
          A = line;
          break;
        case (2):
          Bname = line;
          // System.out.print("Bname: ");
          break;
        case (3):
          B = line;
          break;
        case (4):
          reference = line;
          returnStrings.add(resolve(Aname, A, Bname, B, reference));
          // System.out.print("Bname: ");
          steps = -1;
          break;
        default:
          break;
        }
        steps++;
      }
      // System.out.println(line);
    }
    return hm;
  }

  public static String resolve(String Aname, String A, String Bname, String B,
      String reference)
  {
    int AScore = 0, BScore = 0;

    AScore = GlobalAlignment.NWGlobalAlign(A, reference, -4);
    BScore = GlobalAlignment.NWGlobalAlign(B, reference, -4);
    if (AScore >= BScore)
      return Bname;
    return Aname;
  }
}

class drivedrive
{
  public static void main(String[] args)
  {
    String[] test = { "------", "/PATH", "GEORGE", "GATTACA", "PHIL",
        "GCCGCCGCC", "REFERENCE", "GATTACA" };
    HashMap<String, ArrayList<String>> result = ConflictParser.parse(test);
//    System.out.println(result);
  }
}
