package ic.comparison.operators;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ic.comparison.ComparisonStats;
import ic.comparison.ComparisonUtility;
import ic.comparison.CompatibilityMap;
import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import ic.comparison.TupleMatches;
import static ic.utility.ICUtility.getValueEncoder;
import speedy.model.database.IDatabase;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeInstanceSimilarityBruteForceCompatibility implements IComputeInstanceSimilarity {

    private final static Logger logger = LoggerFactory.getLogger(ComputeInstanceSimilarityBruteForceCompatibility.class);

    private final FindCompatibleTuples compatibleTupleFinder = new FindCompatibleTuples();
    private final FindBestTupleMapping bestTupleMappingFinder = new FindBestTupleMapping();
    private final FindNonMatchingTuples nonMatchingTuplesFinder = new FindNonMatchingTuples();

    @Override
    public InstanceMatchTask compare(IDatabase leftDb, IDatabase rightDb) {
        long start = System.currentTimeMillis();
        InstanceMatchTask instanceMatch = new InstanceMatchTask(this.getClass().getSimpleName(), leftDb, rightDb, getValueEncoder());
        instanceMatch.getEncoder().prepareForEncoding();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDb, instanceMatch.getEncoder());
        List<TupleWithTable> destinationTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDb, instanceMatch.getEncoder());
        ComparisonStats.getInstance().addStat(ComparisonStats.PROCESS_INSTANCE_TIME, System.currentTimeMillis() - start);
        CompatibilityMap compatibilityMap = compatibleTupleFinder.find(sourceTuples, destinationTuples);
        if (logger.isDebugEnabled()) logger.debug("Compatibility map:\n" + compatibilityMap);
        TupleMatches tupleMatches = ComparisonUtility.findTupleMatches(destinationTuples, compatibilityMap);
        ComparisonUtility.sortTupleMatches(tupleMatches);
        if (logger.isDebugEnabled()) logger.debug("TupleMatches: " + tupleMatches);
        TupleMapping bestTupleMapping = bestTupleMappingFinder.findBestTupleMapping(sourceTuples, destinationTuples, tupleMatches);
        nonMatchingTuplesFinder.find(sourceTuples, destinationTuples, bestTupleMapping);
        instanceMatch.setTupleMapping(bestTupleMapping);
        return instanceMatch;
    }

}
