
public class BloomFilter {
	public int array[];
	private int nHash;
	
	public BloomFilter(int nHashs, int size){
		this.nHash=nHashs;
		this.array = new int[size];
	}
	
	public void add(String input){
		for(int i=0; i<input.length();i++){
			StringBuilder sb = new StringBuilder(2);
			sb.append(input); sb.append(i);
			int index = string2hash(sb.toString(),i);
			array[index]=1;
		}
	}
	public void add(int input){add(""+input);}
	
	public boolean isMember(String input){
		int result=0;
		for(int i=0; i<this.nHash;i++){
			StringBuilder sb = new StringBuilder(2);
			sb.append(input); sb.append(i);
			int index = string2hash(sb.toString(),i);
			result+=array[index];
		}
		return result==this.nHash;
	}
	
	public boolean isMember(int input){return isMember(""+input);}

	public int string2hash(String input){
		input =input.replaceAll("\\s+","");
        int str[] = new int[input.length()];
        for(int i=0; i<input.length();i++){
        		str[i]=(int) input.charAt(i);
        }
        
        double hash=0;
        for(int i=0; i<input.length();i++){
            hash = (hash * 65599 + str[i]) % (2^32-1);
        }
        
        int result = (int) (hash % this.array.length);
		return result;
	}
	
	private int string2hash(String str, int k)
	{   
		str=str+k;
	    int ind = ((int) str.hashCode() ) % this.array.length;
	    if(ind<0)
	    	ind=this.array.length+ind;
	    return ind;
	}
	

}