package findset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * find search set for luciphor (modplus result)
 * 
 * @author: Jonghun Park
 * 2016.05.20
 */
public class FindSearchSet {

  /*
   * MODplus Residue and the number of Modification
   */
  static final int C  = 8; //7 + 1( de-carbamedometyl)
  static final int D  = 3;
  static final int E  = 3;
  static final int H  = 2;
  static final int K  = 4;
  static final int M  = 4;
  static final int N  = 2;
  static final int Nt = 5;
  static final int P  = 1;
  static final int Q  = 2;
  static final int R  = 2;
  static final int S  = 3;
  static final int T  = 3;
  static final int W  = 4;
  static final int Y  = 1;
  
  static final String MODPLUS_RESULT_DELIMETER = "\t";
  
  public static void main(String[] args) throws IOException {
    
    System.out.println(args[0]);
    System.out.println("start");
    
    int[][][][][][][][][][][][][][][] set = new int[C+1]
                                                   [D+1]
                                                   [E+1]
                                                   [H+1]
                                                   [K+1]
                                                   [M+1]
                                                   [N+1]
                                                   [Nt+1]
                                                   [P+1]
                                                   [Q+1]
                                                   [R+1]
                                                   [S+1]
                                                   [T+1]
                                                   [W+1]
                                                   [Y+1];
    
    System.out.println("Array memory allocation done");
    
    /*
     * mod name and Number
     */
    HashMap<String, Integer> modNumPair = new HashMap<String, Integer>();
    
    // C
    modNumPair.put("C:Cys->Ser", 1);
    modNumPair.put("C:CarbamidomethylDTT", 2);
    modNumPair.put("C:Dioxidation", 3);
    modNumPair.put("C:Dehydroalanine" , 4);
    modNumPair.put("C:Pyro-carbamidomethyl", 5);
    modNumPair.put("C:Trioxidation", 6);
    modNumPair.put("C:Cam+O", 7);
    // De-fixed modi
    modNumPair.put("C:De-Carbamidomethyl", 8); // -57.021464
    
    //D
    modNumPair.put("D:Methyl" , 1);
    modNumPair.put("D:Dehydrated" , 2);
    modNumPair.put("D:Cation:Na", 3);
    //E
    modNumPair.put("E:Methyl", 1);
    modNumPair.put("E:Glu->pyro-Glu", 2);
    modNumPair.put("E:Cation:Na", 3);
    //H
    modNumPair.put("H:Methyl", 1);
    modNumPair.put("H:Carbamidomethyl", 2);
    //K
    modNumPair.put("K:Methyl", 1);
    modNumPair.put("K:Dimethyl", 2);
    modNumPair.put("K:Acetyl", 3);
    modNumPair.put("K:Carbamidomethyl", 4);
    //M
    modNumPair.put("M:Oxidation", 1);
    modNumPair.put("M:Dioxidation", 2);
    modNumPair.put("M:Dethiomethyl", 3);
    modNumPair.put("M:Acetyl+O", 4);
    //N
    modNumPair.put("N:Deamidated", 1);
    modNumPair.put("N:Ammonia-loss", 2);
    //Nt
    modNumPair.put("Nt:Carbon", 1);
    modNumPair.put("Nt:Formyl", 2); 
    modNumPair.put("Nt:Acetyl", 3); //acetyl/Nterm
    modNumPair.put("Nt:Carbamyl", 4);
    modNumPair.put("Nt:Carbamidomethyl", 5);
    //P
    modNumPair.put("P:Oxidation", 1);
    //Q
    modNumPair.put("Q:Deamidated", 1);
    modNumPair.put("Q:Gln->pyro-Glu", 2); // /Nterm can added
    //R
    modNumPair.put("R:Methyl", 1);
    modNumPair.put("R:Dimethyl", 2);
    //S
    modNumPair.put("S:Dehydrated", 1);
    modNumPair.put("S:Formyl", 2);
    modNumPair.put("S:Phospho", 3);
    //T
    modNumPair.put("T:Dehydrated", 1);
    modNumPair.put("T:Formyl", 2);
    modNumPair.put("T:Phospho", 3);
    //W
    modNumPair.put("W:Carbon", 1);
    modNumPair.put("W:Oxidation", 2);
    modNumPair.put("W:Trp->Kynurenin", 3);
    modNumPair.put("W:Dioxidation", 4);
    //Y
    modNumPair.put("Y:Phospho", 1);
    
    System.out.println("All pair setting completed");
    
    // Modplus File Read
    String modplusResultFile = args[0];
    BufferedReader br = new BufferedReader(new FileReader(modplusResultFile));
    
    String line = br.readLine(); //header 
    
    //mod position
    int c = 0;
    int d = 0;
    int e = 0;
    int h = 0;
    int k = 0;
    int m = 0;
    int n = 0;
    int nt = 0;
    int p = 0;
    int q = 0;
    int r = 0;
    int s = 0;
    int t = 0;
    int w = 0;
    int y = 0;
    
    while ((line = br.readLine()) != null) {
      
      c = 0;
      d = 0;
      e = 0;
      h = 0;
      k = 0;
      m = 0;
      n = 0;
      nt = 0;
      p = 0;
      q = 0;
      r = 0;
      s = 0;
      t = 0;
      w = 0;
      y = 0;
      
      String[] resultCols = line.split(MODPLUS_RESULT_DELIMETER);
      
      String[] mods = resultCols[10].split(" "); //modification column
      
      String modName = "";
      String modResidue = "";
      
      // e.g.) Phospho(S3)
      for (String mod : mods){
        modName = mod.split("\\(")[0];
        modResidue = mod.split("\\(")[1].substring(0, 1);
        
        // to exclude Gln->pyro-Glu/Nterm case.
        switch (modResidue) {
          case "C":
            c = modNumPair.get(modResidue + ":" + modName);
            break;
          case "D":
            d = modNumPair.get(modResidue + ":" + modName);
            break;
          case "E":
            e = modNumPair.get(modResidue + ":" + modName);
            break;
          case "K":
            k = modNumPair.get(modResidue + ":" + modName);
            break;
          case "M":
            m = modNumPair.get(modResidue + ":" + modName);
            break;
          case "N":
            n = modNumPair.get(modResidue + ":" + modName);
            break;
          case "P":
            p = modNumPair.get(modResidue + ":" + modName);
            break;
          case "Q":
            // if (modName.contains("/Nterm")){ //Gln->pyro-Glu/Nterm
            // q = modNumPair.get("Q:" + modName.split("/")[0]);
            // }
            // else{
            // q = modNumPair.get(modResidue + ":" + modName);
            // }
            q = modNumPair.get(modResidue + ":" + modName);
            break;
          case "R":
            r = modNumPair.get(modResidue + ":" + modName);
            break;
          case "S":
            s = modNumPair.get(modResidue + ":" + modName);
            break;
          case "T":
            t = modNumPair.get(modResidue + ":" + modName);
            break;
          case "W":
            w = modNumPair.get(modResidue + ":" + modName);
            break;
          case "Y":
            y = modNumPair.get(modResidue + ":" + modName);
            break;

          default:
            //can't reach here.... error will occur before this line
            if (!modNumPair.containsKey(modResidue + ":" + modName)) {
              System.err.println("No such hash key");
              System.err.println(modResidue + ":" + modName);
            }
       }
       
      }
      set[c][d][e][h][k][m][n][nt][p][q][r][s][t][w][y] += 1;
    } //end while
    
    // Count possible group set count
    int[][][][][][][][][][][][][][][] group = new int [C+1]
                                                      [D+1]
                                                      [E+1]
                                                      [H+1]
                                                      [K+1]
                                                      [M+1]
                                                      [N+1]
                                                      [Nt+1]
                                                      [P+1]
                                                      [Q+1]
                                                      [R+1]
                                                      [S+1]
                                                      [T+1]
                                                      [W+1]
                                                      [Y+1];
    
    System.out.println("group array assigned");
    
    for (c = 0 ; c < C + 1; c++) {
      for (d = 0; d < D + 1; d++) {
        for (e = 0; e < E + 1; e++) {
          for (h = 0; h < H + 1; h++) {
            for (k = 0; k < K + 1; k++) {
              for (m = 0; m < M + 1; m++) {
                for (n = 0; n < N + 1; n++) {
                  for (nt = 0; nt < Nt + 1; nt++) {
                    for (p = 0; p < P + 1; p++) {
                      for (q = 0; q < Q + 1; q++) {
                        for (r = 0; r < R + 1; r++) {
                          for (s = 0; s < S + 1; s++) {
                            for (t = 0; t < T + 1; t++) {
                              for (w = 0; w < W + 1; w++) {
                                for (y = 0; y < Y + 1; y++) {
                                  group[c][d][e][h][k][m][n][nt][p][q][r][s][t][w][y] = sumPossibleSet(set, c,d,e,h,k,m,n,nt,p,q,r,s,t,w,y);
                                } 
                              } 
                            } 
                          } 
                        } 
                      } 
                    } 
                  } 
                } 
              } 
            } 
          }
        }
      }
    }
    
    System.out.println("group calculated completed");
    
    // findMax! --> return each values of each columns
    BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
    
    int max = -1;
    
    int cMax = -1;
    int dMax = -1;
    int eMax = -1;
    int hMax = -1;
    int kMax = -1;
    int mMax = -1;
    int nMax = -1;
    int ntMax = -1;
    int pMax = -1;
    int qMax = -1;
    int rMax = -1;
    int sMax = -1;
    int tMax = -1;
    int wMax = -1;
    int yMax = -1;
    
    while ( true ){
      
      for (c = 0 ; c < C + 1; c++) {
        for (d = 0; d < D + 1; d++) {
          for (e = 0; e < E + 1; e++) {
            for (h = 0; h < H + 1; h++) {
              for (k = 0; k < K + 1; k++) {
                for (m = 0; m < M + 1; m++) {
                  for (n = 0; n < N + 1; n++) {
                    for (nt = 0; nt < Nt + 1; nt++) {
                      for (p = 0; p < P + 1; p++) {
                        for (q = 0; q < Q + 1; q++) {
                          for (r = 0; r < R + 1; r++) {
                            for (s = 0; s < S + 1; s++) {
                              for (t = 0; t < T + 1; t++) {
                                for (w = 0; w < W + 1; w++) {
                                  for (y = 0; y < Y + 1; y++) {
                                    
                                    if (group[c][d][e][h][k][m][n][nt][p][q][r][s][t][w][y] > max){
                                      max = group[c][d][e][h][k][m][n][nt][p][q][r][s][t][w][y];
                                      cMax = c;
                                      dMax = d;
                                      eMax = e;
                                      hMax = h;
                                      kMax = k;
                                      mMax = m;
                                      nMax = n;
                                      ntMax = nt;
                                      pMax = p;
                                      qMax = q;
                                      rMax = r;
                                      sMax = s;
                                      tMax = t;
                                      wMax = w;
                                      yMax = y;
                                    }
                                    
                                  } 
                                } 
                              } 
                            } 
                          } 
                        } 
                      } 
                    } 
                  } 
                } 
              } 
            }
          }
        }
      }
      
      bw.write(cMax +
          dMax +
          eMax +
          hMax +
          kMax +
          mMax +
          nMax +     
          ntMax+
          pMax +
          qMax +
          rMax +
          sMax +
          tMax +
          wMax +
          yMax);
      
      System.out.println(cMax + " " +
          dMax + " " +
          eMax + " " +
          hMax + " " +
          kMax + " " +
          mMax + " " +
          nMax + " " +    
          ntMax+ " " +
          pMax + " " +
          qMax + " " +
          rMax + " " +
          sMax + " " +
          tMax + " " +
          wMax + " " +
          yMax);
      
      System.out.println("max :" + max);
      
      // termination condition
      if ( max == 0){
        break;
      }
      
      max = 0;
      group[cMax][dMax][eMax][hMax][kMax][mMax][nMax][ntMax][pMax][qMax][rMax][sMax][tMax][wMax][yMax] = 0;
    }
    
    System.out.println("done");
  }
  private static int sumPossibleSet(int[][][][][][][][][][][][][][][] set, int c2, int d2, int e2,
      int h2, int k2, int m2, int n2, int nt2, int p2, int q2, int r2, int s2, int t2, int w2,
      int y2) {
    
    ArrayList<Integer> cc = new ArrayList<Integer>();
    ArrayList<Integer> dd = new ArrayList<Integer>();
    ArrayList<Integer> ee = new ArrayList<Integer>();
    ArrayList<Integer> hh = new ArrayList<Integer>();
    ArrayList<Integer> kk = new ArrayList<Integer>();
    ArrayList<Integer> mm = new ArrayList<Integer>();
    ArrayList<Integer> nn = new ArrayList<Integer>();
    ArrayList<Integer> ntnt = new ArrayList<Integer>();
    ArrayList<Integer> pp = new ArrayList<Integer>();
    ArrayList<Integer> qq = new ArrayList<Integer>();
    ArrayList<Integer> rr = new ArrayList<Integer>();
    ArrayList<Integer> ss = new ArrayList<Integer>();
    ArrayList<Integer> tt = new ArrayList<Integer>();
    ArrayList<Integer> ww = new ArrayList<Integer>();
    ArrayList<Integer> yy = new ArrayList<Integer>();
    
    cc.add(0);
    dd.add(0);
    ee.add(0);
    hh.add(0);
    kk.add(0);
    mm.add(0);
    nn.add(0);
    ntnt.add(0);
    pp.add(0);
    qq.add(0);
    rr.add(0);
    ss.add(0);
    tt.add(0);
    ww.add(0);
    yy.add(0);
    
    if (c2 != 0) { cc.add(c2); }
    if (d2 != 0) { dd.add(d2); }
    if (e2 != 0) { ee.add(e2); }
    if (h2 != 0) { hh.add(h2); }
    if (k2 != 0) { kk.add(k2); }
    if (m2 != 0) { mm.add(m2); }
    if (n2 != 0) { nn.add(n2); }
    if (nt2 != 0) { ntnt.add(nt2); }
    if (p2 != 0) { pp.add(p2); }
    if (q2 != 0) { qq.add(q2); }
    if (r2 != 0) { rr.add(r2); }
    if (s2 != 0) { ss.add(s2); }
    if (t2 != 0) { tt.add(t2); }
    if (w2 != 0) { ww.add(w2); }
    if (y2 != 0) { yy.add(y2); }
    
    int sum = 0;
    
    for (int c : cc){
      for (int d : dd){
        for (int e : ee){
          for (int h : hh){
            for (int k : kk){
              for (int m : mm){
                for (int n : nn){
                  for (int nt : ntnt){
                    for (int p : pp){
                      for (int q : qq){
                        for (int r : rr){
                          for (int s : ss){
                            for (int t : tt){
                              for (int w : ww){
                                for (int y : yy){
                                  sum += set[c][d][e][h][k][m][n][nt][p][q][r][s][t][w][y];
                                } 
                              } 
                            }
                          } 
                        } 
                      } 
                    } 
                  } 
                } 
              } 
            } 
          } 
        }
      }
    }
    
   
    return sum;
  }
}
