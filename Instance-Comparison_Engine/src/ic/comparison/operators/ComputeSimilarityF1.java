package ic.comparison.operators;

import ic.comparison.InstanceMatchTask;
import ic.comparison.TupleMapping;
import static ic.utility.ICUtility.getValueEncoder;
import java.util.List;
import speedy.model.database.Cell;
import speedy.model.database.IDatabase;
import speedy.model.database.TupleWithTable;
import speedy.utility.SpeedyUtility;

public class ComputeSimilarityF1 implements IComputeInstanceSimilarity {

    @Override
    public InstanceMatchTask compare(IDatabase leftDb, IDatabase rightDb) {
        InstanceMatchTask instanceMatch = new InstanceMatchTask(this.getClass().getSimpleName(), leftDb, rightDb, getValueEncoder());
        List<TupleWithTable> leftTuples = SpeedyUtility.extractAllTuplesFromDatabase(leftDb, instanceMatch.getEncoder());
        List<TupleWithTable> rightTuples = SpeedyUtility.extractAllTuplesFromDatabase(rightDb, instanceMatch.getEncoder());
        TupleMapping tupleMapping = new TupleMapping();
        if (leftTuples.size() == rightTuples.size()) {
            int tp = 0;
            int fp = 0;
            int fn = 0;
            for (int i = 0; i < leftTuples.size(); i++) {
                TupleWithTable leftRow = leftTuples.get(i);
                TupleWithTable rightRow = rightTuples.get(i);
                tupleMapping.putTupleMapping(leftRow, rightRow);
                for (int cellIdx = 0; cellIdx < leftRow.getTuple().getCells().size(); cellIdx++) {
                    Cell leftCell = leftRow.getTuple().getCells().get(cellIdx);
                    Cell rightCell = rightRow.getTuple().getCells().get(cellIdx);
                    if (leftCell.isOID()) {
                        continue;
                    }
                    String leftCellValue = leftCell.getValue().getPrimitiveValue().toString().toLowerCase();
                    String rightCellValue = rightCell.getValue().getPrimitiveValue().toString().toLowerCase();
                    if (leftCellValue.equals(rightCellValue)) {
                        tp++;
                    } else {
                        fp++;
                    }
                }
            }
            Double score = computeF1(tp, fp, fn);
            tupleMapping.setScore(score);
            instanceMatch.setTupleMapping(tupleMapping);
        }
        return instanceMatch;
    }

    private Double computeF1(int tp, int fp, int fn) {
        double precision = ((float) tp) / (tp + fp);
        double recall = ((float) tp) / (tp + fn);
        double f1 = (2.0 * precision * recall) / (precision + recall);
        return f1;
    }

}
