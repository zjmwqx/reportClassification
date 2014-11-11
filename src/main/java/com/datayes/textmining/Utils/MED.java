package com.datayes.textmining.Utils;

public class MED {
	
    public int min_edit_dic( String target ,String source){  
        int n = target.length();  
        int m = source.length();  
        int[][] distance = new int[ n+1 ][ m+1 ];  
        distance[ 0 ][ 0 ] = 0;  
        for ( int i = 1; i <= n; i++){  
            distance[ i ][ 0 ] = distance[ i-1 ][ 0 ] + ins_cost(target.charAt(i-1));  
        }  
        for ( int j = 1; j <= n; j++){  
            distance[ 0 ][ j ] = distance[ 0 ][ j-1 ] + ins_cost(target.charAt(j-1));  
        }  
        for ( int i = 1; i <= n; i++){  
            for ( int j = 1; j <= m; j++){  
                int ins = distance[ i-1 ][ j ] +ins_cost(target.charAt(i-1));  
                int sub = distance[ i-1 ][ j-1 ] + subs_cost(target.charAt(i-1),source.charAt(j-1));  
                int del = distance[ i ][ j -1 ] + del_cost(source.charAt(j-1));  
                distance[i][j] =  min( ins, min(sub,del));  
            }  
        }  
          
        for ( int i = 0; i <= n; i++){  
            for ( int j = 0; j <= m; j++){  
                System.out.print(distance[i][j]+"\t");  
            }  
            System.out.println();  
        }  
          
        return 1;  
    }  
      
    private int min(int d1, int d2){  
        return d1 < d2 ? d1: d2;  
    }  
      
    private int ins_cost(char c){  
        return 1;  
    }   
      
    private int del_cost(char c){  
        return 1;     
    }  
      
    private int subs_cost(char c1 , char c2){  
        return c1 != c2 ? 2 : 0;          
    } 
    public static void main(String[] args){  
        MED med = new MED();  
        med.min_edit_dic("abc", "cabc");  
    }  
}
