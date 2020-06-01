package argumentretrivalsystem;

/**
 * @author Bianca Mey
 * Anmerkung: finde das mit dem Autor hier nicht gut,
 * weil sooner oder later wir ja alle an allen Klassen mitschreiben werden
 */

/**
 * ranks the dokuments by score, for now something very basic
 * @param arrayList
 */
public class Ranker {
    public static ArrayList<Argument> rank(ArrayList<Argument> arrayList){
        Collections.sort(arrayList);
        return arrayList;
    }
}

