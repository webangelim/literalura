package br.com.alura.literalura;

import br.com.alura.literalura.main.Principal;
import br.com.alura.literalura.repository.IAutorRepository;
import br.com.alura.literalura.repository.ILivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	private final ILivroRepository livroRepository;
	private final IAutorRepository autorRepository;

	@Autowired
	public LiteraluraApplication(ILivroRepository livroRepository, IAutorRepository autorRepository) {
		this.livroRepository = livroRepository;
		this.autorRepository = autorRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Principal principal = new Principal(livroRepository, autorRepository);
		principal.exibeMenu();
	}
}
