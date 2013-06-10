public class Overlap {

   // returns true two substrings overlap 
   public static int isOverlapping(int aStart, int aEnd, int bStart, int bEnd) {
      // return 0 if they are matching overlapping
      if(aStart == bStart && aEnd == bEnd) {
         return 0;
      }

      // return 1 if they align on one end
      if(aStart == bStart || aEnd == bEnd) {
         return 1;
      }
      if(aStart == bEnd || aEnd == bStart) {
         return 1;
      }
      if(aStart < bStart) {
         if(bStart < aEnd) {
            return 1;
         }
         else return 0;
      }
      else if (aStart < bEnd) {
         return 1;
      }
      else return 0;

   }

}

