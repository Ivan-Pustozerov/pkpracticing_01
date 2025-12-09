package SQL.repositories;

import org.junit.jupiter.api.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PerformanceTestFull {
    private static final int TOTAL_RECORDS = 10000;
    private static final String CSV_FILE = "performance_results.csv";

    private static Connection connection;
    private static UsersRepository usersRepo;
    private static MathFunctionsRepository mathRepo;
    private static AnalyticFunctionsRepository analyticRepo;
    private static TabulatedFunctionsRepository tabulatedRepo;
    private static FileWriter csvWriter;

    private static List<String> userNames = new ArrayList<>();
    private static List<Long> userId = new ArrayList<>();
    private static List<Long> FuncId = new ArrayList<>();

    @BeforeAll
    static void setup() throws SQLException, IOException {
        // Setup database connection
        String url = "jdbc:postgresql://localhost:5432/PerfomanceTest";
        String username = "postgres";
        String password = "lkroot";
        connection = DriverManager.getConnection(url, username, password);

        // Initialize repositories
        usersRepo = new UsersRepository();
        mathRepo = new MathFunctionsRepository();
        analyticRepo = new AnalyticFunctionsRepository();
        tabulatedRepo = new TabulatedFunctionsRepository();

        // Initialize tables
        usersRepo.initTable(connection);
        mathRepo.initTable(connection);
        analyticRepo.initTable(connection);
        tabulatedRepo.initTable(connection);


        // Prepare CSV file
        csvWriter = new FileWriter(CSV_FILE);
        csvWriter.write("Test Name,Records Processed,Execution Time (ms),Operations Per Second\n");
        csvWriter.flush();

        // Generate test data
        for (int i = 0; i < TOTAL_RECORDS; i++) {
            userNames.add("perfuser" + i);
        }
    }

    @AfterAll
    static void cleanup() throws IOException {
        if (csvWriter != null) {
            csvWriter.close();
        }
        System.out.println("Performance test completed! Results saved to " + CSV_FILE);
    }

    private void writeToCSV(String testName, int records, long timeMs) throws IOException {
        double opsPerSecond = records / (timeMs / 1000.0);
        csvWriter.write(String.format("%s,%d,%d,%.2f\n",
                testName, records, timeMs, opsPerSecond));
        csvWriter.flush();
        System.out.printf("%s: %d records in %d ms (%.2f ops/sec)%n",
                testName, records, timeMs, opsPerSecond);
    }

    @Test
    @Order(1)
    @DisplayName("Insert Users")
    void Insert_Users() throws IOException, SQLRepositoryException {

        long startTime = System.currentTimeMillis();
        int recordsInserted = 0;

        for (int i = 0; i < TOTAL_RECORDS; i++) {
            boolean isAdmin = i % 2 == 0;
            String name = userNames.get(i);
            String email = name + "@perftest.com";
            byte[] password = new byte[]{12};

            assertDoesNotThrow(() ->
                    usersRepo.insertUser(connection, isAdmin, name, email, password));

            recordsInserted++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Insert Users", recordsInserted, executionTime);
        assertEquals(TOTAL_RECORDS, recordsInserted);

        // Get user IDs for testing
        userId = new ArrayList<>();
        for (String name : userNames) {
            var idList = usersRepo.readUserId(connection, new String[]{name}, "-");
            if (!idList.isEmpty()) {
                userId.add(idList.get(0).id());
            }
        }
    }


    @Test
    @Order(3)
    @DisplayName("Insert Math Functions")
    void Insert_MathFunctions() throws IOException, SQLRepositoryException {

        long startTime = System.currentTimeMillis();
        int recordsInserted = 0;

        for (Long userId : userId) {
            String type = (recordsInserted<TOTAL_RECORDS/2)? "analytic" : "tabulated";
            assertDoesNotThrow(() ->
                    mathRepo.insertMFunc(connection, type, "func_" + userId, userId));

            recordsInserted++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        for (String name : userNames) {
            var idList = usersRepo.readUserFunctions(connection, null, new String[]{name});
            if (!idList.isEmpty()) {
                FuncId.add(idList.get(0).id());
            }
        }

        writeToCSV("Insert Math Functions", recordsInserted, executionTime);
        assertEquals(TOTAL_RECORDS, recordsInserted);
    }

    @Test
    @Order(4)
    @DisplayName("Insert Analytic Functions")
    void Insert_AnalyticFunctions() throws IOException {
        long startTime = System.currentTimeMillis();
        int recordsInserted = 0;

        for (int i=0; i<FuncId.size()/2;++i) {
            long fID = FuncId.get(i);
            String func_expr = "x^2 + "+fID;
            assertDoesNotThrow(() ->
                    analyticRepo.insertAnalyticFunction(connection,fID, func_expr));

            recordsInserted++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Insert Analytic Functions", recordsInserted, executionTime);
        assertEquals(TOTAL_RECORDS/2, recordsInserted);
    }

    @Test
    @Order(5)
    @DisplayName("Insert Tabulated Functions")
    void Insert_TabulatedFunctions() throws IOException {
        long startTime = System.currentTimeMillis();
        int recordsInserted = 0;

        for (int i=FuncId.size()/2; i<FuncId.size();++i) {
            long fID = FuncId.get(i);
            double[] xVals = new double[]{1,2,fID};
            double[] yVals = new double[]{0,0,fID};

            assertDoesNotThrow(() ->
                    tabulatedRepo.insertTabulatedFunction(connection,fID, xVals, yVals));

            recordsInserted++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Insert Tabulated Functions", recordsInserted, executionTime);
        assertEquals(TOTAL_RECORDS/2, recordsInserted);
    }

    @Test
    @Order(6)
    @DisplayName("Read User Info with Various Sorts")
    void Read_UserInfo() throws Exception {

        // Test different sorting options
        String[][] sortTests = {
                {"-", "-"},
                {"name_asc", "asc"},
                {"name_desc", "desc"},
                {"id_asc", "asc"},
                {"id_desc", "desc"},
                {"is_admin_asc", "asc"},
                {"is_admin_desc", "desc"}
        };

        for (String[] sortTest : sortTests) {
            String sortBy = sortTest[0];
            String sortOrder = sortTest[1];

            long startTime = System.currentTimeMillis();

            // Test with usernames
            assertDoesNotThrow(() ->
                    usersRepo.readUserInfo(connection, null,
                            userNames.toArray(new String[0]), sortBy, sortOrder));

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            writeToCSV("Read User Info by name - " + sortBy, TOTAL_RECORDS, executionTime);

            startTime = System.currentTimeMillis();
            // Test with user IDs
            long[] idArray = userId.stream().mapToLong(Long::longValue).toArray();
            assertDoesNotThrow(() ->
                    usersRepo.readUserInfo(connection, idArray, null, sortBy, sortOrder));

            endTime = System.currentTimeMillis();
            executionTime = endTime - startTime;
            writeToCSV("Read User Info by id - " + sortBy, TOTAL_RECORDS, executionTime);

        }
    }

    @Test
    @Order(7)
    @DisplayName("Read User ID")
    void Read_UserId() throws IOException {

        String[] sortOrders = {"-", "asc", "desc"};

        for (String sortOrder : sortOrders) {
            long startTime = System.currentTimeMillis();

            assertDoesNotThrow(() ->
                    usersRepo.readUserId(connection,
                            userNames.toArray(new String[0]), sortOrder));

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            writeToCSV("Read User ID - " + sortOrder, TOTAL_RECORDS, executionTime);
        }
    }

    @Test
    @Order(8)
    @DisplayName("Read Users by Role")
    void Read_UsersByRole() throws IOException {

        long startTime = System.currentTimeMillis();

        assertDoesNotThrow(() ->
                usersRepo.readUserId(connection,
                        userNames.toArray(new String[0]), "asc"));

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Read Users by Role ASC", TOTAL_RECORDS , executionTime);


        startTime = System.currentTimeMillis();

        assertDoesNotThrow(() ->
                usersRepo.readUserId(connection,
                        userNames.toArray(new String[0]), "desc"));


        endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;

        writeToCSV("Read Users by Role DESC", TOTAL_RECORDS , executionTime);
    }


    @Test
    @Order(9)
    @DisplayName("Read User Functions")
    void Read_UserFunctions() throws Exception {

        long startTime = System.currentTimeMillis();

        // Test with user IDs
        long[] idArray = userId.stream().mapToLong(Long::longValue).toArray();
        assertDoesNotThrow(() ->
                usersRepo.readUserFunctions(connection, idArray, null));

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Read User Functions by ID", TOTAL_RECORDS, executionTime);


        startTime = System.currentTimeMillis();
        // Test with usernames
        assertDoesNotThrow(() ->
                usersRepo.readUserFunctions(connection, null,
                        userNames.toArray(new String[0])));

        endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;

        writeToCSV("Read User Functions by Name", TOTAL_RECORDS/2 , executionTime);
    }

    @Test
    @Order(10)
    @DisplayName("Read MFunc Info")
    void Read_MathFunctionInfo() throws Exception {

        // Test different sorting options
        String[][] sortTests = {
                {"-", "-"},
                {"name_asc", "asc"},
                {"name_desc", "desc"},
                {"id_asc", "asc"},
                {"id_desc", "desc"},
                {"is_admin_asc", "asc"},
                {"is_admin_desc", "desc"}
        };

        for (String[] sortTest : sortTests) {
            String sortBy = sortTest[0];
            String sortOrder = sortTest[1];

            long startTime = System.currentTimeMillis();

            long[] idArray = FuncId.stream().mapToLong(Long::longValue).toArray();
            assertDoesNotThrow(() ->
                    mathRepo.readMFuncInfo(connection, idArray, sortBy, sortOrder));

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            writeToCSV("Read Mfunction Info - " + sortBy, TOTAL_RECORDS, executionTime);
        }
    }

    @Test
    @Order(11)
    @DisplayName("Read Analytic Info")
    void Read_AnalyticInfo() throws Exception {

        long startTime = System.currentTimeMillis();

        // Test with user IDs
        long[] idArray = FuncId.stream().mapToLong(Long::longValue).toArray();
        assertDoesNotThrow(() ->
                analyticRepo.readAnalyticFunctionInfo(connection, idArray));

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Read Analytic Functions ", TOTAL_RECORDS , executionTime);
    }

    @Test
    @Order(12)
    @DisplayName("Read Tabulated Info")
    void Read_TabulatedInfo() throws Exception {

        long startTime = System.currentTimeMillis();

        // Test with user IDs
        long[] idArray = FuncId.stream().mapToLong(Long::longValue).toArray();
        assertDoesNotThrow(() ->
                tabulatedRepo.readTabulatedFunctionInfo(connection, idArray));

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Read Tabulated Functions ", TOTAL_RECORDS , executionTime);
    }


    @Test
    @Order(13)
    @DisplayName("Update Users")
    void Update_Users() throws IOException {

        long startTime = System.currentTimeMillis();
        int recordsUpdated = 0;

        for (int i = 0; i < TOTAL_RECORDS; i++) {
            String oldName = userNames.get(i);
            String newName = "updated_" + oldName;

            assertDoesNotThrow(() ->
                    usersRepo.updateUser(connection, null, oldName, null, newName, null, null));

            // Update the name in our list
            userNames.set(i, newName);
            recordsUpdated++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Update Users", recordsUpdated, executionTime);
        assertEquals(TOTAL_RECORDS, recordsUpdated);
    }

    @Test
    @Order(14)
    @DisplayName("Update Math Functions")
    void Update_MathFunctions() throws IOException, SQLRepositoryException {

        int recordsUpdated = 0;
        long extraTime =0;
        long startTime = System.currentTimeMillis();


        for (long owner_id : userId) {
            long owner = owner_id;

            long pauseStart = System.currentTimeMillis();
            long func_id = usersRepo.readUserFunctions(connection, new long[]{owner},null).get(0).id();
            long pauseEnd = System.currentTimeMillis();
            extraTime += pauseEnd - pauseStart;

            String newName = "updated_" + func_id;

            assertDoesNotThrow(() ->
                    mathRepo.updateMFunc(connection, owner, func_id, newName));

            recordsUpdated++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = (endTime - startTime) - extraTime;

        writeToCSV("Update Math Function", recordsUpdated, executionTime);
        assertEquals(TOTAL_RECORDS, recordsUpdated);
    }


    @Test
    @Order(15)
    @DisplayName("Update Analytic Functions")
    void Update_Analytic() throws IOException, SQLRepositoryException {

        int recordsUpdated = 0;
        long startTime = System.currentTimeMillis();


        for (int i=0; i<FuncId.size()/2;++i) {
            long fID = FuncId.get(i);
            String newfunc_expr = "x^3 + "+fID;
            assertDoesNotThrow(() ->
                    analyticRepo.updateAnalyticFunction(connection,fID, newfunc_expr));

            recordsUpdated++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = (endTime - startTime);

        writeToCSV("Update Analytic Function", recordsUpdated, executionTime);
        assertEquals(TOTAL_RECORDS/2, recordsUpdated);
    }

    @Test
    @Order(16)
    @DisplayName("Update Tabulated Functions every way")
    void Update_Tabulated() throws IOException, SQLRepositoryException {

        int recordsUpdated = 0;
        long startTime = System.currentTimeMillis();

        for(int i=FuncId.size()/2; i<FuncId.size();++i){
            long id = FuncId.get(i);
            double[] xVals= new double[]{1,2,3};
            assertDoesNotThrow(() -> tabulatedRepo.updateTabulatedFunctionFull(connection,id, xVals, xVals));
            recordsUpdated++;
        }
        long endTime = System.currentTimeMillis();
        long executionTime = (endTime - startTime);
        writeToCSV("Update Tabulated Function Full", recordsUpdated, executionTime);
        assertEquals(TOTAL_RECORDS/2, recordsUpdated);

        recordsUpdated =0;
        startTime = System.currentTimeMillis();
        for(int i=FuncId.size()/2; i<FuncId.size();++i){
            long id = FuncId.get(i);
            int yind = 1;
            double yval = -5;
            assertDoesNotThrow(() -> tabulatedRepo.updateTabulatedFunctionIndex(connection,id, yind, yval));
            assertDoesNotThrow(() -> tabulatedRepo.updateTabulatedFunctionIndex(connection,id, yind, Double.NaN));
            recordsUpdated++;
        }

        endTime = System.currentTimeMillis();
        executionTime = (endTime - startTime);

        writeToCSV("Update Tabulated Function by Index", recordsUpdated, executionTime);
        assertEquals(TOTAL_RECORDS/2, recordsUpdated);
    }

    @Test
    @Order(17)
    @DisplayName("Remove Functions")
    void Remove_Funcitons() throws IOException, SQLRepositoryException {

        int recordsRemoved =0;
        long extraTime =0;
        long startTime = System.currentTimeMillis();

        for (long owner_id : userId) {
            long owner = owner_id;

            long pauseStart = System.currentTimeMillis();
            long func_id = usersRepo.readUserFunctions(connection, new long[]{owner},null).get(0).id();
            long pauseEnd = System.currentTimeMillis();
            extraTime += pauseEnd - pauseStart;

            assertDoesNotThrow(() ->
                    mathRepo.removeMFunc(connection, owner, func_id));

            recordsRemoved++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = (endTime - startTime) - extraTime;

        writeToCSV("Remove Functions", recordsRemoved, executionTime);
        assertEquals(TOTAL_RECORDS, recordsRemoved);
    }

    @Test
    @Order(18)
    @DisplayName("Remove Users")
    void Remove_Users() throws IOException {

        long startTime = System.currentTimeMillis();
        int recordsRemoved = 0;

        // Then remove users
        for (String userName : userNames) {
            assertDoesNotThrow(() ->
                    usersRepo.removeUser(connection, null, userName));

            recordsRemoved++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        writeToCSV("Remove Users", recordsRemoved, executionTime);
        assertEquals(TOTAL_RECORDS, recordsRemoved);
    }
}
