package br.com.douglas.estoqueweb.repository;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

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

    public void buscaProdutos(DadosCarregadosListener<List<Produto>> listener) {
        buscaProdutosInternos(listener);
    }

    private void buscaProdutosInternos(DadosCarregadosListener<List<Produto>> listener) {
        new BaseAsyncTask<>(dao::buscaTodos,
                resultado -> {
                    listener.quandoCarregados(resultado);
                    buscaProdutosNaAPI(listener);
                }).execute();
    }

    private void buscaProdutosNaAPI(DadosCarregadosListener<List<Produto>> listener) {

        Call<List<Produto>> call = service.buscaTodos();
        new BaseAsyncTask<>(() -> {
            try {
                Response<List<Produto>> resposta = call.execute();
                List<Produto> produtosNovos = resposta.body();
                dao.salva(produtosNovos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dao.buscaTodos();
        }, listener::quandoCarregados)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public interface DadosCarregadosListener<T> {
        void quandoCarregados(T resultado);
    }

    public interface DadosCarregadosCallBack<T> {
        void quandoSucesso(T resultado);
        void quandoFalha(String erro);
    }

}
