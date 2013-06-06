public class Aligner {

   // Returns 0-indexed position in first string where second string prefix starts
   public static int getAlignmentIndex(String s1, String s2) {
      int i = s1.length() - s2.length();
      int start = (i <= 0) ? 0 : i;
      int len;

      for(i = start; i < s1.length(); i++) {
         len = s1.length() - i;
         if(s1.substring(i).equals(s2.substring(0, len))) {
            return i;
         }
      }
      return -1;
   }

   // Returns merged contig string
   public static String mergeContigs(String s1, String s2) {
      int s = getAlignmentIndex(s1, s2);

      return (s == -1) ? "" : s1.substring(0, s + 1) + s2;
   }
}

