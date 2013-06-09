//Lab 3 CSC 448 Spring 2013
//Andrew Nguyen, Bryan Ching, Matt Crussell

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/*
 * Creates an Class for each line entry in GFF file
 */
class GFFEntry
{
  String geneName;
  String isoName;
  String type;
  Integer start;
  Integer end;
  Boolean positive;
  String sequence;
  String GFFString;

  public GFFEntry(String geneName, String isoName, String type, String start,
      String end, String positive, String sequence, String GFFString)
  {
    this.geneName = geneName;
    this.isoName = isoName;
    this.type = type;
    if (Integer.valueOf(start) < Integer.valueOf(end))
    {
      this.start = Integer.valueOf(start);
      this.end = Integer.valueOf(end);
    }
    else
    {
      this.start = Integer.valueOf(end);
      this.end = Integer.valueOf(start);
    }
    this.positive = (positive.equals("+"));
    this.sequence = sequence;
    this.GFFString = GFFString;
  }
}

/*
 * Implements Comparable for future potential implementions Contains information
 * on DNA Region, either Intron or Exon
 */
class DNARegion implements Comparable
{
  public int start;
  public int end;
  public int size;
  public String exonString;
  public String GFFString;

  public DNARegion(int start, int end, String sequence, String GFFString)
  {
    this.start = start;
    this.end = end;
    size = end - start + 1;
    calculate(sequence);
    this.GFFString = GFFString;
  }

  // Calculates exonString information
  private void calculate(String sequence)
  {
    // exonString = sequence.substring(start - 1, end);// TODO
  }

  // Not used in current implementation
  public int compareTo(Object otherObject)
  {
    DNARegion other = (DNARegion) otherObject;
    return start - other.start;
  }

}

class Isoform
{
  private ArrayList<DNARegion> exonList = new ArrayList<DNARegion>();
  private String name;
  private int mRNAstart;
  public String GFFString;

  public ArrayList<DNARegion> getExonList()
  {
    return exonList;
  }

  public int getmRNAstart()
  {
    return mRNAstart;
  }

  public int getmRNAend()
  {
    return mRNAend;
  }

  private int mRNAend;
  private boolean positive;

  public Isoform(int mRNAstart, int mRNAend, boolean positive, String GFFString)
  {
    // this.name = name;
    this.mRNAstart = mRNAstart;
    this.mRNAend = mRNAend;
    this.GFFString = GFFString;
    this.positive = positive;

  }

  // public Isoform(boolean positive, String GFFString)
  // {
  // this.positive = positive;
  // this.GFFString = GFFString;
  // }

  public void add(GFFEntry entry)
  {
    if (entry.type.equals("CDS"))
    {
      addExon(entry.start, entry.end, entry.sequence, entry.GFFString);
    }
    else
    {
      mRNAstart = entry.start;
      mRNAend = entry.end;
    }
  }

  // Assume + direction
  public void addExon(int CDSstart, int CDSend, String sequence,
      String GFFString)
  {
    exonList.add(new DNARegion(CDSstart, CDSend, sequence, GFFString));
  }

  public DNARegion getExon(int index)
  {
    return exonList.get(index);
  }

  public int getNumExons()
  {
    return exonList.size();
  }

  public String getName()
  {
    return name;
  }

  public int getNumIntrons()
  {
    return exonList.size() - 1;
  }

  // Includes end codon assumes positive
  public int getCDSSpan()
  {
    return mRNAend - mRNAstart + 4;
  }

  public int getCDSSize()
  {
    int span = 0;
    for (int i = 0; i < exonList.size(); i++)
    {
      DNARegion temp = exonList.get(i);
      span += temp.size;
    }
    return span;
  }

  public int getIntronSize()
  {
    return getCDSSpan() - getCDSSize();
  }

  public ArrayList<DNARegion> getExons()
  {
    return exonList;
  }

  /*
   * public ArrayList<DNARegion> generateIntronList() { ArrayList<DNARegion>
   * intronList = new ArrayList<DNARegion>(); for (int i = 0; i <
   * exonList.size() - 1; i++) { intronList.add(new DNARegion(exonList.get(i +
   * 1).end, exonList.get(i).start)); } return intronList; }
   */
  public String calculate()
  {
    String isoformString = new String();
    Collections.sort(exonList);
    for (int i = 0; i < exonList.size(); i++)
    {
      System.out.println(exonList.get(i).start);
      isoformString = isoformString.concat(exonList.get(i).exonString);
    }
    if (positive == false)
      isoformString = new StringBuilder(
          NucTranslator.reverseComplement(isoformString)).reverse().toString();
    return NucTranslator.translate(isoformString);
  }

  public StringBuilder writeGFF(int offset)
  {
    StringBuilder sb = new StringBuilder();
    if (offset > 0 && mRNAend < offset)
     return sb;
    sb.append(GFFString.replace("" + mRNAstart, "" + (mRNAstart + offset))
        .replace("" + mRNAend, "" + (mRNAend + offset)) + "\n");
    for (DNARegion exon : exonList)
    {
      sb.append(exon.GFFString.replace("" + exon.start,
          "" + (exon.start + offset)).replace("" + exon.end,
          "" + (exon.end + offset))
          + "\n");
    }
    return sb;
  }
}

class Gene
{
  HashMap<String, Isoform> isoList;
  private long CDSSpanSum;
  private long CDSSizeSum;
  private int numExons;
  private long intronSizeSum;
  private int numIntrons;
  private int maxCDSSize;
  private long totalCDS;
  private String geneString = new String();

  public Gene()
  {
    isoList = new HashMap<String, Isoform>();
  }

  public HashMap<String, Isoform> getIsoList()
  {
    return isoList;
  }

  public int getMaxCDSSize()
  {
    return maxCDSSize;
  }

  public long getTotalCDS()
  {
    return totalCDS;
  }

  public long getIntronSizeSum()
  {
    return intronSizeSum;
  }

  public int getNumIntrons()
  {
    return numIntrons;
  }

  public int getNumExons()
  {
    return numExons;
  }

  public long getCDSSizeSum()
  {
    return CDSSizeSum;
  }

  public long getCDSSpanSum()
  {
    return CDSSpanSum;
  }

  public void calculateMaxCDS()
  {
    // TODO
  }

  public void add(GFFEntry entry)
  {
    if (isoList.containsKey(entry.isoName))
    {
      isoList.get(entry.isoName).add(entry);
    }
    else
    {
      Isoform isoform = new Isoform(entry.start, entry.end, entry.positive,
          entry.GFFString);
      isoform.add(entry);
      isoList.put(entry.isoName, isoform);
    }
  }

  public int getIsoSize()
  {
    return isoList.size();
  }

  public String getIsoforms()
  {
    return geneString;
  }

  public ArrayList<DNARegion> getExonList()
  {
    ArrayList<DNARegion> exonList = new ArrayList<DNARegion>();
    Iterator<Entry<String, Isoform>> it = isoList.entrySet().iterator();
    while (it.hasNext())
    {
      Entry<String, Isoform> entry = it.next();
      Isoform isoform = entry.getValue();
      exonList.addAll(isoform.getExonList());
    }
    return exonList;
  }

  public void calculate()
  {
    numExons = 0;
    CDSSpanSum = 0;
    CDSSizeSum = 0;
    intronSizeSum = 0;
    Iterator<Entry<String, Isoform>> it = isoList.entrySet().iterator();
    ArrayList<Integer> maxExons = null;
    totalCDS = 0L;
    while (it.hasNext())
    {
      Entry<String, Isoform> entry = it.next();
      Isoform isoform = entry.getValue();
      geneString = geneString.concat(entry.getKey() + ": "
          + isoform.calculate() + "\n");
      CDSSpanSum += isoform.getCDSSpan();
      CDSSizeSum += isoform.getCDSSize();
      numExons += isoform.getNumExons();
      intronSizeSum += isoform.getIntronSize();
      numIntrons += isoform.getNumIntrons();
      if (maxExons == null)
      {
        maxExons = new ArrayList<Integer>();
        for (int i = 0; i < isoform.getNumExons(); i++)
        {
          maxExons.add(new Integer(isoform.getExon(i).size));
        }
      }
      else
      {
        for (int i = 0; i < isoform.getNumExons(); i++)
        {
          if (maxExons.get(i) < isoform.getExon(i).size)
            maxExons.set(i, new Integer(isoform.getExon(i).size));
        }
      }
      // it.remove(); // avoids a ConcurrentModificationException
    }
    for (int i = 0; i < maxExons.size(); i++)
    {
      totalCDS += maxExons.get(i);
    }
  }

  public StringBuilder writeGFF(int offset)
  {
    StringBuilder sb = new StringBuilder();
    Iterator<Entry<String, Isoform>> it = isoList.entrySet().iterator();
    while (it.hasNext())
    {
      Entry<String, Isoform> entry = it.next();
      Isoform isoform = entry.getValue();
      sb.append(isoform.writeGFF(offset));
    }
    return sb;
  }

}

class DNASequence
{
  HashMap<String, Gene> geneList;
  private double averageCDSSpan;
  private double averageCDSSize;
  private double averageExonSize;
  // cdssize - exonsize?
  private double averageIntronSize;
  private double averageIntergenicRegion;
  private double averageCDSSpanPerTotal;
  private double averageCDSSizePerTotal;
  private double averageGenesPer10kb;
  private double totalNuc;
  private double totalCDSPerTotal;
  private double totalNucPerGene;
  public String seqString = new String();

  public ArrayList<DNARegion> getExonList()
  {
    ArrayList<DNARegion> exonList = new ArrayList<DNARegion>();
    Iterator<Entry<String, Gene>> it = geneList.entrySet().iterator();
    while (it.hasNext())
    {
      Entry<String, Gene> entry = it.next();
      Gene gene = (Gene) entry.getValue();
      exonList.addAll(gene.getExonList());
    }
    return exonList;
  }

  public DNASequence()
  {
    geneList = new HashMap<String, Gene>();
  }

  public HashMap<String, Gene> getGeneList()
  {
    return geneList;
  }

  public double getTotalCDSPerTotal()
  {
    return totalCDSPerTotal;
  }

  public double getAverageIntronSize()
  {
    return averageIntronSize;
  }

  public double getAverageIntergenicRegion()
  {
    return averageIntergenicRegion;
  }

  public double getAverageCDSSpanPerTotal()
  {
    return averageCDSSpanPerTotal;
  }

  public double getAverageCDSSizePerTotal()
  {
    return averageCDSSizePerTotal;
  }

  public double getAverageGenesPer10kb()
  {
    return averageGenesPer10kb;
  }

  public double getTotalNuc()
  {
    return totalNuc;
  }

  public double getAverageExonSize()
  {
    return averageExonSize;
  }

  public double getAverageCDSSpan()
  {
    return averageCDSSpan;
  }

  public double getAverageCDSSize()
  {
    return averageCDSSize;
  }

  public void add(GFFEntry entry)
  {
    if (geneList.containsKey(entry.geneName))
    {
      geneList.get(entry.geneName).add(entry);
    }
    else
    {
      Gene gene = new Gene();
      gene.add(entry);
      geneList.put(entry.geneName, gene);
    }
  }

  public void setNuc(int totalNuc)
  {
    this.totalNuc = totalNuc;
  }

  public String getSequence()
  {
    return seqString;
  }

  public void calculate()
  {
    long sumCDSSpan = 0L;
    long sumCDSSize = 0L;
    int sumNumExons = 0;
    int sumNumIso = 0;
    long sumIntronSize = 0;
    int sumNumIntrons = 0;
    long totalCDS = 0L;
    Iterator<Entry<String, Gene>> it = geneList.entrySet().iterator();
    int minMRNA = 0;
    int maxMRNA = 0;
    while (it.hasNext())
    {
      Entry<String, Gene> entry = it.next();
      Gene gene = (Gene) entry.getValue();
      gene.calculate();
      seqString = seqString.concat(entry.getKey() + "\n" + gene.getIsoforms()
          + "\n");
      sumNumIso += gene.getIsoSize();
      sumCDSSpan += gene.getCDSSpanSum();
      sumCDSSize += gene.getCDSSizeSum();
      sumNumExons += gene.getNumExons();
      sumIntronSize += gene.getIntronSizeSum();
      sumNumIntrons += gene.getNumIntrons();

      totalCDS += gene.getTotalCDS();
      // it.remove(); // avoids a ConcurrentModificationException
      if (minMRNA == 0)
      {
        minMRNA = ((Isoform) gene.getIsoList().values().toArray()[0])
            .getmRNAstart();
        maxMRNA = ((Isoform) gene.getIsoList().values().toArray()[0])
            .getmRNAend();
      }
      else
      {
        minMRNA = Math.min(minMRNA, ((Isoform) gene.getIsoList().values()
            .toArray()[0]).getmRNAstart());
        maxMRNA = Math.max(maxMRNA, ((Isoform) gene.getIsoList().values()
            .toArray()[0]).getmRNAend());
      }
    }
    averageCDSSpan = (double) sumCDSSpan / sumNumIso;
    averageCDSSize = (double) sumCDSSize / sumNumIso;
    averageExonSize = (double) sumCDSSize / sumNumExons;
    averageIntronSize = (double) sumIntronSize / sumNumIntrons;

    averageIntergenicRegion = (maxMRNA - minMRNA + 3 + 1 - averageCDSSpan
        * geneList.size())
        / (geneList.size() - 1);
    averageCDSSpanPerTotal = geneList.size() * averageCDSSpan / totalNuc;
    averageCDSSizePerTotal = geneList.size() * averageCDSSize / totalNuc;
    averageGenesPer10kb = (double) geneList.size() / totalNuc * 10000;
    totalCDSPerTotal = (double) totalCDS / totalNuc;
    totalNucPerGene = totalNuc / 10000 / geneList.size();
  }

  public double getTotalNucPerGene()
  {
    return totalNucPerGene;
  }

  public StringBuilder writeGFF(int offset)
  {
    StringBuilder sb = new StringBuilder();
    Iterator<Entry<String, Gene>> it = geneList.entrySet().iterator();
    while (it.hasNext())
    {
      Entry<String, Gene> entry = it.next();
      Gene gene = (Gene) entry.getValue();
      sb.append(gene.writeGFF(offset));
    }
    return sb;
  }
}

public class driver
{
  int counter = 0;

  public String drive(String FASTAfile, String GFFfile, String FASTAfile2,
      String GFFfile2)
  {
    DNASequence sequence = new DNASequence();
    DNASequence sequence2 = new DNASequence();
    String nucleotides = readFastaFile(FASTAfile);
    String nucleotides2 = readFastaFile(FASTAfile2);

    parseGFFFile(GFFfile, sequence, nucleotides);
    parseGFFFile(GFFfile2, sequence2, nucleotides2);

    int offset = Aligner.getAlignmentIndex(nucleotides, nucleotides2);
    System.out.println(offset);
    String finalSequence = Aligner.mergeContigs(nucleotides, nucleotides2,
        offset);
    writeFasta(finalSequence);

    // writeGFF(sequence);

    if (offset == -1)
    {
      // String finalSequence = Aligner.mergeContigs(nucleotides, nucleotides2,
      // offset);
      // writeFasta(finalSequence);
      // writeGFF(sequence);
    }
    else
    {
      findConflicts(sequence.getExonList(), sequence2.getExonList(), offset,
          nucleotides.length() - offset);
      writeGFF(printGFF(sequence, 0).append(printGFF(sequence2, offset)));
      // System.out.println();
    }

    String returnString = new String();

    return returnString;

  }

  public class ConflictingExon
  {
    DNARegion before;
    DNARegion after;

    public ConflictingExon(DNARegion before, DNARegion after)
    {
      super();
      this.before = before;
      this.after = after;
    }
  }

  public ArrayList<ConflictingExon> findConflicts(ArrayList<DNARegion> before,
      ArrayList<DNARegion> after, int bOffset, int aOffset)
  {
    ArrayList<ConflictingExon> returnArr = new ArrayList<ConflictingExon>();
    ArrayList<DNARegion> before2 = (ArrayList<DNARegion>) before.clone();
    ArrayList<DNARegion> after2 = (ArrayList<DNARegion>) after.clone();
    for (DNARegion exon : before)
    {
      if (exon.end < bOffset)
      {
        if (before2.remove(exon) == false)
          System.out.println("null");
      }
    }

    for (DNARegion exon : after)
    {
      if (exon.start > aOffset)
      {
        after2.remove(exon);
      }
    }
    // for(DNARegion exon : after)
    // {
    // System.out.println(exon.start);
    // }
    ArrayList<DNARegion> before3 = (ArrayList<DNARegion>) before2.clone();
    ArrayList<DNARegion> after3 = (ArrayList<DNARegion>) after2.clone();
    for (DNARegion exon1 : before2)
    {
      for (DNARegion exon2 : after2)
      {
        // System.out.println(exon1.start + " " + (exon2.start + aOffset) + " "
        // + Offset);
        if ((exon1.start == (exon2.start + bOffset))
            && (exon1.end == (exon2.end + bOffset)))
        {
          // System.out.println("matched");
          before3.remove(exon1);
          after3.remove(exon2);
        }

      }
    }
    System.out.println(before3);
    for (DNARegion exon1 : before3)
    {
      System.out.println(exon1.start + " " + exon1.end);
    }
    System.out.println(after3);
    for (DNARegion exon1 : after3)
    {
      System.out.println(exon1.start + " " + exon1.end);
    }

    return returnArr;
  }

  public void writeFasta(String finalSequence)
  {
    Path path = FileSystems.getDefault().getPath(".",
        "output" + (counter) + ".fna");
    try
    {
      Files.write(path, ("\n" + finalSequence).getBytes(),
          StandardOpenOption.CREATE);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public StringBuilder printGFF(DNASequence sequence, int offset)
  {
    return sequence.writeGFF(offset);
  }

  public void writeGFF(StringBuilder sb)
  {
    Path path = FileSystems.getDefault().getPath(".",
        "output" + (counter) + ".gff");
    try
    {
      Files.write(path, sb.toString().getBytes(), StandardOpenOption.CREATE);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.out.print(sequence.writeGFF(offset).toString());
  }

  public void parseGFFFile(String path, DNASequence sequence, String seq)
  {
    try
    {
      FileInputStream fstream = new FileInputStream(path);
      Scanner fScanner = new Scanner(fstream);
      String currLine = new String();
      while (fScanner.hasNextLine())
      {
        currLine = fScanner.nextLine();
        String[] splitLine = currLine.split("\\s+");
        if (splitLine.length > 10
            && (splitLine[2].equals("mRNA") || splitLine[2].equals("CDS")))
        {
          // 9 genename
          // 11 isoname
          // 2 cds/mrna
          // 3 start
          // 4 end
          // 6 +/-
          // returnString = returnString.concat("" + currLine);
          // returnString = returnString.concat("" + splitLine[0]);

          sequence.add(new GFFEntry(splitLine[9], splitLine[11], splitLine[2],
              splitLine[3], splitLine[4], splitLine[6], seq, currLine));
        }

      }
      fScanner.close();
    }
    catch (FileNotFoundException e)
    {
      // returnString = returnString.concat("" + "File " + path +
      // " not found.");
    }
  }

  public String readFastaFile(String path)
  {
    Scanner sc;
    try
    {
      sc = new Scanner(new File(path));
    }
    catch (Exception e)
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

  public static void main(String[] args)
  {
    driver d = new driver();

    String file1 = "derecta_3Lcontrol_fosmid1.0";
    String file2 = "derecta_3Lcontrol_fosmid2.0";

    d.drive("derecta_3Lcontrol_fasta/" + file1 + ".fna",
        "derecta_3Lcontrol_gff/" + file1 + ".gff", "derecta_3Lcontrol_fasta/"
            + file2 + ".fna", "derecta_3Lcontrol_gff/" + file2 + ".gff");

  }
}
