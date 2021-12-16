package br.com.douglas.estoqueweb.retrofit.service;

import java.util.List;

import br.com.douglas.estoqueweb.model.Produto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ProdutoService {

    @GET("produto")
    Call<List<Produto>> buscaTodos();

    @POST("produto")
    Call<Produto> salva(@Body Produto produto);

}
