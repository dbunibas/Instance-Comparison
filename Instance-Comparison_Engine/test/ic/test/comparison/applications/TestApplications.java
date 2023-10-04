package ic.test.comparison.applications;

import ic.comparison.ComparisonConfiguration;
import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import ic.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import ic.comparison.operators.ComputeInstanceSimilarityHashing;
import ic.comparison.operators.ComputeSimilarityF1;
import ic.comparison.operators.IComputeInstanceSimilarity;
import ic.utility.ICUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.SpeedyConstants;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.utility.test.UtilityForTests;

public class TestApplications extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestApplications.class);
    private List<String> results = new ArrayList<>();

    @Override
    protected void tearDown() throws Exception {
        this.results = new ArrayList<>();
    }

    @Override
    protected void setUp() throws Exception {
        this.results = new ArrayList<>();
    }

    public void testDataCleaning() {
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-datacleaning" + File.separator + "bus" + File.separator);
        String pathHolistic = basePath + File.separator + "holistic" + File.separator;
        String pathHoloclean = basePath + File.separator + "holoclean" + File.separator;
        String pathLunatic = basePath + File.separator + "lunatic" + File.separator;
        String pathSampling = basePath + File.separator + "sampling" + File.separator;
        String pathClean = basePath + File.separator + "clean" + File.separator;
        String pathsAlgorithms[] = {pathHolistic, pathHoloclean, pathLunatic, pathSampling};
        String algorithms[] = {"holistic", "holoclean", "lunatic", "sampling"};
        setInjectiveFunctionalMapping();
        SpeedyConstants.setStringSkolemPrefixes(new String[]{"_L", "V_", "_N"});
        for (int i = 0; i < algorithms.length; i++) {
            String algorithm = algorithms[i];
            String pathSolution = pathsAlgorithms[i];
            IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathSolution);
            IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathClean);
            IComputeInstanceSimilarity similarityF1Inst = new ComputeSimilarityF1();
            IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
            InstanceMatchTask compareF1Inst = similarityF1Inst.compare(leftDB, rightDB);
            InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
            double scoreF1Inst = compareF1Inst.getTupleMapping().getScore();
            double scoreGreedy = compareGreedy.getTupleMapping().getScore();
            String output = generateOutput(algorithm, leftDB, rightDB, compareGreedy.getTupleMapping(), scoreF1Inst, scoreGreedy, 0, 0);
//            System.out.println(output);
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    public void testDataExchange() {
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-de" + File.separator + "doctors" + File.separator);
        String sizes[] = {"5k", "25k"};
        String solutions[] = {"wrong", "universal-no-egd", "universal-egd"};
        String gold = "core";
        for (String size : sizes) {
            String pathSize = basePath + size + File.separator;
            String goldPath = pathSize + gold + File.separator;
            for (String solution : solutions) {
                setNonInjectiveNonFunctionalMapping();
                String solutionPath = pathSize + solution + File.separator;
                IDatabase leftDB = ICUtility.loadMainMemoryDatabase(solutionPath);
                IDatabase rightDB = ICUtility.loadMainMemoryDatabase(goldPath);
                IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                System.out.println("Size: " + size + " - Solution: " + solution + " - Score Greedy: " + scoreGreedy);
            }
        }
    }
    
    public void testDataVersioning() {
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-dataversioning" + File.separator);
        String datasets[] = {"iris", "nba"};
        String orig = "ver_0";
        String variants [] = {"ver_s", "ver_r", "ver_rs", "ver_c"};
        for (String dataset : datasets) {
            String pathSize = basePath + dataset + File.separator;
            String goldPath = pathSize + orig + File.separator;
            for (String variant : variants) {
                setInjectiveFunctionalMapping();
                String solutionPath = pathSize + variant + File.separator;
                IDatabase leftDB = ICUtility.loadMainMemoryDatabase(solutionPath);
                IDatabase rightDB = ICUtility.loadMainMemoryDatabase(goldPath);
                IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                Set<TupleWithTable> leftNonMatchingTuples = compareGreedy.getTupleMapping().getLeftNonMatchingTuples();
                Set<TupleWithTable> rightNonMatchingTuples = compareGreedy.getTupleMapping().getRightNonMatchingTuples();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMapping = compareGreedy.getTupleMapping().getTupleMapping();
                System.out.println("Dataset: " + dataset + " - Variant: " + variant + " - Score Greedy: " + scoreGreedy + " - NML: " + leftNonMatchingTuples.size() + " - NMR: " + rightNonMatchingTuples.size());
            }
        }
    }

    private void setInjectiveFunctionalMapping() {
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setFunctional(true);
    }

    private void setNonInjectiveFunctionalMapping() {
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setFunctional(true);
    }

    private void setInjectiveNonFunctionalMapping() {
        ComparisonConfiguration.setInjective(true);
        ComparisonConfiguration.setFunctional(false);
    }

    private void setNonInjectiveNonFunctionalMapping() {
        ComparisonConfiguration.setInjective(false);
        ComparisonConfiguration.setFunctional(false);
    }

    private String generateOutput(String methodName, IDatabase sourceDB, IDatabase targetDB, TupleMapping tupleMapping, double bruteForceScore, double greedyScore, long greedyTime, long bruteForceTime) {
        String sourceStats = getDBStats(sourceDB);
        String targetStats = getDBStats(targetDB);
        long tupleMappingSize = tupleMapping.getTupleMapping().size();
        long leftNonMatchingTuples = tupleMapping.getLeftNonMatchingTuples().size();
        long rightNonMatchingTuples = tupleMapping.getRightNonMatchingTuples().size();
        String experiment = methodName + "\t"
                + sourceStats + "\t"
                + targetStats + "\t"
                + tupleMappingSize + "\t"
                + leftNonMatchingTuples + "\t"
                + rightNonMatchingTuples + "\t"
                + bruteForceScore + "\t"
                + greedyScore + "\t"
                + greedyTime + "\t"
                + bruteForceTime;
        experiment = experiment.replace(".", ",");
        this.results.add(experiment);
        return experiment;
    }

    private String getDBStats(IDatabase db) {
        long dbSize = 0;
        long vars = 0;
        long consts = 0;
        for (String tableName : db.getTableNames()) {
            ITable table = db.getTable(tableName);
            long tableSize = table.getSize();
            dbSize += tableSize;
            ITupleIterator tupleIterator = table.getTupleIterator();
            while (tupleIterator.hasNext()) {
                Tuple tuple = tupleIterator.next();
                for (Cell cell : tuple.getCells()) {
                    if (cell.getValue() instanceof NullValue) {
                        vars++;
                    }
                    if (cell.getValue() instanceof ConstantValue) {
                        consts++;
                    }
                }
            }
        }
        String dbStats = dbSize + " | " + consts + " | " + vars;
        return dbStats;
    }

}
