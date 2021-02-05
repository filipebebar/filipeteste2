package controller;

import model.Pessoa;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repository.PessoaRepository;
import utilidades.Utilidades;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaRepository repository;

    @PostMapping("/")
    public Pessoa adicionar(@RequestBody Pessoa pessoa) {
        Date data = new Date();

        if (Utilidades.isCpfValido(pessoa.getCpf())
                && !pessoa.getContatos().isEmpty()
                && pessoa.getDataNascimento().after(data)) {
            pessoa.getContatos().forEach(c -> c.setPessoa(pessoa));

            return repository.save(pessoa);
        }
        return pessoa;
    }

    @GetMapping("/all")
    public List<Pessoa> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> buscar(@PathVariable(name = "id") Long id) {
        Pessoa pessoa = repository.getOne(id);

        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pessoa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable(name = "id") Long id, @RequestBody Pessoa pessoa) {
        Pessoa existente = repository.getOne(id);

        existente.getContatos().clear();
        existente.getContatos().addAll(pessoa.getContatos());

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        BeanUtils.copyProperties(pessoa, existente, "id");

        existente = repository.save(existente);

        return ResponseEntity.ok(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable(name = "id") Long id) {
        Pessoa pessoa = repository.getOne(id);

        if (pessoa == null) {
            return ResponseEntity.notFound().build();
        }

        repository.delete(pessoa);

        return ResponseEntity.noContent().build();
    }


}
