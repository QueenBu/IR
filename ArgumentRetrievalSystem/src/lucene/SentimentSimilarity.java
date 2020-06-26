package lucene;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.util.SmallFloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SentimentSimilarity extends Similarity {

    @Override
    public String toString() {
        return "TestSimilarity";
    }

    // ------------------------------ Norm handling ------------------------------

    /**
     * Cache of decoded bytes.
     */
    private static final int[] LENGTH_TABLE = new int[ 256 ];

    static {
        for ( int i = 0; i < 256; i++ ) {
            LENGTH_TABLE[ i ] = SmallFloat.byte4ToInt((byte) i);
        }
    }

    /**
     * True if overlap tokens (tokens with a position of increment of zero) are
     * discounted from the document's length. (copied from {@link SimilarityBase}
     */
    boolean discountOverlaps = true;

    /**
     * Encodes the document length in the same way as {@link BM25Similarity}.
     */
    @Override
    public final long computeNorm(FieldInvertState state) {

        final int numTerms;
        if ( state.getIndexOptions() == IndexOptions.DOCS && state.getIndexCreatedVersionMajor() >= 8 ) {
            numTerms = state.getUniqueTermCount();
        } else if ( discountOverlaps ) {
            numTerms = state.getLength() - state.getNumOverlap();
        } else {
            numTerms = state.getLength();
        }
        return SmallFloat.intToByte4(numTerms);
    }

    /**
     * wie wichtig ist eine Menge an Termen fuer ein Dokument
     *
     * @param boost           variabler Faktor um Wert eines Dokuments zu steigern
     * @param collectionStats Informationen über alle Dokumente in der Sammlung
     * @param termStats       Menge von: Informationen zu einem Term bezüglich ALLER Dokumente
     * @return SimScorer, welcher dann (intern) nur noch die Häufigkeit des Terms in EINEM Dokument und den über
     * {@link SentimentSimilarity#computeNorm} berechneten Normwert erhält und dann einen Score für ein Dokument zu
     * der Suchanfrage (Menge an Termen) gibt
     */
    @Override
    public final SimScorer scorer(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
        SimScorer[] weights = new SimScorer[ termStats.length ];

        for ( int i = 0; i < termStats.length; i++ ) {

            BasicStats stats = newStats(collectionStats.field(), boost);
            fillFullStats(stats, collectionStats, termStats[ i ]);
            weights[ i ] = new TestSimScorer(stats);
        }
        if ( weights.length == 1 ) {
            return weights[ 0 ];
        } else {
            return new MultiSimScorer(weights);
        }
    }


    /**
     * Factory method to return a custom stats object
     */
    protected BasicStats newStats(String field, double boost) {
        return new BasicStats(field, boost);
    }

    /**
     * Fills all member fields defined in {@link BasicStats} in {@code stats}.
     */
    protected void fillFullStats(BasicStats stats, CollectionStatistics collectionStats, TermStatistics termStats) {

        assert termStats.totalTermFreq() <= collectionStats.sumTotalTermFreq();
        assert termStats.docFreq() <= collectionStats.sumDocFreq();

        stats.setNumberOfDocuments(collectionStats.docCount());
        stats.setNumberOfFieldTokens(collectionStats.sumTotalTermFreq());
        stats.setAvgFieldLength(collectionStats.sumTotalTermFreq() / (double) collectionStats.docCount());
        stats.setDocFreq(termStats.docFreq());
        stats.setTotalTermFreq(termStats.totalTermFreq());
    }


    static final class TestSimScorer extends SimScorer {

        final BasicStats stats;

        int decodeNorm(long norm) {
            return LENGTH_TABLE[ Byte.toUnsignedInt((byte) norm) ];
        }

        TestSimScorer(BasicStats stats) {
            this.stats = stats;
        }

        protected float idf() {
            return (float) Math.log((stats.getNumberOfDocuments() + 1.0f) / stats.getDocFreq());
        }

        protected float tf(float freq, int docLen) {
            return freq / docLen;
        }

        @Override
        public float score(float freq, long encodedNorm) {
            return Math.max(0.0f, (float) stats.getBoost() * tf(freq, decodeNorm(encodedNorm)) * idf());
        }

        @Override
        public Explanation explain(Explanation freq, long norm) {
            return Explanation.match(this.score(freq.getValue().floatValue(), norm),
                    "Math.max(0.0f, " + stats.getBoost() + " * " + tf(freq.getValue().floatValue(), decodeNorm(norm)) + " * " + idf() + "), with freq of:", Collections.singleton(freq));
        }
    }

    /**
     * copied from org.apache.lucene.search.similarities.MultiSimilarity
     */
    static class MultiSimScorer extends SimScorer {
        private final SimScorer[] subScorers;

        MultiSimScorer(SimScorer[] subScorers) {
            this.subScorers = subScorers;
        }

        @Override
        public float score(float freq, long norm) {
            float sum = 0.0f;
            for ( SimScorer subScorer : subScorers ) {
                sum += subScorer.score(freq, norm);
            }
            return sum;
        }

        @Override
        public Explanation explain(Explanation freq, long norm) {
            List<Explanation> subs = new ArrayList<>();
            for ( SimScorer subScorer : subScorers ) {
                subs.add(subScorer.explain(freq, norm));
            }
            return Explanation.match(score(freq.getValue().floatValue(), norm), "sum of:", subs);
        }
    }
}
