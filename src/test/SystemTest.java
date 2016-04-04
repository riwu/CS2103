package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import defaultPart.Logic;
import defaultPart.Recur;
import defaultPart.Recur.TimeUnit;
import defaultPart.Storage;
import defaultPart.Task;
import defaultPart.TaskDate;
import defaultPart.TaskTime;

public class SystemTest {

	/* Location to load/save the expected test results */
	private static final String EXPECTED_FILE_NAME = "test\\SystemTest_expected.xml";
	private static final String TEST_FILE_NAME = "test\\SystemTest_actual.xml";

	/**
	 * Settings for XML file comparison using XMLUnit
	 */
	@Before
	public void runBeforeEveryTest() {

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		XMLUnit.setNormalizeWhitespace(true);
	}

	/**
	 * Helper function to create the a recur item based on presence of parameters
	 * 
	 * @throws ParseException
	 */
	private Recur createRecur(String timeUnit, int frequency, String startOfRecur, String endOfRecur)
			throws ParseException {

		Recur newRecur = new Recur();

		newRecur.setTimeUnit(TimeUnit.valueOf(timeUnit));
		newRecur.setFrequency(frequency);
		TaskDate startDate = new TaskDate();
		startDate.setDateFromString(startOfRecur);
		newRecur.setStartDate(startDate);
		if (endOfRecur != null) {
			TaskDate endDate = new TaskDate();
			endDate.setDateFromString(endOfRecur);
			newRecur.setEndDate(endDate);
		}
		return newRecur;

	}

	/**
	 * Helper function to create the expected file to compare with the one generated by logic
	 * 
	 * @param expectedFile
	 *            Expected file to save
	 * @throws SAXException
	 * @throws ParseException
	 */
	private void storageCreateExpectedTask(Storage storage, File expectedFile, String description,
			String date, String startTime, String endTime, Boolean completed, Recur recur)
					throws SAXException, ParseException {

		Task newTask = new Task();
		newTask.setDescription(description);

		if (date != null) {
			TaskDate calDate = new TaskDate();
			calDate.setDateFromString(date);
			newTask.setDate(calDate);
		}
		if (startTime != null) {
			TaskTime calStartTime = new TaskTime();
			calStartTime.setTimeFromString(startTime);
			newTask.setStartTime(calStartTime);
		}
		if (endTime != null) {
			TaskTime calEndTime = new TaskTime();
			calEndTime.setTimeFromString(endTime);
			newTask.setEndTime(calEndTime);
		}
		if (completed) {
			newTask.toggleCompleted();
		}
		if (recur != null) {
			newTask.setRecur(recur);
		}
		storage.addToTaskList(newTask);
		storage.saveTasksToFile();
	}

	/**
	 * Helper function to execute the command through logic and saving it through its storage
	 * 
	 * @param cmd
	 *            command to be parsed and executed
	 * @throws SAXException
	 */
	private void logicExecuteCommand(Logic logic, String cmd) throws SAXException {

		logic.executeCommand(cmd);
		logic.saveTasksToFile();
	}

	@Test
	public final void testAddFloatingTask() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "CS2103T Reading");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "CS2103T Reading", null, null, null, false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineWithNumberInDescription()
			throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "500 words CFG1010 8/4");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "500 words CFG1010", "8/4/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineWithTime() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan Jap Trip 30/1/2016 11am");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan Jap Trip", "30/1/2016", "11:00AM", null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddEvent() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 30/1/2016 11am-12pm");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "30/1/2016", "11:00AM",
				"12:00PM", false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineRecurringDay() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Go out with girlfriend 1/9 3d 15");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Go out with girlfriend", "01/9/2016", null, null,
				false, createRecur("DAY", 3, "01/9/216", "16/10/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineRecurringDayWithWraparoundYear()
			throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Go out with girlfriend 1/4 3d 15");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Go out with girlfriend", "01/4/2017", null, null,
				false, createRecur("DAY", 3, "01/4/2017", "16/5/2017"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineRecurringWeek() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Go out with jully 2/7 1w 2");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Go out with jully", "02/7/2016", null, null, false,
				createRecur("WEEK", 1, "02/7/216", "16/7/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddDeadlineRecurringEndWithNumber()
			throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "HIMYM 1/1/2027 12:00 1d 50");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "HIMYM", "1/1/2027", "12:00PM", null, false,
				createRecur("DAY", 1, "1/1/2027", "20/2/2027"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testAddEventRecurringDay() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am-12pm 1d 10");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("DAY", 1, "1/5/2016", "11/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditDescription() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "CS2103T Reading");
		logicExecuteCommand(logic, "e 1 Potato Reading");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Potato Reading", null, null, null, false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditDate() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Tomato Reading 11/4/2016");
		logicExecuteCommand(logic, "e 1 20/4/2016");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Tomato Reading", "20/4/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditStartTime() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Tomato Reading 11/4/2016 2pm");
		logicExecuteCommand(logic, "e 1 3pm");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Tomato Reading", "11/4/2016", "3:00PM", null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditStartTimeFromEvent() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Cabbage Reading 11/4/2016 2pm-4pm");
		logicExecuteCommand(logic, "e 1 3pm");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Cabbage Reading", "11/4/2016", "3:00PM", null,
				false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditEndTime() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Cucumber Reading 11/4/2016 2pm-4pm");
		logicExecuteCommand(logic, "e 1 2pm-5pm");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Cucumber Reading", "11/4/2016", "2:00PM", "5:00PM",
				false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditRecurTimeUnit() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am-12pm 1d 1");
		logicExecuteCommand(logic, "e 1 1w 1");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("WEEK", 1, "1/5/2016", "8/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditRecurFrequency() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am-12pm 1d 1");
		logicExecuteCommand(logic, "e 1 3d 1");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("DAY", 3, "1/5/2016", "4/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditRecurTimesToRecur() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am-12pm 1d 1");
		logicExecuteCommand(logic, "e 1 1d 5");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("DAY", 1, "1/5/2016", "6/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditRecurEndOfRecur() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am-12pm 1d 1/1/2016");
		logicExecuteCommand(logic, "e 1 1d 10/1/2016");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("DAY", 1, "1/5/2016", "10/1/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testEditInvalidIndex() throws SAXException, IOException, ParseException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy some potatoes 1/5/2016 11am/12pm 1d 1/1/2016");
		logicExecuteCommand(logic, "e 2 Buy Tomatoes Instead");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Buy some potatoes", "1/5/2016", "11:00AM",
				"12:00PM", false, createRecur("DAY", 1, "1/5/2016", "1/1/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testFindWord() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trips Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "f Trip");
		List<Integer> actualIndexList = logic.getIndexesFound();

		// Setting up expected Task List for comparison
		List<Integer> expectedList = new ArrayList<Integer>();
		expectedList.add(0);
		expectedList.add(2);

		// This is to test the expected behavior of this function
		assertEquals(expectedList, actualIndexList);
	}

	@Test
	public final void testFindCapitalizedWord() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trips Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "f TRIP");
		List<Integer> actualIndexList = logic.getIndexesFound();

		// Setting up expected Task List for comparison
		List<Integer> expectedList = new ArrayList<Integer>();
		expectedList.add(0);
		expectedList.add(2);

		// This is to test the expected behavior of this function
		assertEquals(expectedList, actualIndexList);
	}

	@Test
	public final void testFindUnorderedWords() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "f trip plan");
		List<Integer> actualIndexList = logic.getIndexesFound();

		// Setting up expected Task List for comparison
		List<Integer> expectedList = new ArrayList<Integer>();
		expectedList.add(0);
		expectedList.add(2);

		// This is to test the expected behavior of this function
		assertEquals(expectedList, actualIndexList);

	}

	@Test
	public final void testDeleteFirstIndex() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "d 1");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "1/5/2016", null, null, false,
				null);
		storageCreateExpectedTask(storage, expectedFile, "Fly to Japan", "1/5/2016", null, null, false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteMiddleIndex() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "d 2");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "1/5/2016", null, null, false,
				null);
		storageCreateExpectedTask(storage, expectedFile, "Trip Japan Plan", "1/5/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteLastIndex() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "d 3");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Fly to Japan", "1/5/2016", null, null, false, null);
		storageCreateExpectedTask(storage, expectedFile, "Trip Japan Plan", "1/5/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteInvalidIndex() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "d 4");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "1/5/2016", null, null, false,
				null);
		storageCreateExpectedTask(storage, expectedFile, "Fly to Japan", "1/5/2016", null, null, false, null);
		storageCreateExpectedTask(storage, expectedFile, "Trip Japan Plan", "1/5/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteFloatingTasks() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy Potatoes");
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Eat Potatoes");
		logicExecuteCommand(logic, "Fly to Japan 1/5/2016");
		logicExecuteCommand(logic, "Cook Potatoes");
		logicExecuteCommand(logic, "Trip Japan Plan 1/5/2016");
		logicExecuteCommand(logic, "Love Potatoes");
		logicExecuteCommand(logic, "d -");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "1/5/2016", null, null, false,
				null);
		storageCreateExpectedTask(storage, expectedFile, "Fly to Japan", "1/5/2016", null, null, false, null);
		storageCreateExpectedTask(storage, expectedFile, "Trip Japan Plan", "1/5/2016", null, null, false,
				null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteByDate() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Buy Potatoes");
		logicExecuteCommand(logic, "Plan some trips 1/5/2016");
		logicExecuteCommand(logic, "Fly to Japan 1/6/2016");
		logicExecuteCommand(logic, "Trip Japan Plan 1/7/2016");
		logicExecuteCommand(logic, "d < 1/6/2016");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Fly to Japan", "1/6/2016", null, null, false, null);
		storageCreateExpectedTask(storage, expectedFile, "Trip Japan Plan", "1/7/2016", null, null, false,
				null);
		storageCreateExpectedTask(storage, expectedFile, "Buy Potatoes", null, null, null, false, null);

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}

	@Test
	public final void testDeleteRecurTriggerWithIndex() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016 3d 1");
		logicExecuteCommand(logic, "d 1");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "4/5/2016", null, null, false,
				createRecur("DAY", 3, "1/5/2016", "4/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}
	
	
	@Test
	public final void testDeleteRecurTriggerWithLessThanDate() throws SAXException, ParseException, IOException {

		// Setting up actual Task List for comparison
		File testFile = new File(TEST_FILE_NAME);
		Logic logic = new Logic(testFile);
		logicExecuteCommand(logic, "Plan some trips 1/5/2016 3d 1");
		logicExecuteCommand(logic, "d < 1/6/2016");

		// Setting up expected Task List for comparison
		File expectedFile = new File(EXPECTED_FILE_NAME);
		Storage storage = new Storage(expectedFile);
		storageCreateExpectedTask(storage, expectedFile, "Plan some trips", "4/5/2016", null, null, false,
				createRecur("DAY", 3, "1/5/2016", "4/5/2016"));

		// This is to test the expected behavior of this function
		FileReader fr1 = new FileReader(expectedFile);
		FileReader fr2 = new FileReader(testFile);
		XMLAssert.assertXMLEqual(fr1, fr2);
	}
	
	

	// Currently commented out because it wipes the current task list

	// @Test
	// public final void testChangeDirectory() throws SAXException, ParseException, IOException {
	//
	// // Setting up actual Task List for comparison
	// Logic logic = new Logic();
	// logicExecuteCommand(logic, "Buy Potatoes");
	// logicExecuteCommand(logic, "s test");
	//
	// // Setting up expected Task List for comparison
	// File expectedFile = new File("test\\tasklist.xml");
	// assert (expectedFile.isFile());
	// assert (expectedFile.canRead());
	//
	// logicExecuteCommand(logic, "s /");
	// }

}
