# app-Estoque-web
CRUD de produtos em estoque com foco no estudo de persistência de dados na web

### API Subida em localhost 
- Para utilizar a API executar:

```
java -jar server.jar
```

### Rotas disponíveis na API REST

GET /produto -> Devolve a lista com todos os produtos cadastrados:
```json
[
    {
        "id": 2,
        "nome": "Bola de volei",
        "preco": 59.99,
        "quantidade": 100
    },
    {
        "id": 3,
        "nome": "Bola de futebol",
        "preco": 89.99,
        "quantidade": 30
    }
]
```

POST /produto -> Adiciona um produto a lista com a seguinte estrutura:
```json
{
    "nome": "Bola de futebol",
    "preco": "89.99",
    "quantidade:": "10"
}
```

PUT /produto/{id} -> Edita o produto com a id selecionada

DELETE /produto/{id} -> Deleta o produto com a id selecionada
