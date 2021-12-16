package br.com.douglas.estoqueweb.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.douglas.estoqueweb.R;
import br.com.douglas.estoqueweb.model.Produto;
import br.com.douglas.estoqueweb.repository.ProdutoRepository;
import br.com.douglas.estoqueweb.ui.adapter.ListaProdutosAdapter;
import br.com.douglas.estoqueweb.ui.dialog.EditaProdutoDialog;
import br.com.douglas.estoqueweb.ui.dialog.SalvaProdutoDialog;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String NAO_FOI_POSSIVEL_EDITAR_O_PRODUTO = "Não foi possível editar o produto";
    private static final String NAO_FOI_POSSIVEL_SALVAR_O_PRODUTO = "Não foi possível salvar o produto";
    private static final String NAO_FOI_POSSIVEL_REMOVER_O_PRODUTO = "Não foi possível remover o produto";
    public static final String NAO_FOI_POSSIVEL_CARREGAR_OS_PRODUTOS_NOVOS = "Não foi possível carregar os produtos novos";
    private ListaProdutosAdapter adapter;
    ProdutoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        repository = new ProdutoRepository(this);
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallBack<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> resultado) {
                adapter.atualiza(resultado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_CARREGAR_OS_PRODUTOS_NOVOS);
            }
        });
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao, Produto produtoEscolhido) {
        repository.remove(produtoEscolhido, new ProdutoRepository.DadosCarregadosCallBack<Void>() {
            @Override
            public void quandoSucesso(Void resultado) {
                adapter.remove(posicao);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_REMOVER_O_PRODUTO);
            }
        });
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, this::salva).mostra();
    }

    private void salva(Produto produtoCriado) {
        repository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                adapter.adiciona(resultado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_SALVAR_O_PRODUTO);
            }
        });
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> edita(posicao, produtoCriado))
                .mostra();
    }

    private void edita(int posicao, Produto produtoCriado) {
        repository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
            @Override
            public void quandoSucesso(Produto produtoEditado) {
                adapter.edita(posicao, produtoEditado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(NAO_FOI_POSSIVEL_EDITAR_O_PRODUTO);
            }
        });
    }

    private void mostraErro(String msg) {
        Toast.makeText(ListaProdutosActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
