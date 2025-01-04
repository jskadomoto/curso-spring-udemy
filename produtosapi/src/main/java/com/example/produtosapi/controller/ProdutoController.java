package com.example.produtosapi.controller;

import com.example.produtosapi.model.Produto;
import com.example.produtosapi.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("produtos")
public class ProdutoController {

    private ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public Produto salvar(@RequestBody Produto produto) {
        System.out.println("Produto recebido: " + produto);
        var id = UUID.randomUUID().toString();
        produto.setId(id);
        produtoRepository.save(produto);
        return produto;
    }

    @GetMapping("/{id}")
    public Produto obterPorId(@PathVariable("id") String id) {
        return produtoRepository.findById((id)).orElse(null);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletarPorId(@PathVariable("id") String id) {
        // Verificar se o produto existe
        Optional<Produto> produtoOptional = produtoRepository.findById(id);

        if (produtoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto não encontrado");
        }

        Produto produto = produtoOptional.get();
        try {
            // Tentativa de deletar o produto
            produtoRepository.deleteById(id);
            return ResponseEntity.ok(
                    String.format("Produto '%s' com ID '%s' deletado com sucesso", produto.getNome(), produto.getId()));
        } catch (Exception e) {
            // Caso ocorra algum erro na exclusão
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Erro ao tentar excluir o produto '%s' com ID '%s'", produto.getNome(), produto.getId()));
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<String> atualizarProduto(@PathVariable("id") String id, @RequestBody Produto produtoAtualizado) {
        // Verificar se o produto existe
        Optional<Produto> produtoOptional = produtoRepository.findById(id);

        if (produtoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto não encontrado");
        }

        Produto produtoExistente = produtoOptional.get();
        try {
            // Atualizar os dados do produto existente com os novos valores
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            // Adicione outros campos conforme necessário

            // Salvar o produto atualizado
            produtoRepository.save(produtoExistente);

            return ResponseEntity.ok(
                    String.format("Produto '%s' com ID '%s' atualizado com sucesso", produtoExistente.getNome(), produtoExistente.getId()));
        } catch (Exception e) {
            // Caso ocorra algum erro durante a atualização
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Erro ao tentar atualizar o produto '%s' com ID '%s'", produtoExistente.getNome(), produtoExistente.getId()));
        }
    }

    @GetMapping
    public List<Produto> buscar(@RequestParam("nome") String nome){
        return produtoRepository.findByNome(nome);
    }
}
