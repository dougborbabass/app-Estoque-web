package br.com.douglas.estoqueweb.repository;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import br.com.douglas.estoqueweb.asynctask.BaseAsyncTask;
import br.com.douglas.estoqueweb.database.dao.ProdutoDAO;
import br.com.douglas.estoqueweb.model.Produto;
import br.com.douglas.estoqueweb.retrofit.EstoqueRetrofit;
import br.com.douglas.estoqueweb.retrofit.service.ProdutoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class ProdutoRepository {

    private final ProdutoDAO dao;
    private final ProdutoService service;

    public ProdutoRepository(ProdutoDAO dao) {
        this.dao = dao;
        service = new EstoqueRetrofit().getProdutoService();
    }

    public void buscaProdutos(DadosCarregadosCallBack<List<Produto>> callBack) {
        buscaProdutosInternos(callBack);
    }

    private void buscaProdutosInternos(DadosCarregadosCallBack<List<Produto>> callBack) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    callBack.quandoSucesso(resultado);
                    buscaProdutosNaAPI(callBack);
                }).execute();
    }

    private void buscaProdutosNaAPI(DadosCarregadosCallBack<List<Produto>> callBack) {
        Call<List<Produto>> call = service.buscaTodos();

        call.enqueue(new Callback<List<Produto>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<Produto>> call, Response<List<Produto>> response) {
                if (response.isSuccessful()) {
                    List<Produto> produtosNovos = response.body();
                    if (produtosNovos != null) {
                        atualizaInterno(produtosNovos, callBack);
                    } else {
                        callBack.quandoFalha("Resposta não sucedida");
                    }
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<Produto>> call, Throwable t) {
                callBack.quandoFalha("Falha de comunicação: " + t.getMessage());
            }
        });
    }

    private void atualizaInterno(List<Produto> produtosNovos, DadosCarregadosCallBack<List<Produto>> callBack) {
        new BaseAsyncTask<>(() -> {
            dao.salva(produtosNovos);
            return dao.buscaTodos();
        }, callBack::quandoSucesso).execute();
    }

    public void salva(Produto produto, DadosCarregadosCallBack<Produto> callBack) {
        salvaNaAPI(produto, callBack);
    }

    private void salvaNaAPI(Produto produto, DadosCarregadosCallBack<Produto> callBack) {
        Call<Produto> call = service.salva(produto);
        call.enqueue(new Callback<Produto>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Produto> call, Response<Produto> response) {
                if (response.isSuccessful()) {
                    Produto produtoSalvo = response.body();
                    salvaInterno(produtoSalvo, callBack);
                } else {
                    callBack.quandoFalha("Resposta não sucedida");
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Produto> call, Throwable t) {
                callBack.quandoFalha("Falha de comunicação: " + t.getMessage());
            }
        });
    }

    private void salvaInterno(Produto produto, DadosCarregadosCallBack<Produto> callBack) {
        new BaseAsyncTask<>(() -> {
            long id = dao.salva(produto);
            return dao.buscaProduto(id);
        }, callBack::quandoSucesso).execute();
    }

    public interface DadosCarregadosCallBack<T> {
        void quandoSucesso(T resultado);

        void quandoFalha(String erro);
    }

}
