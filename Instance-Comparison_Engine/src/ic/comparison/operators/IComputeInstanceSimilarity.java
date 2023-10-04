package ic.comparison.operators;

import ic.comparison.InstanceMatchTask;
import speedy.model.database.IDatabase;

public interface IComputeInstanceSimilarity {

    public InstanceMatchTask compare(IDatabase leftInstance, IDatabase rightInstance);
}
