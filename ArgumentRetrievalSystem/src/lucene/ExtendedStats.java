package lucene;

import org.apache.lucene.search.similarities.BasicStats;

public class ExtendedStats extends BasicStats {

    private float relativeSentiment;

    public ExtendedStats(String field, double boost) {
        super(field, boost);
    }

    public void setRelativeSentiment(float relativeSentiment) {
        this.relativeSentiment = relativeSentiment;
    }

    public float getRelativeSentiment() {
        return relativeSentiment;
    }
}
