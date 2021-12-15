package br.com.douglas.estoqueweb.database.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

public class BigDecimalConverter {

    @TypeConverter
    public Double paraDouble(BigDecimal valor) {
        return valor.doubleValue();
    }

    @TypeConverter
    public BigDecimal paraBigDecimal(Double valor) {
        if (valor != null) {
            return new BigDecimal(valor);
        }
        return BigDecimal.ZERO;
    }

}
