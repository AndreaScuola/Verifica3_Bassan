package org.example.modelli;

import java.util.List;

public class Zoo {
    private List<Animale> animali;

    public List<Animale> getAnimali() {return animali;}

    @Override
    public String toString(){
        String msg = "Zoo: ";
        for(Animale a : animali)
            msg += "\n-" + a.toString();

        return msg;
    }
}
