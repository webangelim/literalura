package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ILivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByTitulo(String nomeLivro);


}
