package ic.test.comparison.generator;

import ic.comparison.ComparisonConfiguration;
import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import ic.comparison.ValueMapping;
import ic.comparison.generator.ComparisonScenarioGeneratorWithMappings;
import ic.comparison.generator.InstancePair;
import ic.comparison.operators.ComputeInstanceSimilarityBruteForce;
import ic.comparison.operators.ComputeInstanceSimilarityBruteForceCompatibility;
import ic.comparison.operators.ComputeInstanceSimilarityHashing;
import ic.comparison.operators.ComputeScore;
import ic.comparison.operators.IComputeInstanceSimilarity;
import ic.test.comparison.ComparisonUtilityTest;
import ic.utility.ICUtility;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import speedy.model.database.IValue;
import speedy.model.database.NullValue;
import speedy.model.database.Tuple;
import speedy.model.database.TupleWithTable;
import speedy.model.database.mainmemory.datasource.IntegerOIDGenerator;
import speedy.persistence.file.operators.ExportCSVFile;
import speedy.utility.SpeedyUtility;

public class TestComparisonScenarioGeneratorWithMappings extends TestCase {

    private final static Logger logger = LoggerFactory.getLogger(TestComparisonScenarioGeneratorWithMappings.class);

    private int newReduntandTuples = 10;
    private int newRandomTuples = 10;
    private int cellsToChange = 5;
    private List<String> results = new ArrayList<>();
    private boolean printInfo = true;
    private boolean skipAssert = true;
    private boolean useBruteForceInGreedy = true;
    private boolean exportWithOid = true;

    public void xtestExecutionSmall() {
        String dataset = "doctors-100";
        for (int nre = 0; nre <= 99; nre += 5) {
            for (int nra = 0; nra <= 99; nra += 5) {
                for (int cc = 0; cc <= 99; cc += 5) {
                    this.newReduntandTuples = nre;
                    this.newRandomTuples = nra;
                    this.cellsToChange = cc;
                    modifyCellsInSource(dataset, null, true, null);
                    modifyCellsInTarget(dataset, null, true, null);
                    modifyCellsInSourceAndTarget(dataset, null, true, null);
                    addRedundantRowsInSource(dataset, null, true, null);
                    addRedundantRowsInTarget(dataset, null, true, null);
                    addRedundantRowsInSourceAndTarget(dataset, null, true, null);
                    addRandomRowsInSource(dataset, null, true, null);
                    addRandomRowsInTarget(dataset, null, true, null);
                    addRandomRowsInSourceAndTarget(dataset, null, true, null);
                    addRandomAndRedundantRowsInSourceAndTarget(dataset, null, true, null);
                }
            }
        }
    }

    public void xtestExecution() {
        //"conference", "doctors-100", ,
        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
//        String[] datasets = {"conference"};
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, null, true, null);
            modifyCellsInTarget(dataset, null, true, null);
            modifyCellsInSourceAndTarget(dataset, null, true, null);
            addRedundantRowsInSource(dataset, null, true, null);
            addRedundantRowsInTarget(dataset, null, true, null);
            addRedundantRowsInSourceAndTarget(dataset, null, true, null);
            addRandomRowsInSource(dataset, null, true, null);
            addRandomRowsInTarget(dataset, null, true, null);
            addRandomRowsInSourceAndTarget(dataset, null, true, null);
            addRandomAndRedundantRowsInSourceAndTarget(dataset, null, true, null);
            System.out.print(".\n");
        }
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void xtestExecutionGreedy() {
//"doctors-100", "doctors-1k"
//        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
        String[] datasets = {"doctors-1k"};

        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRedundantRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRedundantRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRandomRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRandomRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRandomRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".");
            addRandomAndRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, null);
            System.out.print(".\n");
        }
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void xtestExport() {
        System.out.println("");
        // "conference", "doctors-100",
        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
//        String[] datasets = {"bikeshare"};
//        String[] datasets = {"conference"};
        String exportPath = "/Users/enzoveltri/Desktop/instance-comparisons/";
        for (String dataset : datasets) {
            System.out.println(dataset);
            modifyCellsInSource(dataset, null, false, exportPath);
            System.out.print(".");
            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRedundantRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRedundantRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRandomRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRandomRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRandomRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".");
            addRandomAndRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), false, exportPath);
            System.out.print(".\n");
        }
    }

    public void xtestExportAndExecuteGreedy() {
        // "conference", "doctors-100",
//        String[] datasets = {"conference", "doctors-100", "doctors-1k"};
        String[] datasets = {"doctors-1k", "bikeshare-1k", "github-1k"};
//        String[] datasets = {"conference"};
        int[] cellsToChangeValues = {15, 20, 25, 30, 35, 40, 45, 50};
        for (int cellsToChangeValue : cellsToChangeValues) {
            this.cellsToChange = cellsToChangeValue;
            String exportPath = "/Users/enzoveltri/Desktop/instance-comparisons/exp-diff-errors-new-strat/" + this.cellsToChange + "-" + this.newReduntandTuples + "-" + this.newRandomTuples + "-new/";
            for (String dataset : datasets) {
                System.out.println(dataset);
                IntegerOIDGenerator.setCounter(1);
//            modifyCellsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                System.out.print(".");
                IntegerOIDGenerator.setCounter(1);
//            modifyCellsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//                modifyCellsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
//                System.out.println(this.results.get(this.results.size() - 1));
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRedundantRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRedundantRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRandomRowsInSource(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRandomRowsInTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
//            addRandomRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                IntegerOIDGenerator.setCounter(1);
                System.out.print(".");
                addRandomAndRedundantRowsInSourceAndTarget(dataset, new ComputeInstanceSimilarityHashing(useBruteForceInGreedy), true, exportPath);
                System.out.println(this.results.get(this.results.size() - 1));
                System.out.print(".\n");
            }
            for (String result : this.results) {
                System.out.println(result);
            }
        }
        System.out.println("*****************");
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void testExecuteBFAndGreedyOnInstances() {
        ComparisonConfiguration.setTwoWayValueMapping(true);

//        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp-diff-errors-new-strat/5-10-10-new/github-10k/modifyCellsInSourceAndTarget/left/";
//        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp-diff-errors-new-strat/5-10-10-new/github-10k/modifyCellsInSourceAndTarget/right/";
//        String methodName = "addRandomAndRedundantRowsInSourceAndTarget";
//        setNonInjectiveNonFunctionalMapping();
//        String methodName = "modifyCellsInSourceAndTarget";
//        setInjectiveFunctionalMapping();
//        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp1/nba/ver_0/";
//        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp1/iris/ver_1_removed_20/";
//        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp1/nba/ver_columns/";
//        String methodName = "ver0-ver2-column";
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        ComparisonConfiguration.setBfMaxRecursionLevel(20); //20;
        ComparisonConfiguration.setForceExaustiveSearch(false);

        // FOR DATA CLEANING is injective functional mapping
//        setInjectiveFunctionalMapping();
//        SpeedyConstants.setStringSkolemPrefixes(new String[]{"_L", "V_", "_N"});
//        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp-datacleaning/bus/dirty/";
//        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp-datacleaning/bus/vc/";
//        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp-datacleaning/bus/clean/";
        // greedy, holistic, holoclean, lunatic, sampling, vc
//        String methodName = "vc-clean";
        // FOR DATA VERSIONING
        setInjectiveFunctionalMapping();
        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp-dataversioning/nba/ver_0";
        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp-dataversioning/nba/ver_columns";
        String methodName = "shuffled";
        // FOR DATA EXCHANGE
//        setNonInjectiveNonFunctionalMapping();
//        String pathLeft = "/Users/enzoveltri/Desktop/instance-comparisons/exp-de/doctors/real25k/universal-no-egd/";
//        String pathRight = "/Users/enzoveltri/Desktop/instance-comparisons/exp-de/doctors/real25k/core/";
//        String methodName = "no-egd-core";
//        ComparisonConfiguration.setTwoWayValueMapping(false);
        IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
        IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
//        IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityBruteForceCompatibility();
//        IComputeInstanceSimilarity bf = new ComputeSimilarityF1();
        IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityHashing(false);
        IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
        long startBF = System.currentTimeMillis();
        InstanceMatchTask compareBF = bf.compare(leftDB, rightDB);
        long timeBG = System.currentTimeMillis() - startBF;
        System.out.println("Time BF:" + timeBG);
        long startGreedy = System.currentTimeMillis();
        InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
        long timeGreedy = System.currentTimeMillis() - startGreedy;
        double scoreBF = compareBF.getTupleMapping().getScore();
        double scoreGreedy = compareGreedy.getTupleMapping().getScore();
        generateOutput(methodName, leftDB, rightDB, compareGreedy.getTupleMapping(), scoreBF, scoreGreedy, 9999, timeGreedy, timeBG);
        for (String result : this.results) {
            System.out.println(result);
        }
//        TupleMapping tupleMappingGreedy = compareGreedy.getTupleMapping();
//        System.out.println(tupleMappingGreedy);
    }

    public void xtestAndExecuteBFAndGreedyOnInstancesBatch() {
        // BATCH CONFIG
//        String[] datasets = {"bikeshare-5k","github-5k"};
        String[] datasets = {"github-5k"};
        String[] methods = {"modifyCellsInSourceAndTarget", "addRandomAndRedundantRowsInSourceAndTarget"};
//        String[] methods = {"addRandomAndRedundantRowsInSourceAndTarget"};
        String baseFolder = "/Users/enzoveltri/Desktop/instance-comparisons/exp-diff-errors-new-strat/1-10-10-new/";
        ComparisonConfiguration.setUseDictionaryEncoding(true);
        for (String dataset : datasets) {
            System.out.println("Dataset:" + dataset);
            for (String method : methods) {
                String pathLeft = baseFolder + dataset + "/" + method + "/left/";
                String pathRight = baseFolder + dataset + "/" + method + "/right/";
                if (method.equals("modifyCellsInSourceAndTarget")) {
//                    ComparisonConfiguration.setTwoWayValueMapping(false);
                    setInjectiveFunctionalMapping();
                }
                if (method.equals("addRandomAndRedundantRowsInSourceAndTarget")) {
//                    ComparisonConfiguration.setTwoWayValueMapping(true);
                    setNonInjectiveNonFunctionalMapping();
                }
                if (dataset.contains("doctors")) {
                    ComparisonConfiguration.setBfMaxRecursionLevel(null);
                } else {
                    ComparisonConfiguration.setBfMaxRecursionLevel(10);
                }
                IDatabase leftDB = ICUtility.loadMainMemoryDatabase(pathLeft);
                IDatabase rightDB = ICUtility.loadMainMemoryDatabase(pathRight);
//                IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityBruteForce();
                IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityBruteForceCompatibility();
//        IComputeInstanceSimilarity bf = new ComputeInstanceSimilarityHashing(false);
                IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
                long startBF = System.currentTimeMillis();
                InstanceMatchTask compareBF = bf.compare(leftDB, rightDB);
                long timeBG = System.currentTimeMillis() - startBF;
                long startGreedy = System.currentTimeMillis();

                InstanceMatchTask compareGreedy = greedy.compare(leftDB, rightDB);
                long timeGreedy = System.currentTimeMillis() - startGreedy;
                double scoreBF = compareBF.getTupleMapping().getScore();
                double scoreGreedy = compareGreedy.getTupleMapping().getScore();
                String output = generateOutput(method, leftDB, rightDB, compareBF.getTupleMapping(), scoreBF, scoreGreedy, 9999, timeGreedy, timeBG);
                System.out.println(output);

            }
        }
        System.out.println("********************");
        for (String result : this.results) {
            System.out.println(result);
        }
    }

    public void modifyCellsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonConfiguration.setTwoWayValueMapping(true);
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        ComputeScore computeScore = new ComputeScore();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            ComparisonConfiguration.setTwoWayValueMapping(true);
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
    }

    public void modifyCellsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void modifyCellsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
//        String scenarioName = "conference";
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setInjectiveFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        System.out.println("Time Generation: " + generator.getTimeGeneration());
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/modifyCellsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + score + " Greedy: " + scoreSimilarity, scoreSimilarity == score);
            }
            generateOutput("modifyCellsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
        }
    }

    public void addRedundantRowsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRedundantRowsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRedundantRowsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRedundantRowsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRedundantRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, 0, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRedundantRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRedundantRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInSource(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, false);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInSource";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRandomRowsInSource", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), false, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRandomRowsInTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
//        IDatabase originalDB = ComparisonUtilityTest.loadDatabase(sourceFile, "/resources/redundancy/");
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(0, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
//            ComparisonConfiguration.setForceExaustiveSearch(false);
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + score + " Greedy: " + scoreSimilarity, scoreSimilarity == score);
            }
            generateOutput("addRandomRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");

        }
    }

    public void addRandomAndRedundantRowsInSourceAndTarget(String scenarioName, IComputeInstanceSimilarity similarityChecker, boolean computeSimilarity, String expPath) {
        if (similarityChecker == null) {
            similarityChecker = new ComputeInstanceSimilarityBruteForce();
        }
        String sourceFile = scenarioName + "/initial/";
        setNonInjectiveNonFunctionalMapping();
        ComparisonScenarioGeneratorWithMappings generator = new ComparisonScenarioGeneratorWithMappings(newReduntandTuples, newRandomTuples, cellsToChange, 1234);
        InstancePair instancePair = generator.generateWithMappings(ComparisonUtilityTest.getFolder(sourceFile, "/resources/redundancy/"), true, true);
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        List<TupleWithTable> sourceTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(sourceDB);
        List<TupleWithTable> targetTuples = SpeedyUtility.extractAllTuplesFromDatabaseForGeneration(targetDB);
        ComputeScore computeScore = new ComputeScore();
        double score = computeScore.computeScore(sourceTuples, targetTuples, instancePair.getTupleMapping());
        if (expPath != null) {
            ExportCSVFile exporter = new ExportCSVFile();
            expPath += scenarioName + "/addRandomAndRedundantRowsInSourceAndTarget";
            exporter.exportDatabase(instancePair.getLeftDB(), true, exportWithOid, expPath + "/left/");
            exporter.exportDatabase(instancePair.getRightDB(), true, exportWithOid, expPath + "/right/");
            saveMappings(score, instancePair, expPath);
        }
        logger.info("InstancePair: {}", instancePair);
        logger.info("Score: {}", score);
        if (computeSimilarity) {
            long start = System.currentTimeMillis();
            InstanceMatchTask result = similarityChecker.compare(sourceDB, targetDB);
            long end = System.currentTimeMillis();
            if (printInfo) {
                System.out.println("Time (ms):" + (end - start));
            }
            logger.info("Source to Target");
            logger.info(result.toString());
            Double scoreSimilarity = result.getTupleMapping().getScore();
            if (printInfo) {
                System.out.println("BruteForce Score: " + score + " - ComputedScore: " + scoreSimilarity);
            }
            if (scoreSimilarity == null) {
                scoreSimilarity = 0.0;
            }
            if (!skipAssert && score != scoreSimilarity) {
                System.out.println(instancePair);
                System.out.println("Score Generated:" + score);
                System.out.println("Source to Target");
                System.out.println(result.toString());
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingsGenerated = instancePair.getTupleMapping().getTupleMapping();
                Map<TupleWithTable, Set<TupleWithTable>> tupleMappingBruteForce = result.getTupleMapping().getTupleMapping();
                MapDifference<TupleWithTable, Set<TupleWithTable>> difference = Maps.difference(tupleMappingsGenerated, tupleMappingBruteForce);
                print(difference);
                System.out.println("Error config:\n" + this.newReduntandTuples + " - " + this.newRandomTuples + " - " + this.cellsToChange);
            }
            if (!skipAssert) {
                assertTrue("Same score with Brute Force" + scenarioName + " - scoreBruteForce: " + scoreSimilarity + " Greedy: " + score, scoreSimilarity == score);
            }
            generateOutput("addRandomAndRedundantRowsInSourceAndTarget", instancePair, score, scoreSimilarity, generator.getTimeGeneration(), (end - start), "NA");
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

    private void generateOutput(String methodName, InstancePair instancePair, double bruteForceScore, Double greedyScore, long timeGenerationInstances, long greedyTime, String bruteForceTime) {
        String configuration = this.cellsToChange + " | " + this.newReduntandTuples + " | " + this.newRandomTuples;
        IDatabase sourceDB = instancePair.getLeftDB();
        IDatabase targetDB = instancePair.getRightDB();
        String sourceStats = getDBStats(sourceDB);
        String targetStats = getDBStats(targetDB);
        long tupleMappingSize = instancePair.getTupleMapping().getTupleMapping().size();
        long leftNonMatchingTuples = instancePair.getTupleMapping().getLeftNonMatchingTuples().size();
        long rightNonMatchingTuples = instancePair.getTupleMapping().getRightNonMatchingTuples().size();
        String experiment = configuration + "\t"
                + methodName + "\t"
                + sourceStats + "\t"
                + targetStats + "\t"
                + tupleMappingSize + "\t"
                + leftNonMatchingTuples + "\t"
                + rightNonMatchingTuples + "\t"
                + bruteForceScore + "\t"
                + greedyScore + "\t"
                + timeGenerationInstances + "\t"
                + greedyTime + "\t"
                + bruteForceTime;
        experiment = experiment.replace(".", ",");
        this.results.add(experiment);
    }

    private String generateOutput(String methodName, IDatabase sourceDB, IDatabase targetDB, TupleMapping tupleMapping, double bruteForceScore, Double greedyScore, long timeGenerationInstances, long greedyTime, long bruteForceTime) {
        String configuration = this.cellsToChange + " | " + this.newReduntandTuples + " | " + this.newRandomTuples;
        String sourceStats = getDBStats(sourceDB);
        String targetStats = getDBStats(targetDB);
        long tupleMappingSize = tupleMapping.getTupleMapping().size();
        long leftNonMatchingTuples = tupleMapping.getLeftNonMatchingTuples().size();
        long rightNonMatchingTuples = tupleMapping.getRightNonMatchingTuples().size();
        String experiment = configuration + "\t"
                + methodName + "\t"
                + sourceStats + "\t"
                + targetStats + "\t"
                + tupleMappingSize + "\t"
                + leftNonMatchingTuples + "\t"
                + rightNonMatchingTuples + "\t"
                + bruteForceScore + "\t"
                + greedyScore + "\t"
                + timeGenerationInstances + "\t"
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

    private void print(MapDifference<TupleWithTable, Set<TupleWithTable>> difference) {
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnGenerated = difference.entriesOnlyOnLeft();
        String onlyOnGeneratedString = "Only on Generated:\n" + SpeedyUtility.printMapCompact(onlyOnGenerated);
        Map<TupleWithTable, Set<TupleWithTable>> onlyOnAlgorithm = difference.entriesOnlyOnRight();
        String onlyOnAlgorithmString = "Only on Algorithm:\n" + SpeedyUtility.printMapCompact(onlyOnAlgorithm);
        Map<TupleWithTable, MapDifference.ValueDifference<Set<TupleWithTable>>> entriesDiffering = difference.entriesDiffering();
        String differencesInMapString = "Differences in common:\n" + SpeedyUtility.printMapCompact(entriesDiffering);
        String toPrint = "-----------------------------\n"
                + onlyOnGeneratedString
                + "-----------------------------\n"
                + onlyOnAlgorithmString
                + "-----------------------------\n"
                + differencesInMapString;
        System.out.println(toPrint);
    }

    private void saveMappings(double score, InstancePair instancePair, String expPath) {
        String fileName = expPath + "/mappings.json";
        TupleMapping tupleMapping = instancePair.getTupleMapping();
        MappingExport exportObj = new MappingExport(score, tupleMapping.getTupleMapping(), tupleMapping.getLeftToRightValueMapping(), tupleMapping.getRightToLeftValueMapping());
        ObjectMapper objectMapper = new ObjectMapper();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(fileName));
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportObj);
            out.print(jsonString);
        } catch (Exception e) {
            logger.error("Exception in exporting json: " + e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }

    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    private class MappingExport implements Serializable {

        private double score;
        private Map<String, Set<String>> tupleMapping;
        private Map<String, String> leftToRightValueMapping;
        private Map<String, String> rightToLeftValueMapping;

        public MappingExport(double score, Map<TupleWithTable, Set<TupleWithTable>> tupleMapping, ValueMapping leftToRightValueMapping, ValueMapping rightToLeftValueMapping) {
            this.score = score;
            this.tupleMapping = convertTupleMapping(tupleMapping);
            this.leftToRightValueMapping = convertMap(leftToRightValueMapping);
            this.rightToLeftValueMapping = convertMap(rightToLeftValueMapping);
        }

        private Map<String, Set<String>> convertTupleMapping(Map<TupleWithTable, Set<TupleWithTable>> tupleMapping) {
            Map<String, Set<String>> mapping = new HashMap<>();
            for (TupleWithTable key : tupleMapping.keySet()) {
                String sKey = key.getTuple().toStringNoOID();
                if (exportWithOid) {
                    sKey = key.getTuple().toStringWithOID();
                }
                Set<String> values = mapping.get(sKey);
                if (values == null) {
                    values = new HashSet<>();
                    mapping.put(sKey, values);
                }
                Set<TupleWithTable> tupleSet = tupleMapping.get(key);
                for (TupleWithTable value : tupleSet) {
                    String sValue = value.getTuple().toStringNoOID();
                    if (exportWithOid) {
                        sValue = value.getTuple().toStringWithOID();
                    }
                    values.add(sValue);
                }
            }
            return mapping;
        }

        private Map<String, String> convertMap(ValueMapping valueMapping) {
            Map<String, String> map = new HashMap<>();
            for (IValue key : valueMapping.getKeys()) {
                String sKey = key.getPrimitiveValue().toString();
                String sValue = valueMapping.getValueMapping(key).getPrimitiveValue().toString();
                map.put(sKey, sValue);
            }
            return map;
        }

    }

}
