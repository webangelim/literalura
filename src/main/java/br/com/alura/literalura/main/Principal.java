package br.com.alura.literalura.main;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosAutor;
import br.com.alura.literalura.model.DadosLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.IAutorRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;
import br.com.alura.literalura.repository.ILivroRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private static final String ENDERECO = "https://gutendex.com/books/?search=";
    private final Scanner sc = new Scanner(System.in);
    private final ILivroRepository livroRepository;
    private final IAutorRepository autorRepository;
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();
    private List<Livro> livros = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();

    public Principal(ILivroRepository livroRepository, IAutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        int run = -1;
        var menu = """
                \n
                ################ LiterAlura #################
                # 1 - Buscar livro por título               #
                # 2 - Listar livros cadastrados             #
                # 3 - Listar autores cadastrados            #
                # 4 - Listar autor vivo em determinado ano  #
                # 5 - Listar livros por idioma              #
                #                                           #
                # 0 - Sair                                  #
                #############################################
                """;
        while (run != 0) {
            System.out.println(menu);
            run = sc.nextInt();
            sc.nextLine();
            switch (run) {
                case 1:
                    buscarLivroPorTitulo();
                    break;
                case 2:
                    listarLivrosCadastrados();
                    break;
                case 3:
                    listarAutoresCadastrados();
                    break;
                case 4:
                    listarAutoresVivosEmDeterminadoAno();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Digite o idioma a ser buscado: ");
        var idioma = sc.nextLine();
        livros = livroRepository.findByIdiomaIgnoreCase(idioma);
        if (livros.isEmpty()) {
            System.out.println("Não há livros nesse idioma cadastrados no banco.");
        } else {
            for (Livro livro : livros) {
                System.out.println(livro);
            }
        }
    }

    private void listarAutoresVivosEmDeterminadoAno() {
        System.out.println("Digite o ano: ");
        Integer ano = sc.nextInt();
        autores = autorRepository.autorVivo(ano);
        if (autores.isEmpty()) {
            System.out.println("Não havia autores vivos no ano " + ano + " cadastrado no banco.");
        } else {
            for (Autor autor : autores) {
                System.out.println(autor);
            }
        }
    }

    private void listarAutoresCadastrados() {
        autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("Não há autores cadastrados no momento.");
        } else {
            for (Autor autor : autores) {
                System.out.println(autor);
            }
        }
    }

    private void buscarLivroPorTitulo() {
        System.out.println("Digite o nome do livro: ");
        var nomeLivro = sc.nextLine();
        var dados = consumoApi.obterDados(ENDERECO + nomeLivro.replace(" ", "%20"));
        salvarNoBanco(dados);
    }

    private void listarLivrosCadastrados() {
        livros = livroRepository.findAll();

        if (livros.isEmpty()) {
            System.out.println("Não há livros cadastrados no momento.");
        } else {
            for (Livro livro : livros) {
                System.out.println(livro);
            }
        }
    }

    private void salvarNoBanco(String dados) {
        try {
            Livro livro = new Livro(converteDados.obterDados(dados, DadosLivro.class));
            Autor autor = new Autor(converteDados.obterDados(dados, DadosAutor.class));
            Optional<Autor> bancoAutor = autorRepository.findByNome(autor.getNome());
            Optional<Livro> bancoLivro = livroRepository.findByTitulo(livro.getTitulo());
            if (bancoAutor.isEmpty()) {
                autorRepository.save(autor);
                bancoAutor = Optional.of(autor);
            }
            if (bancoLivro.isEmpty()) {
                livro.setAutor(bancoAutor.get());
                livroRepository.save(livro);
                bancoLivro = Optional.of(livro);
            }
            System.out.println(bancoLivro);
        } catch (NullPointerException e) {
            System.out.println("Livro não encontrado");
        }
    }
}
