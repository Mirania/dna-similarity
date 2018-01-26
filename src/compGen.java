import java.io.File;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.FileNotFoundException;

public class compGen {
	//Bloom filter -> check if specie is in database
	private BloomFilter bf;
	
	//MinHash
	private TreeMap<String, String[]> lista = new TreeMap<String, String[]>();
	private TreeMap<String, Integer> tamanho = new TreeMap<String, Integer>();
	private int nFiles;
	private double[][] distances;
	private int hashed[][];
	private int nHash;
	private int div=5;
	
	
	//iniciar e ler ficheiros da base de dados
	public compGen(String file, int nHash,int div){
		this.div=div;
		this.nHash=nHash;
		this.nFiles=0;
		while(true){
			String filename = file+(nFiles+1);
			Scanner sc;
			try {
				File fin = new File(filename);
				sc = new Scanner(fin);
			} catch (FileNotFoundException e) {
				break;
			}
			nFiles++;
			//1 linha -> Nome da especie
			String key=sc.nextLine();
			tamanho.put(key, 0);
			//linhas do ficheiro
			whil:
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				//divide as linhas em partes iguais: shingles
				for(int part=1; part<=div;part++){
					if((((70/div)*(part)))<=line.length()){
						//inicio, ou continuacao
						if(lista.containsKey(key)){
							int size = lista.get(key).length;
							String[] atual = new String[size+1];
							for(int i=0;i<size;i++)
								atual[i]=lista.get(key)[i];
							atual[size]=line.substring((int)(70/div)*(part-1), (int)(70/div)*part);
							lista.put(key, atual);
						}else{
							String[] atual = new String[1];
							atual[0]=line.substring((int)(70/div)*(part-1), (int)(70/div)*part);
							lista.put(key, atual);
						}
						tamanho.put(key, tamanho.get(key)+(int)(70/div));
						
					}else{
						//ultima linha de tamanho nao standard
						tamanho.put(key, tamanho.get(key)+line.length());
						int size = lista.get(key).length;
						String[] atual = new String[size+1];
						for(int i=0;i<size;i++)
							atual[i]=lista.get(key)[i];
						String aux=line.substring((70/div)*(part-1), line.length());
						for(int a=aux.length(); a<(70/div);a++)
							aux=aux+"X";
						atual[size]=aux;
						lista.put(key, atual);
						
						if((70/div)*(part)>line.length()){
							break whil;
						}
					}
				}
				
						
			}
			sc.close();
		}
		
		//adicionar ao bloomFilter
		bf = new BloomFilter(5,nFiles*5*120);
		Object[] names = lista.keySet().toArray();
		int x;
		for(x=0; x<nFiles; x++){
			bf.add((String)(names[x]));
		}
		System.out.printf("%d ficheiros lidos com sucesso!\n",nFiles);
	}
	
	//procura no BloomFilter
	public boolean isInDatabase(String specie){
		return bf.isMember(specie);
	}
	
	//Calcular similaridade
	public void calcMinHash(){
		Object[] species = lista.keySet().toArray();
		int nSpecies=species.length;
		hashed = new int[nSpecies][nHash]; // #user - hashed movies
		
		int min=0;
		int val;
		int x;
		//hashing
		for(x=0; x<nSpecies; x++){
			String specie = (String)(species[x]);
			String[] dna=lista.get(specie);
			for (int k=0; k<nHash; k++) {
				for (int t=0; t<dna.length; t++) {			
					val = string2hash(""+k+dna[t],k);
					if (t==0) {min=val;};
					if (val<min) {min=val;};
				}
				hashed[x][k] = min;
			}
			//System.out.printf("%s: [", specie);
			//for(int s=0; s<hashed[x].length; s++)
				//System.out.printf(" %s", hashed[x][s]);
			//System.out.println(" ]");
		}
		
		//calculo das "distancias"
		distances=new double[nSpecies][nSpecies];
		for(x=0; x<nSpecies; x++){
			for(int y=x+1; y<nSpecies; y++){
				double inter=0;
				for(int i=0; i<nHash; i++){
					if (hashed[x][i] == hashed[y][i]) {
						inter++;
					}
				}
				distances[x][y] = (1-(inter/nHash));
				int tam1=tamanho.get((String)(species[x]));
				int tam2=tamanho.get((String)(species[x]));
				
				if(Math.abs((tam1-tam2)/tam1) > 0.1){
					distances[x][y]=1;
				}
				if(Math.abs((tam1-tam2)/tam2) > 0.1){
					distances[x][y]=1;
				}
			}
		}
	}
	
	//hashCode foi a melhor em termos de resultado/tempo
	static int string2hash(String str, int k)
	{   
		str=str+k;
	    return ((int) str.hashCode());
	}
	
	//imprime as especies semelhantes na BD 
	public void printCalc(double threshold){
		Object[] species = lista.keySet().toArray();
		int x;
		int res=0;
		for(x=0; x<species.length; x++){
			String user1 = (String)(species[x]);
			for(int y=x+1; y<species.length; y++){
				String user2 = (String)(species[y]);
				if(distances[x][y]<threshold){
					System.out.printf("'%s' e '%s': %.2f%%\n",user1,user2,(1.0-distances[x][y])*100.0);
					res++;
				}
			}
		}
		if(res==0)
			System.out.println("Nenhuma correspondência encontrada");
	}
	
	//imprime as especies semelhantes a escolhida
	public double printCalcFile(String filename, double threshold){
		Scanner sc;
		try {
			File fin = new File(filename);
			sc = new Scanner(fin);
		} catch (FileNotFoundException e) {
			return 0;
		}
		String[] novo = new String[0];
		int Localtamanho=0;
		whil2:
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			for(int part=1; part<div;part++){
				if((((70/div)*(part)))<=line.length()){
					
					if(novo.length>0){
						int size = novo.length;
						String[] atual = new String[size+1];
						for(int i=0;i<size;i++)
							atual[i]=novo[i];
						atual[size]=line.substring((int)(70/div)*(part-1), (int)(70/div)*part);
						novo=atual;
					}else{
						String[] atual = new String[1];
						atual[0]=line.substring((int)(70/div)*(part-1), (int)(70/div)*part);
						novo=atual;
					}
					Localtamanho=Localtamanho+(int)(70/div);
					
				}else{
					Localtamanho=Localtamanho+(int)(70/div);
					int size = novo.length;
					String[] atual = new String[size+1];
					for(int i=0;i<size;i++)
						atual[i]=novo[i];
					String aux=line.substring((70/div)*(part-1), line.length());
					for(int a=aux.length(); a<(70/div);a++)
						aux=aux+"X";
					atual[size]=aux;
					novo=atual;
					
					if((70/div)*(part)>line.length()){
						break whil2;
					}
				}
			}
		}
		sc.close();
		
		
		
		int[] Localhashed = new int[nHash];
		int val;
		int min=0;
		for (int k=0; k<nHash; k++) {
			for (int t=0; t<novo.length; t++) {			
				val = string2hash(""+k+novo[t],k);
				if (t==0) {min=val;};
				if (val<min) {min=val;};
			}
			Localhashed[k] = min;
		}
		//System.out.printf("File: [");
		//for(int s=0; s<Localhashed.length; s++)
			//System.out.printf(" %s", Localhashed[s]);
		//System.out.println(" ]");
		
		
		
		double[] Localdistances=new double[nFiles+1];
		Object[] users = tamanho.keySet().toArray();
		for(int x=0; x<nFiles; x++){
			double inter=0;
			for(int i=0; i<nHash; i++){
				if (hashed[x][i] == Localhashed[i]) {
					inter++;
				}
			}
			Localdistances[x] = (1-(inter/nHash));
				if(Math.abs(Localtamanho-tamanho.get(users[x]))/Localtamanho > 0.1){
					Localdistances[x]=1;
				}
			}
		
		
		
		Object[] species = lista.keySet().toArray();
		int x;
		int res=0;
		for(x=0; x<species.length; x++){
			String user = (String)(species[x]);
			if(Localdistances[x]<threshold){
				System.out.printf("'%s': %.2f%%\n",user,(1.0-Localdistances[x])*100.0);
				res++;
			}
		}
		if(res==0)
			System.out.println("Nenhuma correspondência encontrada");

		
		for(x=0; x<species.length; x++){
			String user = (String)(species[x]);
			if(user=="Homo sapiens" && Localdistances[x]<0.4){
				return (1.0-Localdistances[x])*100.0;
			}
		}
		return 0;
	}
	
	
	//imprime todas as especies da BD
	public void printCalc(){
		Object[] names = lista.keySet().toArray();
		int x;
		for(x=0; x<nFiles; x++){
			String name = (String)(names[x]);
			System.out.printf("Especie %d: %s\n",x+1,name);
		}
	}


}
