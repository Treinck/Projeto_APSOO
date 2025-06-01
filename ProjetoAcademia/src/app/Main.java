package app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        AcessoAluno AA = new AcessoAluno();

        while(true) {
            System.out.print("Deseja adicionar ou Buscar entre os alunos ja adicionados?\nUse 1 para Adicionar e 2 para Buscar (ou ENTER para sair): ");
            String entrada = scanner.nextLine();

            if(entrada.equals("1")){
            	//pedir nome do aluno
            	System.out.println("E qual o nome do aluno: ");
            	String nome = scanner.nextLine();
            	
            	System.out.println("Telefone: ");
            	String tel = scanner.nextLine();

            	System.out.println("Telefone de Emergência: ");
            	String telE = scanner.nextLine();
            	
            	System.out.println("Treino que realiza: ");
            	String tipoT = scanner.nextLine();
            	
            	 // Pede dados do endereço
                System.out.print("Rua: ");
                String rua = scanner.nextLine();

                System.out.print("Número: ");
                String numero = scanner.nextLine();

                System.out.print("Bairro: ");
                String bairro = scanner.nextLine();

                System.out.print("CEP: ");
                String cep = scanner.nextLine();

                System.out.print("Complemento: ");
                String complemento = scanner.nextLine();
                
             // Cria os objetos Aluno e Endereco
                Endereco endereco = new Endereco(rua, numero, bairro, cep, complemento);
                Aluno aluno = new Aluno(nome, tel, telE, tipoT, endereco);

                int x = AA.buscarEndereco(endereco);
                if(x == -1) {
                	System.out.println("\nO Aluno não possui um endereço registrado, adicionei o endereço e o aluno!");
                	try {
                		AA.inserirAlunoComEndereco(aluno, endereco);
                    } catch (SQLException e) {
                    	System.out.println("Erro ao inserir aluno e endereço: " + e.getMessage());
                        e.printStackTrace();
                    }
                }else {
                	System.out.println("\nO Aluno possui um endereço registrado, adicionei-o ao endereço!");
                	AA.inserirAluno(aluno, x);
                }
            }else if(entrada.equals("2")){
            	while(true) {
            		 System.out.print("\nDigite o nome do aluno (ou ENTER para sair): ");
                     String busca = scanner.nextLine();

                     if(busca.isBlank()) {
                     	break;
                     }
            		List<Aluno> resultados = AA.buscarPorNome(busca);

            		if(resultados.isEmpty()){
            			System.out.println("Nenhum aluno encontrado.");
            		}else{
            			System.out.println("Resultados:");
            			resultados.forEach(System.out::println);
            		}
            	}
            }else if(entrada.equals("3")){
            	System.out.println("\nE qual o id do aluno: ");
            	int id = scanner.nextInt();  
            	scanner.nextLine();
            	AA.removerAluno(id);
            	
            }else if(entrada.isBlank()) {
            	break;
            }System.out.println("\n");
        }
        scanner.close();
        System.out.println("Programa encerrado.");
    }
}
