package br.com.douglas.estoqueweb.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.douglas.estoqueweb.R;
import br.com.douglas.estoqueweb.database.EstoqueDatabase;
import br.com.douglas.estoqueweb.database.dao.ProdutoDAO;
import br.com.douglas.estoqueweb.model.Produto;
import br.com.douglas.estoqueweb.repository.ProdutoRepository;
import br.com.douglas.estoqueweb.ui.adapter.ListaProdutosAdapter;
import br.com.douglas.estoqueweb.ui.dialog.EditaProdutoDialog;
import br.com.douglas.estoqueweb.ui.dialog.SalvaProdutoDialog;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private ListaProdutosAdapter adapter;
    private ProdutoDAO dao;
    ProdutoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        EstoqueDatabase db = EstoqueDatabase.getInstance(this);
        dao = db.getProdutoDAO();

        repository = new ProdutoRepository(dao);
        repository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallBack<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> resultado) {
                adapter.atualiza(resultado);
            }

            @Override
            public void quandoFalha(String erro) {
                Toast.makeText(ListaProdutosActivity.this, "Não foi possível carregar os produtos novos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener((posicao, produtoEscolhido) -> {
            repository.remove(produtoEscolhido, new ProdutoRepository.DadosCarregadosCallBack<Void>() {
                @Override
                public void quandoSucesso(Void resultado) {
                    adapter.remove(posicao);
                }

                @Override
                public void quandoFalha(String erro) {
                    Toast.makeText(ListaProdutosActivity.this, "Não foi possível remover o produto", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado ->
                repository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
                    @Override
                    public void quandoSucesso(Produto resultado) {
                        adapter.adiciona(resultado);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        Toast.makeText(ListaProdutosActivity.this, "Não foi possível salvar o produto", Toast.LENGTH_SHORT).show();
                    }
                }))
                .mostra();
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> repository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallBack<Produto>() {
                    @Override
                    public void quandoSucesso(Produto produtoEditado) {
                        adapter.edita(posicao, produtoEditado);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        Toast.makeText(ListaProdutosActivity.this, "Não foi possível editar o produto", Toast.LENGTH_SHORT).show();
                    }
                }))
                .mostra();
    }
}
