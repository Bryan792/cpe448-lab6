/*
 * Team 6
 * Andrew Nguyen
 * Bryan Ching
 * Matt Crussell
 * CPE 448 Bioinformatics
 * NaiveSuffixTree
 */

import java.io.File;
import java.util.Scanner;


public class FileReader
{
  public static String readFastaFile(String path)
  {
    Scanner sc;
    try
    {
      sc = new Scanner(new File(path));
    } catch (Exception e)
    {
      sc = new Scanner("");
    }

    String sequence;
    StringBuilder sb = new StringBuilder();
    if (sc.hasNextLine())
    {
      sc.nextLine();
    }

    while (sc.hasNextLine())
    {
      sb.append(sc.nextLine());
    }

    sequence = sb.toString();
    sequence = sequence.replace("\n", "");
    sequence = sequence.replaceAll("\\s+", "");
    sc.close();
    return sequence;
  }
}
