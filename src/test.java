import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class test {
	static compGen cg;

	public static void main(String[] args) {
		int aux=0;
		while(menu()==0){
			System.out.println("\n\n\n");
		}
	System.out.println("Programa terminado...");
	}
	
	static int menu(){
		System.out.println("a) Carregar BD");
		System.out.println("b) ReHash BD");
		System.out.println("c) Verificar existencia na BD");
		System.out.println("d) Comparar especies na BD");
		System.out.println("e) Comparar nova especie com as da BD");
		System.out.println("x) Sair");
		System.out.print("Selecione uma das opções: ");
		Scanner sc = new Scanner(System.in);
		switch (sc.nextLine().toLowerCase()){
			case "a":
				System.out.print("Caminho e prefixo dos ficheiros: ");
				String files = sc.nextLine();
				cg = new compGen(files, 540,10);
			case "b":
				cg.calcMinHash();
				System.out.printf("Hash's geradas com sucesso!\n");
				break;
			case "c":
				System.out.print("Espécie a procurar: ");
				String specie = sc.nextLine();
				System.out.println(
					cg.isInDatabase(specie)
					?
					"A espécie encontra-se na base de dados!"	
					:
					"A espécie não se encontra disponível!"
				);
				break;
			case "d":
				cg.printCalc(0.4);
				break;
			case "e":
				System.out.print("Caminho e nome do ficheiro: ");
				String file = sc.nextLine();
				cg.printCalcFile(file,0.4);
				break;
			case "x":
				return 1;
			default:
				System.out.println("Opção inválida!");
		}
		return 0;
	}

}
