package br.com.douglas.estoqueweb.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.douglas.estoqueweb.model.Produto;

@Dao
public interface ProdutoDAO {

    @Insert
    long salva(Produto produto);

    @Update
    void atualiza(Produto produto);

    @Query("SELECT * FROM Produto")
    List<Produto> buscaTodos();

    @Query("SELECT * FROM Produto WHERE id = :id")
    Produto buscaProduto(long id);

    @Delete
    void remove(Produto produto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void salva(List<Produto> produtosNovos);
}
