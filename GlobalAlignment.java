import java.lang.Math;
import java.util.Arrays;
public class GlobalAlignment
{
  public static int NWGlobalAlign(String s, String t, int d)
  {
    s = '_' + s;
    t = '_' + t;
    int n = s.length();
    int m = t.length();
    int[][] c = new int[n][m];
    int[][] u = new int[n][m];
    int i, j;
    
    for(i = 0; i < n; i++)
    {
      c[i][0] = d * i;
    }
    for(j = 0; j < m; j++)
    {
      c[0][j] = d * j;
    }
    for(i = 1; i < n; i++)
    {
      for(j = 1; j < m; j++)
      {
        int Replace = c[i - 1][j - 1] + score(s.charAt(i), t.charAt(j));
        int Insert = c[i][j - 1] + d;
        int Delete = c[i - 1][j] + d;
        c[i][j] = Math.max(Insert, Delete);
        c[i][j] = Math.max(Replace, c[i][j]);
        
        if(c[i][j] == Replace)
          u[i][j] = -3;
        else if(c[i][j] == Insert)
          u[i][j] = -2;  
        else if(c[i][j] == Delete)
          u[i][j] = -1; 
      }
    }
    
    //String[] recover = AlignmentRecover(s, t, u);
    //System.out.println(s + " = " + recover[0] + "\n" + t + " = " + recover[1]);
    return c[n-1][m-1];
  }
  private static String[] AlignmentRecover(String s, String t, int[][] u)
  {
    String A[] = {"",""};
    int i = s.length() - 1;
    int j = t.length() - 1;
    while(i + j > 0)
    {
      if(u[i][j] == -3) //Replacement
      {
        A[0] = s.charAt(i--) + A[0]; 
        A[1] = t.charAt(j--) + A[1];
      }
      else if(u[i][j] == -2 || i == 0) //Insertion
      {
        A[0] = "_" + A[0]; 
        A[1] = t.charAt(j--) + A[1];
      }
      else if(u[i][j] ==  -1 || j == 0)//Deletion
      {
        A[0] = s.charAt(i--) + A[0]; 
        A[1] = "_" + A[1];
      }
    }
    return A;
  }
  public static int score(char s, char t)
  {
    if(s  == t)
      return 5;
    return -4;
  }
  public static void printMatrix(int[][] matrix)
  {
    for(int[] arr: matrix)
    {
      System.out.println(Arrays.toString(arr));
    }
  }
}

/*class driver
{
  public static void main(String[] args)
  {
    String s = "ATGCAT";
    String t = "TGATGA";
//    int[][] score = GlobalAlignment.getScore(s, t);
//    GlobalAlignment.printMatrix(score);
    System.out.println(GlobalAlignment.NWGlobalAlign(s, t,  -3));
  }
}*/
