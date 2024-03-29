package ic;

import speedy.model.database.operators.IRunQuery;
import speedy.model.database.operators.dbms.SQLRunQuery;
import speedy.model.database.operators.mainmemory.MainMemoryRunQuery;
import speedy.model.algebra.operators.IInsertTuple;
import speedy.model.algebra.operators.IUpdateCell;
import speedy.model.algebra.operators.sql.SQLInsertTuple;
import speedy.model.algebra.operators.sql.SQLUpdateCell;
import speedy.model.database.operators.IDatabaseManager;
import speedy.model.database.operators.IExplainQuery;
import speedy.model.database.operators.dbms.SQLDatabaseManager;
import speedy.model.database.operators.dbms.SQLExplainQuery;
import speedy.model.database.operators.mainmemory.MainMemoryDatabaseManager;
import speedy.model.database.operators.mainmemory.MainMemoryExplainQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.model.algebra.operators.mainmemory.MainMemoryInsertTuple;
import speedy.model.algebra.operators.mainmemory.MainMemoryUpdateCell;
import speedy.model.database.operators.IAnalyzeDatabase;
import speedy.model.database.operators.dbms.SQLAnalyzeDatabase;
import speedy.model.database.operators.mainmemory.MainMemoryAnalyzeDatabase;

public class OperatorFactory {

    private static Logger logger = LoggerFactory.getLogger(OperatorFactory.class);
    private static OperatorFactory singleton = new OperatorFactory();
    //
    private IRunQuery mainMemoryQueryRunner = new MainMemoryRunQuery();
    private IRunQuery sqlQueryRunner = new SQLRunQuery();
    //
    private IExplainQuery mainMemoryQueryExplanator = new MainMemoryExplainQuery();
    private IExplainQuery sqlQueryExplanator = new SQLExplainQuery();
    //
    private IUpdateCell mainMemoryCellUpdater = new MainMemoryUpdateCell();
    private IUpdateCell sqlCellUpdater = new SQLUpdateCell();
    //
    private IInsertTuple mainMemoryInsertOperator = new MainMemoryInsertTuple();
    private IInsertTuple sqlInsertOperator = new SQLInsertTuple();
    //
    private IDatabaseManager mainMemoryDatabaseManager = new MainMemoryDatabaseManager();
    private IDatabaseManager sqlDatabaseManager = new SQLDatabaseManager();
    //
    private IAnalyzeDatabase mainMemoryAnalyzer = new MainMemoryAnalyzeDatabase();
    private IAnalyzeDatabase sqlAnalyzer = new SQLAnalyzeDatabase();
    //

    private OperatorFactory() {
    }

    public static OperatorFactory getInstance() {
        return singleton;
    }
}
