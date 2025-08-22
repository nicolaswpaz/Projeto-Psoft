package com.ufcg.psoft.commerce.exception.itemcarteira;

public class ItemCarteiraNaoExisteException extends RuntimeException {
    public ItemCarteiraNaoExisteException() {super("O Ativo não foi encontrado na carteira/ ou não existe");}
}
