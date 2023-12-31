package ic.test.utility;


import speedy.model.database.ITable;
import speedy.persistence.xml.DAOXmlUtility;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityTest {

    private static Logger logger = LoggerFactory.getLogger(UtilityTest.class);
    private static DAOXmlUtility daoUtility = new DAOXmlUtility();
    private static Runtime runtime = Runtime.getRuntime();
    public static final String RESOURCES_FOLDER = "/resources/";
    public static final String EXPERIMENTS_FOLDER = "/experiments/";

    public static String getResultDir() {
        return System.getProperty("user.home") + "/Dropbox/BartExp/";
    }


    public static String getExperimentsFolder(String fileTask) {
        String experimentsFolder = UtilityTest.getExternalFolder(EXPERIMENTS_FOLDER);
        return experimentsFolder + fileTask;
    }

    public static String getResourcesFolder(String fileTask) {
        String resourcesFolder = UtilityTest.getExternalFolder(RESOURCES_FOLDER      );
        return resourcesFolder + fileTask;
    }

    public static String getAbsoluteFileName(String fileName) {
        return UtilityTest.class.getResource(fileName).getFile();
    }

    public static String getExternalFolder(String fileName) {
        File buildDir = new File(UtilityTest.class.getResource("/").getFile()).getParentFile();
        File rootDir = buildDir.getParentFile();
        String miscDir = rootDir.toString() + File.separator + "misc";
        return miscDir + fileName;
    }

    public static long getSize(ITable table) {
        return table.getSize();
    }

    
    public static String getMemInfo() {
        NumberFormat format = NumberFormat.getInstance(Locale.ITALIAN);
        StringBuilder sb = new StringBuilder();
        long allocatedMemory = runtime.totalMemory();
        sb.append(format.format(allocatedMemory / 1024 / 1024)).append(" MB");
        return sb.toString();

    }

}
