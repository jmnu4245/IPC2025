package ourModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pablo
 */
public class PreguntasHechas {
    private static PreguntasHechas instance;
    private final List<Integer> preguntasContestadas;

    private PreguntasHechas() {
        preguntasContestadas = new ArrayList<>();
    }

    public static PreguntasHechas getInstance() {
        if (instance == null) {
            instance = new PreguntasHechas();
        }
        return instance;
    }

    // Obtienes la lista
    public List<Integer> getPreguntasHechas() {
        return preguntasContestadas;
    }

    // Añadir a la lista
    public void agregarPregunta(int nPregunta) {
        preguntasContestadas.add(nPregunta);
    }

    // Vaciar la lista
    public void resetear() {
        preguntasContestadas.clear();
    }
}
