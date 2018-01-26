import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class testNHash {
	static compGen cg;

	public static void main(String[] args) {
		
		double max=0;
		int maxHash=0;
		int maxShingle=0;
		
		//int[] shingles = {1,2,5,7,10,14,35,70};
		//1, 2 -> nenhum resultado; 35, 70 -> todos os resutados 
		int[] shingles = {5,7,10,14};
		int nHashs=500;
		while(nHashs<600){
			
			
			for(int i=0; i<shingles.length;i++){
				
				double value=trigger(nHashs, shingles[i]);
				if(value>max){
					max=value;
					maxHash=nHashs;
					maxShingle=shingles[i];
				}
				System.out.println("");
			}
			nHashs=nHashs+20;
			System.out.println("\n\n");
		}
	System.out.println("Programa terminado...");
	System.out.printf("\nBetter Choice: %d Hash's; %d chars Shingles!\n",maxHash,maxShingle);
	}
	//535
	
	static double trigger(int nHashs, int shingles){
		System.out.printf("nHash: %d\tShingleSize: %d\n",nHashs,shingles);
		cg = new compGen("Genomas/gen", nHashs,shingles);
		cg.calcMinHash();
		return cg.printCalcFile("Genomas/teste3",0.4);
	}

}
