package com.normalizar.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraficaData {
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private List<String> rangosEjeY; // Asumiendo que son Strings con el formato "X,XXXX"
    private List<Integer> rangosEjeX;

    private List<Double> puntosGrficar;


}
