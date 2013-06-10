public class Overlap {

   // returns true two substrings overlap 
   public static boolean isOverlapping(int aStart, int aEnd, int bStart, int bEnd) {
      // return 0 if they are matching overlapping
      if(aStart == bStart && aEnd == bEnd) {
         return false;
      }

      // return 1 if they align on one end
      if(aStart == bStart || aEnd == bEnd) {
         return true;
      }
      if(aStart == bEnd || aEnd == bStart) {
         return true;
      }
      if(aStart < bStart) {
         if(bStart < aEnd) {
            return true;
         }
         else return false;
      }
      else if (aStart < bEnd) {
         return true;
      }
      else return false;

   }

}

