package edu.lu.uni.serval.javabusinesslocs.cli;

import edu.lu.uni.serval.javabusinesslocs.locations.BusinessLocation;
import edu.lu.uni.serval.javabusinesslocs.locations.InvocationLocation;
import edu.lu.uni.serval.javabusinesslocs.locations.TypeReferenceLocation;
import edu.lu.uni.serval.javabusinesslocs.locator.FileRequest;
import edu.lu.uni.serval.javabusinesslocs.locator.LocationsCollector;
import edu.lu.uni.serval.javabusinesslocs.output.*;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static edu.lu.uni.serval.javabusinesslocs.locations.BusinessLocation.IF_CONDITIONS_AS_TKN;
import static edu.lu.uni.serval.javabusinesslocs.locations.BusinessLocation.CONDITIONS_AS_TKN;
import static org.junit.Assert.*;

public class CliRequestTest {

    private static final String FILE_3 = "src/test/resources/javafile/Role.java";
    private static final String FILE_4 = "src/test/resources/javafile/User.java";
    private static final String FILE_5 = "src/test/resources/javafile/UserRole.java";
    private static final String file_with_static_invocation = "src/test/resources/javafile/DummyClassWithStaticInvocation.java";
    private static final String file_with_field_reference = "src/test/resources/javafile/DummyClass.java";
    private static final String file_with_else_if = "src/test/resources/javafile/DummyClassWithElseIf.java";
    private static final String file_multimaps = "src/test/resources/javafile/multimaps.java";
    private static final String file_with_less = "src/test/resources/javafile/DummyClasswithless.java";

    private static final String file_with_nested_else_if = "src/test/resources/javafile/DummyClassWithNestedElseIf.java";
    private static final String dummy_file_with_loop = "src/test/resources/javafile/DummyClassWithLoop.java";
    private static final String dummy_file_with_constructor = "src/test/resources/javafile/DummyClassWithConstructor.java";
    private static final String dummy_file_with_for = "src/test/resources/javafile/DummyClassWithFor.java";
    private static final String file_with_try_catch = "src/test/resources/javafile/DummyClassWithTryCatch.java";
    private static final String file_1 = "src/test/resources/javafile/ArgumentImpl.java";
    private static final String nested_if_conditions = "src/test/resources/javafile/DummyClassWithIfNestedCdt.java";
    private static final String DummyClassWithElseIfWithBrackets = "src/test/resources/javafile/DummyClassWithElseIfWithBrackets.java";
    private static final String DummyClassWithInstanceOf = "src/test/resources/javafile/DummyClassWithInstanceOf.java";

    private static final String lines_1_str = "109@115@124@126";
    private static final List<Integer> lines_1 = new ArrayList<Integer>() {{
        add(109);
        add(115);
        add(124);
        add(126);
    }};

    private static final String file_2 = "src/test/resources/javafile/WriteableCommandLine.java";
    private static final String lines_2_str = "47";
    private static final List<Integer> lines_2 = new ArrayList<Integer>() {{
        add(47);
    }};

    private final static FileRequest IFREQ1 = new FileRequest(file_1, null, lines_1);
    private final static FileRequest EFREQ1 = new ExcludeFileRequest(file_1, null, lines_1);
    private final static FileRequest IFREQ2 = new FileRequest(file_2, null, lines_2);
    private final static FileRequest EFREQ2 = new ExcludeFileRequest(file_2, null, lines_2);

    private static Path outputDir;
    private static Path expectedDir;

    @BeforeClass
    public static void beforeClass() throws Exception {
        expectedDir = Paths.get("src/test/resources/expected").resolve(CliRequestTest.class.getSimpleName());
        outputDir = Paths.get("src/test/resources/tmp").resolve(CliRequestTest.class.getSimpleName());
        Files.createDirectories(outputDir);
    }

    @Before
    public void setUp() {
        // before every method we make sure that the static vars are set to their default values.
        IF_CONDITIONS_AS_TKN = false;
        CONDITIONS_AS_TKN = false;
    }

    private File[] getOutputAndExpectedFiles(String dir) throws IOException {
        Path expectDir = expectedDir.resolve(dir);
       assertTrue(expectDir.toFile().isDirectory());
        Path expectedJson = expectDir.resolve(LocationsCollector.DEFAULT_JSON_LOCATIONS_FILE_NAME);
        File expectedFile = expectedJson.toFile();
        assertTrue(expectedFile.isFile());

        Path outDir = outputDir.resolve(dir);
        Files.createDirectories(outDir);
        assertTrue(outputDir.toFile().isDirectory());
        File outFile = outDir.resolve(LocationsCollector.DEFAULT_JSON_LOCATIONS_FILE_NAME).toFile();

        return new File[]{expectedFile, outDir.toFile(), outFile};
    }

    @Test
    public void parseArgs_multiple_included_files_multiple_lines() {
        int requested_number_of_locs = 5;
        String[] req = {"-in=" + file_1 + "::" + lines_1_str, "-in=" + file_2 + "::" + lines_2_str, "-out=out", "-n="+requested_number_of_locs};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        assertNotNull(cliRequest.fileRequests);
        assertEquals("out", cliRequest.outputDir);
        assertEquals(2, cliRequest.fileRequests.size());
        assertEquals(new Integer(requested_number_of_locs), cliRequest.numberOfTokens);
        FileRequest req1 = cliRequest.fileRequests.get(0);
        FileRequest req2 = cliRequest.fileRequests.get(1);
        assertEquals(IFREQ1, req1);
        assertEquals(IFREQ2, req2);
        assertTrue(file_1.equals(req1.getJavaFilePath()) && lines_1.containsAll(req1.getLinesToMutate()) && req1.getLinesToMutate().containsAll(lines_1)
                && file_2.equals(req2.getJavaFilePath()) && lines_2.containsAll(req2.getLinesToMutate()) && req2.getLinesToMutate().containsAll(lines_2)
        );
    }

    // fixme move this somewhere else
    @Test
    public void test_equals_INFR_EXFR() {
        assertNotEquals(IFREQ1, EFREQ1);
        assertNotEquals(IFREQ2, EFREQ2);
        assertNotEquals(EFREQ1, EFREQ2);
        assertNotEquals(IFREQ1, IFREQ2);
    }

    @Test
    public void parseArgs_multiple_excluded_files_multiple_lines() {
        String[] req = {"-ex=" + file_1 + "::" + lines_1_str, "-ex=" + file_2 + "::" + lines_2_str, "-out=out"};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        assertNotNull(cliRequest.fileRequests);
        assertEquals("out", cliRequest.outputDir);
        assertEquals(2, cliRequest.fileRequests.size());
        assertNull(cliRequest.numberOfTokens);
        FileRequest req1 = cliRequest.fileRequests.get(0);
        FileRequest req2 = cliRequest.fileRequests.get(1);
        assertTrue(req1 instanceof ExcludeFileRequest);
        assertTrue(req2 instanceof ExcludeFileRequest);
        assertEquals(EFREQ1, req1);
        assertEquals(EFREQ2, req2);
        assertTrue(file_1.equals(req1.getJavaFilePath()) && lines_1.containsAll(req1.getLinesToMutate()) && req1.getLinesToMutate().containsAll(lines_1)
                && file_2.equals(req2.getJavaFilePath()) && lines_2.containsAll(req2.getLinesToMutate()) && req2.getLinesToMutate().containsAll(lines_2)
        );
    }



    @Test
    public void sys_test__process_one_files_multiple_lines() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__process_one_files_multiple_lines");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        int requested_number_of_locs = 5;

        String[] req = {"-in=" + file_1 + "::" + lines_1_str, "-out=" + outDir, "-n="+requested_number_of_locs};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        cliRequest.start();
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__process_one_file_no_lines() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__process_one_file_no_lines");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        int requested_number_of_locs = 5;

        String[] req = {"-in=" + file_1, "-out=" + outDir, "-n="+requested_number_of_locs, "-selection=random"};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_1, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);

        assertEquals("org.apache.commons.cli2.option.ArgumentImpl", cp.getQualifiedName());
        int n = 0;
        for (MethodLocations m : cp.getMethodPredictions()) {
            for (LineLocations l : m.getLine_predictions()) {
                n += l.getLocations().size();
            }
        }
        assertEquals(requested_number_of_locs, n);
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__process_3_files_no_lines() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__process_3_files_no_lines");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + FILE_3, "-in=" + FILE_4, "-in=" + FILE_5, "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        cliRequest.start();
        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__while_condition_location() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__while_condition_location");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];
        String[] req = {"-in=" + dummy_file_with_loop + "::" , "-out=" + outDir};
        CONDITIONS_AS_TKN = true;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(dummy_file_with_loop, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);

        assertEquals("DummyClassWithLoop", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(7, methodP.getEndLineNumber());
        assertEquals("main(java.lang.String[])", methodP.getMethodSignature());
        assertEquals(3, methodP.getLine_predictions().size());

        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(3, lp1.getLine_number());
        assertEquals(1, lp1.getLocations().size());

        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(4, lp2.getLine_number());
        assertEquals(4, lp2.getLocations().size());

        LineLocations lp3 = methodP.getLine_predictions().get(2);
        assertEquals(5, lp3.getLine_number());
        assertEquals(2, lp3.getLocations().size());


        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__for_condition_location() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__for_condition_location");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];
        String[] req = {"-in=" + dummy_file_with_for + "::" , "-out=" + outDir};
        CONDITIONS_AS_TKN = true;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(dummy_file_with_for, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);

        assertEquals("DummyClassWithFor", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(8, methodP.getEndLineNumber());
        assertEquals("main(java.lang.String[])", methodP.getMethodSignature());
        assertEquals(3, methodP.getLine_predictions().size());

        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(3, lp1.getLine_number());
        assertEquals(1, lp1.getLocations().size());

        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(4, lp2.getLine_number());
        assertEquals(7, lp2.getLocations().size());

        LineLocations lp3 = methodP.getLine_predictions().get(2);
        assertEquals(5, lp3.getLine_number());
        assertEquals(6, lp3.getLocations().size());

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__process_1_if_nested_conditions() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__process_1_if_nested_conditions");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String in_class = nested_if_conditions;
        String[] req = {"-in=" + in_class, "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(in_class, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithIf", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(4, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(36, 114), methodP.getCodePosition());
        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp = methodP.getLine_predictions().get(0);
        assertEquals(3, lp.getLine_number());
        assertEquals(7, lp.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }


    @Test
    public void sys_test__file__DummyClassWithElseIfWithBrackets() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__file__DummyClassWithElseIfWithBrackets");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String in_class = DummyClassWithElseIfWithBrackets;
        String[] req = {"-in=" + in_class, "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(in_class, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithElseIfWithBrackets", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(8, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(52, 194), methodP.getCodePosition());
        assertEquals(4, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        LineLocations lp3 = methodP.getLine_predictions().get(2);
        LineLocations lp4 = methodP.getLine_predictions().get(3);
        assertEquals(3, lp1.getLine_number());
        assertEquals(4, lp2.getLine_number());
        assertEquals(5, lp3.getLine_number());
        assertEquals(6, lp4.getLine_number());
        assertEquals(1, lp1.getLocations().size());
        assertEquals(1, lp2.getLocations().size());
        assertEquals(3, lp3.getLocations().size());
        assertEquals(3, lp4.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }


    @Test
    public void sys_test__nested_else_if__mask_full_cdt_disabled() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__nested_else_if__mask_full_cdt_disabled");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_nested_else_if + "::", "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_nested_else_if, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithElseIf", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(9, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(40, 198), methodP.getCodePosition());
        assertEquals(3, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(3, lp1.getLine_number());
        assertEquals(5, lp2.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        assertEquals(3, lp2.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__try_catch() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__try_catch");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_try_catch + "::", "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_try_catch, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithTryCatch", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(10, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(42, 235), methodP.getCodePosition());
        assertEquals(3, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        LineLocations lp3 = methodP.getLine_predictions().get(2);
        assertEquals(4, lp1.getLine_number());
        assertEquals(6, lp2.getLine_number());
        assertEquals(7, lp3.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        assertEquals(3, lp2.getLocations().size());
        assertEquals(3, lp3.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }


    @Test
    public void sys_test__nested_else_if__mask_full_cdt_enabled() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__nested_else_if__mask_full_cdt_enabled");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_nested_else_if + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = true;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_nested_else_if, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithElseIf", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(9, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(40, 198), methodP.getCodePosition());
        assertEquals(3, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        LineLocations lp3 = methodP.getLine_predictions().get(2);
        assertEquals(3, lp1.getLine_number());
        assertEquals(5, lp2.getLine_number());
        assertEquals(6, lp3.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        assertEquals(4, lp2.getLocations().size());
        assertEquals(3, lp3.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }


    @Test
    public void sys_test__else_if__mask_full_cdt_disabled() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__else_if__mask_full_cdt_disabled");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_else_if + "::", "-out=" + outDir};
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_else_if, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithElseIf", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(5, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(40, 142), methodP.getCodePosition());
        assertEquals(2, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(3, lp1.getLine_number());
        assertEquals(4, lp2.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        assertEquals(6, lp2.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }


    @Test
    public void sys_test__else_if__mask_full_cdt_enabled() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__else_if__mask_full_cdt_enabled");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_else_if + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = true;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_else_if, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithElseIf", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(2, methodP.getStartLineNumber());
        assertEquals(5, methodP.getEndLineNumber());
        assertEquals("meth(int,int)", methodP.getMethodSignature());
        assertEquals(new CodePosition(40, 142), methodP.getCodePosition());
        assertEquals(2, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(3, lp1.getLine_number());
        assertEquals(4, lp2.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        assertEquals(7, lp2.getLocations().size());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__static_invocation() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__static_invocation");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_static_invocation + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_static_invocation, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithStaticInvocation", cp.getQualifiedName());
        assertEquals(4, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(214, methodP.getCodePosition().getStartPosition());
        assertEquals(460, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(11, lp1.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());
        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 447 );
        assertEquals(invocation.getCodePosition().getEndPosition(), 451 );
        //reference
        TypeReferenceLocation reference = (TypeReferenceLocation) locs.get(1);
        assertEquals(reference.getCodePosition().getStartPosition(), 440);
        assertEquals(reference.getCodePosition().getEndPosition(), 445);

        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__static_invocation_full_qualified_name() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__static_invocation_full_qualified_name");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_static_invocation + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_static_invocation, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithStaticInvocation", cp.getQualifiedName());
        assertEquals(4, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(1);
        assertEquals(467, methodP.getCodePosition().getStartPosition());
        assertEquals(743, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(16, lp1.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());

        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 730);
        assertEquals(invocation.getCodePosition().getEndPosition(), 734);

        //reference
        TypeReferenceLocation reference = (TypeReferenceLocation) locs.get(1);
        assertEquals(reference.getCodePosition().getStartPosition(), 706);
        assertEquals(reference.getCodePosition().getEndPosition(), 728);

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__static_invocation_method_with_arguments() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__static_invocation_method_with_arguments");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_static_invocation + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_static_invocation, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithStaticInvocation", cp.getQualifiedName());
        assertEquals(4, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(2);
        assertEquals(750, methodP.getCodePosition().getStartPosition());
        assertEquals(1058, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(21, lp1.getLine_number());
        assertEquals(4, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());

        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 1028);
        assertEquals(invocation.getCodePosition().getEndPosition(), 1033);

        //reference
        TypeReferenceLocation reference = (TypeReferenceLocation) locs.get(1);
        assertEquals(reference.getCodePosition().getStartPosition(), 1004);
        assertEquals(reference.getCodePosition().getEndPosition(), 1026);

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__object_method_invocation() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__object_method_invocation");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_static_invocation + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_static_invocation, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("DummyClassWithStaticInvocation", cp.getQualifiedName());
        assertEquals(4, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(3);
        assertEquals(1065, methodP.getCodePosition().getStartPosition());
        assertEquals(1278, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(27, lp1.getLine_number());
        assertEquals(2, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());

        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 1265);
        //assertEquals(invocation.getCodePosition().getEndPosition(), 1268);

        BusinessLocation object = (BusinessLocation) locs.get(1);
        assertEquals(object.getCodePosition().getStartPosition(), 1262);
        assertEquals(object.getCodePosition().getEndPosition(), 1263);

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__field_reference() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__field_reference");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_field_reference + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_field_reference, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("org.apache.commons.cli2.option.DummyClass", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(177, methodP.getCodePosition().getStartPosition());
        assertEquals(292, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(9, lp1.getLine_number());
        assertEquals(7, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());
        assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__multimaps() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__multimaps");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_multimaps + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_multimaps, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("org.assertj.vavr.internal.multimaps", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(1984, methodP.getCodePosition().getStartPosition());
        //assertEquals(2564, methodP.getCodePosition().getEndPosition());

        assertEquals(8, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(1);
        assertEquals(43, lp1.getLine_number());
        //assertEquals(3, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());

        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 2219);
        assertEquals(invocation.getCodePosition().getEndPosition(), 2233);

        //reference
        BusinessLocation location = (BusinessLocation) locs.get(1);
        assertEquals(location.getCodePosition().getStartPosition(), 2208);
        assertEquals(location.getCodePosition().getEndPosition(), 2217);

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__less() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__less");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + file_with_less + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(file_with_less, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("org.assertj.vavr.internal.DummyClasswithless", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(1934, methodP.getCodePosition().getStartPosition());
        assertEquals(2073, methodP.getCodePosition().getEndPosition());

        assertEquals(1, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(40, lp1.getLine_number());
        assertEquals(7, lp1.getLocations().size());
        List<Location> locs = new ArrayList<Location>(lp1.getLocations());

        //invocation
        InvocationLocation invocation = (InvocationLocation) locs.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 2012);
        assertEquals(invocation.getCodePosition().getEndPosition(), 2017);


        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }

    @Test
    public void sys_test__constructor() throws IOException {
        File[] files = getOutputAndExpectedFiles("sys_test__constructor");
        File expectedFile = files[0];
        File outDir = files[1];
        File outFile = files[2];

        String[] req = {"-in=" + dummy_file_with_constructor + "::", "-out=" + outDir};
        IF_CONDITIONS_AS_TKN = false;
        CliRequest cliRequest = CliRequest.parseArgs(req);
        LocationsCollector locator = cliRequest.start();
        List<FileLocations> fileLocations = locator.getItems();
        assertTrue("nothing parse!", fileLocations != null && !fileLocations.isEmpty());
        assertEquals("wrong files parsed!", 1, fileLocations.size());
        FileLocations fileLocation = fileLocations.get(0);
        assertEquals(dummy_file_with_constructor, fileLocation.getFile_path());
        assertEquals(1, fileLocation.getClassPredictions().size());
        ClassLocations cp = fileLocation.getClassPredictions().get(0);
        assertEquals("org.assertj.vavr.api.AbstractValidationAssert", cp.getQualifiedName());
        assertEquals(1, cp.getMethodPredictions().size());
        MethodLocations methodP = cp.getMethodPredictions().get(0);
        assertEquals(360, methodP.getCodePosition().getStartPosition());
        assertEquals(625, methodP.getCodePosition().getEndPosition());

        assertEquals(3, methodP.getLine_predictions().size());
        LineLocations lp1 = methodP.getLine_predictions().get(0);
        assertEquals(13, lp1.getLine_number());
        assertEquals(3, lp1.getLocations().size());
        List<Location> locs_lp1 = new ArrayList<Location>(lp1.getLocations());
        InvocationLocation invocation = (InvocationLocation) locs_lp1.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 449);
        assertEquals(invocation.getCodePosition().getEndPosition(), 453);

        LineLocations lp2 = methodP.getLine_predictions().get(1);
        assertEquals(14, lp2.getLine_number());
        List<Location> locs_lp2 = new ArrayList<Location>(lp2.getLocations());
        invocation = (InvocationLocation) locs_lp2.get(0);
        assertEquals(invocation.getCodePosition().getStartPosition(), 516);
        assertEquals(invocation.getCodePosition().getEndPosition(), 528);

        invocation = (InvocationLocation) locs_lp2.get(1);
        assertEquals(invocation.getCodePosition().getStartPosition(), 505);
        assertEquals(invocation.getCodePosition().getEndPosition(), 512);

        //assertTrue("The files differ!", FileUtils.contentEquals(expectedFile, outFile));
    }




    @AfterClass
    public static void afterClass() throws Exception {
       //FileUtils.deleteDirectory(outputDir.toFile());
    }
}