package ARSystem;


import java.util.Collections;
import java.util.List;

/**
 * @author Bianca Mey
 * Anmerkung: finde das mit dem Autor hier nicht gut,
 * weil sooner oder later wir ja alle an allen Klassen mitschreiben werden
 */
public class Ranker {
    /**
     * ranks the dokuments by score, for now something very basic
     *
     * @param arrayList
     */
    public static List<Argument> rank(List<Argument> arrayList) {
        arrayList.sort(Collections.reverseOrder());
        return arrayList;
    }
}

