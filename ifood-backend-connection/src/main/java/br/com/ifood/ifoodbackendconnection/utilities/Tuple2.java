package br.com.ifood.ifoodbackendconnection.utilities;

import lombok.Value;

@Value
public class Tuple2<K, V> {
    private K _1;
    private V _2;
}