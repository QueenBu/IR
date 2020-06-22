package lucene;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class TestSimilarity extends SimilarityBase {

    protected double idf(BasicStats stats) {
        return Math.log(((double) stats.getNumberOfDocuments() + 1.0D) / (double) stats.getDocFreq());
    }

    protected double tf(double freq, double docLen) {
        return freq / docLen;
    }

    @Override
    protected double score(BasicStats basicStats, double freq, double docLen) {
        return Math.max(0.0D, basicStats.getBoost() * tf(freq, docLen) * idf(basicStats));
    }

    @Override
    public String toString() {
        return "TestSimilarity";
    }

}
