import java.io.*;
import java.util.*;

public class minHash {
	private TreeMap<Integer, int[]> lista = new TreeMap<Integer, int[]>();
	private double[][] distances;
	private int nUsers=0;
	

	public void readFile(String filename) throws FileNotFoundException{
		File fn = new File(filename);
		Scanner sc = new Scanner(fn);
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			Scanner split = new Scanner(line);
				int user=split.nextInt();

				int book=split.nextInt();
				split.nextInt();split.nextInt();					
				if(lista.containsKey(user)){
					int size = lista.get(user).length;
					int[] atual = new int[size+1];
					for(int i=0;i<size;i++)
						atual[i]=lista.get(user)[i];
					atual[size]=book;
					lista.put(user, atual);
				}else{
					int[] atual = new int[1];
					atual[0]=book;
					lista.put(user, atual);
					nUsers++;
				}
				
			split.close();
		}
		sc.close();	

	}
	
	public void calcJaccard(){
		distances=new double[nUsers][nUsers];
		Object[] users = lista.keySet().toArray();
		for(int x=0; x<nUsers; x++){
			int user1 = (int)(users[x]);
			double inter=0,uni=0;
			for(int y=x+1; y<nUsers; y++){
				int user2 = (int)(users[y]);
				inter=0;
				int[] books1=lista.get(user1);
				int[] books2=lista.get(user2);
				for(int i=0; i<books1.length; i++){
					for(int j=0; j<books2.length; j++){
						if( books1[i] == books2[j] )
							inter++;
					}
				}
	
				uni=books1.length+books2.length-inter;				
				distances[x][y]=1-(inter/uni);
				
			}
	
		}
	}
	
	
	/*
	static int string2hash(String str, int k)
	{   k=k%15;
		int[] caso  = {47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109};
		int[] casoInit = {7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61};
	    int hash = casoInit[k];
	    int c;
	    for(int i=0; i<str.length(); i++){
	    	c =(int) str.charAt(i);
	        hash = hash*31 + c;
	    }
	    return Math.abs(hash);
	}
	
	*/
	
	static int string2hash(String str, int k)
	{   
		str=str+k;
	    return ((int) str.hashCode());
	}
	
	
	
	public void calcminHash(int nkey) { // #hash functions		
		Object[] users = lista.keySet().toArray();
		int[][] hashed=new int[nUsers][nkey]; // #user - hashed movies
		
		int min=0;
		int val;
		int x;
		for(x=0; x<nUsers; x++){
			int user = (int)(users[x]);
			int[] books=lista.get(user);
			for (int k=0; k<nkey; k++) {
				for (int t=0; t<books.length; t++) {			
					val = string2hash(""+k+books[t],k);
					if (t==0) {min=val;};
					if (val<min) {min=val;};
				}
				hashed[x][k] = min;
			}
		}
		//fim do calculo do minhash
		//calculo das distancias
		distances=new double[nUsers][nUsers];
		for(x=0; x<nUsers; x++){
			for(int y=x+1; y<nUsers; y++){
				double inter=0;
				for(int i=0; i<nkey; i++){
					if (hashed[x][i] == hashed[y][i]) {
						inter++;
					}
				}
				distances[x][y] = (1-(inter/nkey))*100.0;
				/*
				if(1-(inter/nkey) < 0.4){
					System.out.printf("User %d: [",(int)(users[x]));
					for(int p=0; p<hashed[x].length;p++){
					System.out.printf(" %"
							+ "d",hashed[x][p]);
					}
					System.out.print(" ]\n");
					System.out.printf("User %d: [",(int)(users[y]));
					for(int p=0; p<hashed[y].length;p++){
						System.out.printf(" %d",hashed[y][p]);
						}
					System.out.print(" ]\n\n");
					//System.out.printf("%f %d: %f\n", inter,nkey,1-(inter/nkey));
				}
				*/
			}
		}
	}
	
	
	public void printFile(){
		Iterator itr = lista.keySet().iterator();
	    while(itr.hasNext()){
	    	int user = (int) itr.next();
	    	System.out.printf("User %d: [",user);
	    	int[] books = lista.get(user); 
	    	for(int i=0; i<books.length; i++){
	    		System.out.printf(" %d",books[i]);
	    	}
	    	System.out.printf(" ]\n");
	    }
	}
	
	public void printCalc(double threshold){
		Object[] users = lista.keySet().toArray();
		int x;
		int res=0;
		for(x=0; x<nUsers; x++){
			int user1 = (int)(users[x]);
			for(int y=x+1; y<nUsers; y++){
				int user2 = (int)(users[y]);
				if(distances[x][y]<threshold){
					System.out.printf("%d - %d: %.3f\n",user1,user2,distances[x][y]);
					res++;
				}
			}
		}
		System.out.printf("Total: %d\n",res);
	}
	
	
}
