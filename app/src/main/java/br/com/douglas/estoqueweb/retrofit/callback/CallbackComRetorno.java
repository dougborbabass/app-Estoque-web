package br.com.douglas.estoqueweb.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

import static br.com.douglas.estoqueweb.retrofit.callback.MensagensCallback.FALHA_DE_COMUNICACAO;
import static br.com.douglas.estoqueweb.retrofit.callback.MensagensCallback.RESPOSTA_NAO_SUCEDIDA;

public class CallbackComRetorno<T> implements Callback<T> {


    private final RespostaCallBack<T> callBack;

    public CallbackComRetorno(RespostaCallBack<T> callBack) {
        this.callBack = callBack;
    }


    @Override
    @EverythingIsNonNull
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T resultado = response.body();
            if (resultado != null) {
                callBack.quandoSucesso(resultado);
            } else {
                callBack.quandoFalha(RESPOSTA_NAO_SUCEDIDA);
            }
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<T> call, Throwable t) {
        callBack.quandoFalha(FALHA_DE_COMUNICACAO + t.getMessage());
    }

    public interface RespostaCallBack<T> {
        void quandoSucesso(T resultado);

        void quandoFalha(String erro);
    }
}
