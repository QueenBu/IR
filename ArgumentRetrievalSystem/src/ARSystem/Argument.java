package ARSystem;

/**
 *
 * @author Maximilian Schmidt
 */
public class Argument implements Comparable<Argument> {

    private String id;
    private String title;
    private String sourceUrl;
    private String conclusion;
    private String summary;
    private double score;

    public Argument(String id, String title, String sourceUrl, String conclusion, String summary, double score) {
        this.id = id;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.conclusion = conclusion;
        this.summary = summary;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getConclusion() {
        return conclusion;
    }

    public String getSummary() {
        return summary;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Argument{" + "id=" + id + ", title=" + title + ", sourceUrl=" + sourceUrl + ", conclusion=" + conclusion + ", summary=" + summary + ", score=" + score + '}';
    }

    @Override
    public int compareTo(Argument argument) {
        return (Double.compare(this.getScore(), argument.getScore()));
    }

}
