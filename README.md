# Instance-Comparison

This tool provides a fast way to compare incomplete instances, i.e. instances with labeled nulls. The tool returns a similarity score of instances (a value between 0 and 1) but also differences in compared instances.
We implemented an exact algorithm that works only on small instances. We also implemented a greedy algorithm called Signature algorithm that can be executed on bigger instances with a minimal difference in the computation of such similarities.

## Comparing Instances

The easiest way to compare two instances is by defining the comparison scenario using a properties file. You can find an example of such a file at: `Instance-Comparison-Engine/core/src/test/resources/similarity/ComparisonTask.properties`


### Execution Instructions

To execute the comparison ensure you are in the project's root directory (`Instance-Comparison-Engine`), where the `gradlew` script is located. Then run the command

   - **For Unix-like Systems (Linux/macOS)**:
     ```sh
     ./gradlew run --args="/absolute/path/to/ComparisonTask.properties"
     ```
   - **For Windows**:
     ```cmd
     gradlew.bat run --args="/absolute/path/to/ComparisonTask.properties"
     ```

If your path contains spaces or special characters, enclose it in double quotes to avoid any issues.


## Execute experiments

To execute some basic experiments:
1) Unzip the datasets.zip.001 archive in `Instance-Comparison-Engine/core/src/test/resources/`
2) Execute the following Java Classes in the test package:
  -  ic.test.comparison.scalability.TestScalability. It executes both the Exact Algorithm and the Signature Algorithm.
  -  ic.test.comparison.applications.TestApplications. It executes the Signature Algorithm on some target applications like Data Cleaning, Data Exchange and Data Versioning

The following snippet of code provides an example of using the Signature Algorithm (ComputeInstanceSimilarityHashing class) to compare two different instances (source and target). We assume that source and target are folders and contain a CSV file for each relational table.

```java
import java.util.Map;
import java.util.Set;
import ic.utility.ICUtility;
import ic.comparison.operators.IComputeInstanceSimilarity;
import ic.comparison.operators.ComputeInstanceSimilarityHashing;
import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import speedy.model.database.TupleWithTable;


String sourcePath = "..."; // your source folder path
String targetPath = "..."; // your target folder path
IDatabase sourceDB = ICUtility.loadMainMemoryDatabase(sourcePath);
IDatabase targetDB = ICUtility.loadMainMemoryDatabase(targetPath);
IComputeInstanceSimilarity greedy = new ComputeInstanceSimilarityHashing(true);
InstanceMatchTask compareGreedy = greedy.compare(sourceDB, targetDB);
double scoreGreedy = compareGreedy.getTupleMapping().getScore();
Set<TupleWithTable> sourceNonMatchingTuples = compareGreedy.getTupleMapping().getLeftNonMatchingTuples();
Set<TupleWithTable> targetNonMatchingTuples = compareGreedy.getTupleMapping().getRightNonMatchingTuples();
Map<TupleWithTable, Set<TupleWithTable>> tupleMapping = compareGreedy.getTupleMapping().getTupleMapping();

```

## Extra 
We also provide a Jupyter Notebook in misc folder, used to compute metrics for target applications.

## Citation

```
@inproceedings{InstanceComparison,
  author       = {Boris Glavic and
                  Giansalvatore Mecca and
                  Ren{\'{e}}e J. Miller and
                  Paolo Papotti and
                  Donatello Santoro and
                  Enzo Veltri},
  title        = {Similarity Measures For Incomplete Database Instances},
  booktitle    = {Proceedings 27th International Conference on Extending Database Technology,
                  {EDBT} 2024, Paestum, Italy, March 25 - March 28},
  pages        = {461--473},
  publisher    = {OpenProceedings.org},
  year         = {2024},
  url          = {https://doi.org/10.48786/edbt.2024.40},
  doi          = {10.48786/EDBT.2024.40}
}
```
