package com.zuliaworks.zusaldo.eventos;

public interface EscuchaEvento<T> {
    void eventoDisparado(T e);
}
