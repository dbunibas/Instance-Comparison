package ic.test.comparison.scalability;

import ic.comparison.ComparisonConfiguration;
import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import ic.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import ic.comparison.operators.ComputeInstanceSimilarityHashing;
import ic.comparison.operators.IComputeInstanceSimilarity;
import ic.utility.ICUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.ITupleIterator;
import speedy.model.database.Cell;
import speedy.model.database.ConstantValue;
import speedy.model.database.IDatabase;
import speedy.model.database.ITable;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.utility.test.UtilityForTests;

public class TestScalability extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestScalability.class);
    private List<String> results = new ArrayList<>();

    @Override
    protected void tearDown() throws Exception {
        this.results = new ArrayList<>();
    }

    @Override
    protected void setUp() throws Exception {
        this.results = new ArrayList<>();
    }

    public void xtestScalability() {
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        ComparisonConfiguration.setForceExaustiveSearch(true);
        ComparisonConfiguration.setTwoWayValueMapping(true);
        boolean useBF = true; // if true will take too many time
        // to get results but without all the possible combination of BF set setForceExaustiveSearch(false)
        String configGeneration = "5-10-10";
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-scalability" + File.separator + configGeneration + File.separator);
        System.out.println("BASE_PATH_SCALABILITY: " + basePath);
        String datasets[] = {"doctors", "bikeshare", "github"};
        String sizes[] = {"500", "1k", "5k", "10k", "100k"};
        String methods[] = {"modifyCellsInSourceAndTarget", "addRandomAndRedundantRowsInSourceAndTarget"};
        for (String method : methods) {
            for (String dataset : datasets) {
                for (String size : sizes) {
                    String expPath = basePath + dataset + "-" + size + File.separator + method + File.separator;
                    String pathLeft = expPath + "left";
                    String pathRight = expPath + "right";
                    if (method.equals("modifyCellsInSourceAndTarget")) {
                        setInjectiveFunctionalMapping();
                    }
                    if (method.equals("addRandomAndRedundantRowsInSourceAndTarget")) {
                        setNonInjectiveNonFunctionalMapping();
                    }
                    IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
                    IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
                    IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityBruteForceCompatibility();
                    IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                    double scoreBF = -1;
                    long timeBF = -1;
                    if (useBF) {
                        long startBF = System.currentTimeMillis();
                        InstanceMatchTask compareBF = bf.compare(leftDB, rightDB);
                        timeBF = System.currentTimeMillis() - startBF;
                        scoreBF = compareBF.getTupleMapping().getScore();
                    }
                    long startGreedy = System.currentTimeMillis();
                    InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                    long timeGreedy = System.currentTimeMillis() - startGreedy;
                    double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                    String output = generateOutput(method, leftDB, rightDB, compareGreedy.getTupleMapping(), scoreBF, scoreGreedy, timeGreedy, timeBF);
//                  System.out.println(output);
                }
            }
        }
        System.out.println("***************************");
        System.out.println("Method\tSourceStats\tTargetStats\tTupleMappings\tLeftNonMatching\tRightNonMatching\tBFScore\tGreedyScore\tGreedyTime\tBFTime");
        for (String result : this.results) {
            System.out.println(result);
        }

    }

    public void testCellChangesInjected() {
        // SCORES FOR BF are stored in mappings.json of each scenario
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        ComparisonConfiguration.setForceExaustiveSearch(true);
        ComparisonConfiguration.setTwoWayValueMapping(true);
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-scalability" + File.separator);
        System.out.println("BASE_PATH_SCALABILITY: " + basePath);
        String datasets[] = {"doctors", "bikeshare", "github"};
        String size = "1k";
        String method = "modifyCellsInSourceAndTarget";
        String percentages[] = {"1", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};
        for (String dataset : datasets) {
            for (String percentage : percentages) {
                String configGeneration = percentage + "-10-10";
                String expPath = basePath + configGeneration + File.separator + dataset + "-" + size + File.separator + method + File.separator;
                String pathLeft = expPath + "left";
                String pathRight = expPath + "right";
                setInjectiveFunctionalMapping();
                IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
                IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
                IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                double scoreBF = -1;
                long timeBF = -1;
                long startGreedy = System.currentTimeMillis();
                InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                long timeGreedy = System.currentTimeMillis() - startGreedy;
                double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                String output = generateOutput(method, leftDB, rightDB, compareGreedy.getTupleMapping(), scoreBF, scoreGreedy, timeGreedy, timeBF);
//                System.out.println(output);
            }
        }
        System.out.println("***************************");
        System.out.println("Method\tSourceStats\tTargetStats\tTupleMappings\tLeftNonMatching\tRightNonMatching\tBFScore\tGreedyScore\tGreedyTime\tBFTime");
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void testAblation() {
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        ComparisonConfiguration.setForceExaustiveSearch(true);
        ComparisonConfiguration.setTwoWayValueMapping(true);
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-scalability" + File.separator);
        System.out.println("BASE_PATH_SCALABILITY: " + basePath);
        String datasets[] = {"doctors", "bikeshare", "github"};
        String size = "1k";
        String method = "addRandomAndRedundantRowsInSourceAndTarget";
        String percentages[] = {"5"};
        for (String dataset : datasets) {
            for (String percentage : percentages) {
                String configGeneration = percentage + "-10-10";
                String expPath = basePath + configGeneration + File.separator + dataset + "-" + size + File.separator + method + File.separator;
                String pathLeft = expPath + "left";
                String pathRight = expPath + "right";
                setNonInjectiveNonFunctionalMapping();
                IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
                IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
                IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                IComputeInstanceSimilarity greedyNoBF = new ComputeInstanceSimilarityHashing(false);
                long startGreedyNoBF = System.currentTimeMillis();
                InstanceMatchTask compareGreedyNoBF = greedyNoBF.compare(leftDB, rightDB);
                long timeNoBF = System.currentTimeMillis() - startGreedyNoBF;
                long startGreedy = System.currentTimeMillis();
                InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                long timeGreedy = System.currentTimeMillis() - startGreedy;
                double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                double scoreNoBF = compareGreedyNoBF.getTupleMapping().getScore();
                String output = generateOutput(method, leftDB, rightDB, compareGreedyNoBF.getTupleMapping(), scoreNoBF, scoreGreedy, timeGreedy, timeNoBF);
//                System.out.println(output);
            }
        }
        System.out.println("***************************");
        System.out.println("Method\tSourceStats\tTargetStats\tTupleMappings\tLeftNonMatching\tRightNonMatching\tBFScore\tGreedyScore\tGreedyTime\tBFTime");
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void testAblationAttributes() {
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        ComparisonConfiguration.setForceExaustiveSearch(false);
        ComparisonConfiguration.setTwoWayValueMapping(true);
        boolean useBF = false; // if true will take too many time
        String configGeneration = "10-10-10";
        String basePath = UtilityForTests.getResourcesFolder("datasets" + File.separator + "exp-ablation" + File.separator + configGeneration + File.separator);
        System.out.println("BASE_PATH_SCALABILITY: " + basePath);
        String datasets[] = {"github"};
        String sizes[] = {"500", "1k", "5k"};
        String methods[] = {"modifyCellsInSourceAndTarget", "addRandomAndRedundantRowsInSourceAndTarget"};
        String maxAttrsWithNulls[] = {"1", "5", "10", "15", "19"};
        for (String maxAttrsWithNull : maxAttrsWithNulls) {
            String basePathExpAttrs = basePath + maxAttrsWithNull + File.separator;
            for (String method : methods) {
                for (String dataset : datasets) {
                    for (String size : sizes) {
                        String expPath = basePathExpAttrs + dataset + "-" + size + File.separator + method + File.separator;
                        String pathLeft = expPath + "left";
                        String pathRight = expPath + "right";
                        if (method.equals("modifyCellsInSourceAndTarget")) {
                            setInjectiveFunctionalMapping();
                        }
                        if (method.equals("addRandomAndRedundantRowsInSourceAndTarget")) {
                            setNonInjectiveNonFunctionalMapping();
                        }
                        IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
                        IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
                        IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityBruteForceCompatibility();
                        IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                        //IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(false);
                        double scoreBF = -1;
                        long timeBF = -1;
                        if (useBF) {
                            long startBF = System.currentTimeMillis();
                            InstanceMatchTask compareBF = bf.compare(leftDB, rightDB);
                            timeBF = System.currentTimeMillis() - startBF;
                            scoreBF = compareBF.getTupleMapping().getScore();
                        }
                        long startGreedy = System.currentTimeMillis();
                        InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                        long timeGreedy = System.currentTimeMillis() - startGreedy;
                        double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                        String output = generateOutput(method+"-Attrs:"+maxAttrsWithNull, leftDB, rightDB, compareGreedy.getTupleMapping(), scoreBF, scoreGreedy, timeGreedy, timeBF);
//                        System.out.println(output);
                    }
                }
            }
        }
        System.out.println("***************************");
        System.out.println("Method\tSourceStats\tTargetStats\tTupleMappings\tLeftNonMatching\tRightNonMatching\tBFScore\tGreedyScore\tGreedyTime\tBFTime");
        for (String result : this.results) {
            System.out.println(result);
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
