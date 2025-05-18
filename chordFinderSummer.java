import java.util.Scanner;

// add a method that generates a chord progression within the key
// add a voicing calculator key that randomizes the order of one chord and then finds the shortest distance to the other notes from there
public class chordFinderSummer {
    private static Scanner sc = new Scanner(System.in);

    // main method: introduces chordFinder, calls getKey & getMode, intializes sharp or flat array
    public static void main(String[] args) {
        System.out.println("Welcome to chordFinder. This program is supported for diatonic triads in standard major & minor keys.");
        String key = getKey();
        String mode = getMode();
        String[] noteArray;
        if ((key.length() >= 2 && key.charAt(1) == 'b') || (key.equals("f") && mode.equals("major"))) { // not complete, causing issues with semitones (Ex: c minor)
            noteArray = new String[] {"a", "bb", "b", "c", "db", "d", "eb", "e", "f", "gb", "g", "ab"};
        }
        else {
            noteArray = new String[] {"a", "a#", "b", "c", "c#", "d", "d#", "e", "f", "f#", "g", "g#"};
        }
        menu(key, mode, noteArray);
    }

    // gets the key signature from user
    public static String getKey() {
        System.out.println("Enter the key of choice (do not include mode): ");
        String key = sc.nextLine().toLowerCase();  
        while (
            (key.charAt(0) < 'a' || key.charAt(0) > 'g') || 
            (key.length() > 2 || key.length() <= 0) || 
            (key.length() == 2 && (key.charAt(1) != '#' && key.charAt(1) !='b'))
            ) {
                System.out.println("error: enter the key of choice (do not include mode): ");
                key = sc.nextLine().toLowerCase();  
        }
        return key;
    }

    // takes the mode (currently major or minor) from the user
    public static String getMode() {
        System.out.println("Enter the mode (major or minor): ");
        String mode = sc.nextLine().toLowerCase(); 
        while (!mode.equals("major") && !mode.equals("minor")) {
            System.out.println("error: try again");
            System.out.println("Enter the mode (major or minor): ");
            mode = sc.nextLine().toLowerCase(); 

        }
        return mode;
    }


    // creates a menu for options
    // add a error message here for if the option chosen is not 1-4
    public static void menu(String key, String mode, String[] noteArray) {
        System.out.println("");
        System.out.println("MENU " + "(key selected: " + key + " " + mode + ")");
        System.out.println("1. print the key");
        System.out.println("2. print the notes of a chord");
        System.out.println("3. find the number of semitones between two notes");
        System.out.println("4. quit");
        System.out.println("What would you like to find? Enter the number here: ");
        int menuChoice = Integer.parseInt(sc.nextLine());
        if (menuChoice <1 || menuChoice > 5) {
            System.out.println("error: please choose an option");
            displayMenu(key, mode, noteArray);
        }
        // actions based on the menu choice 1, prints the key signature
        if (menuChoice == 1) {
            String[] keyArray = keyArray(key, mode, noteArray);
            System.out.println(" ");
            System.out.println("Key of "+ key + " " + mode + ":");
            for (int i = 0; i < 7; i++){
                System.out.println(keyArray[i]);
            }
            displayMenu(key, mode, noteArray);
        }
        // based on choice 2, prints the notes of the chord
        if (menuChoice == 2) {
            String[] keyArray = keyArray(key, mode, noteArray);
            String[] chordArray = chordArray(keyArray, noteArray, mode);
            System.out.println(" ");
            for (int i = 0; i < 4; i++) {
                System.out.println(chordArray[i]);
            }
            displayMenu(key, mode, noteArray);
        }
        //based on choice 3, number of semitones
        if (menuChoice == 3) {
            int note1Index = 0;
            int note2Index = 0;
            // input- could prob be its own function
            System.out.println("Enter the first note (lower): ");
            String note1 = sc.nextLine();
            note1 = note1.toLowerCase();
            System.out.println("Enter the second note (higher): ");
            String note2 = sc.nextLine();
            note2 = note2.toLowerCase();
            // also get the index within the key, could be useful for actual intervals (use the semitones then key difference to find)
            for (int i = 0; i < noteArray.length; i++) {
                if (noteArray[i].equals(note1)){
                    note1Index = i;
                }
                else if (noteArray[i].equals(note2)){
                    note2Index = i;
                }
            }
            int interval = intervalFinder(note1Index, note2Index);
            if (interval != 1){
                System.out.println ("There are " + interval + " semitones between " + note1 + " and " + note2 + "." );
            }
            else {
                System.out.println ("There is " + interval + " semitone between " + note1 + " and " + note2 + "." );
            }
            displayMenu(key, mode, noteArray);
        }
        // menu choice 4, quits the program
        if (menuChoice == 4) {
            System.exit(1);
        }
        if (menuChoice == 5) {
            String[] keyArray = keyArray(key, mode, noteArray);
            String[][] chordProgression = chordProgression(keyArray, noteArray, mode);
        }
    }
  
    // creates a chord array: index 0 is chord name, indexes 1-3 are the notes of the chord. calls chordType
    public static String[] chordArray(String[] keyArray, String[] noteArray, String mode) {
        String[] chordArray;
        String updatedMode = " ";
        System.out.println("Enter the scale degree of the root note: ");
        int scaleDegree = Integer.parseInt(sc.nextLine());
        int scaleIndex = scaleDegree-1;
        chordArray = new String[] {" ", keyArray[scaleIndex], keyArray[(scaleIndex+2) % keyArray.length], keyArray[(scaleIndex+4) % keyArray.length]};
        int[] intervals = chordType(noteArray, chordArray);
        if (intervals[0] == 4 && intervals[1] == 3) {
            updatedMode = "major";
        }
        
        else if (intervals[0] == 3 && intervals[1] == 3) {
            updatedMode = "diminished";
        }
        else if (intervals[0] == 3 && intervals[1] == 4) {
            updatedMode = "minor";
        }
        else if (intervals[0] == 4 && intervals[1] == 4) {
            updatedMode = "augmented";
        }
        String chordName = keyArray[scaleIndex] + " " + updatedMode + ":";
        chordArray[0] = chordName;
        return chordArray;
    }

    /**************************************************************************************************************
    changes the array so index 0 is the starting note of the key. Returns the new version of noteArr, notesInKey.
    checks for whether the key is major or minor, and then goes through the values of the array and if there is a # 
    when there should be a b, it changes it.
    *****************************************************************************************************************/ 
    public static String[] keyArray (String key, String mode, String[] noteArray) {
        // creates the offset 
        int offset = 0;
        String[] keyArray = new String[7];
        for (int i = 0; i < 12; i++) { 
            if (noteArray[i].equals(key)) {
                int startingNoteIndex = i; //finds the index of the starting note in the original array
                offset = startingNoteIndex;
            }
        }
        // takes the notes from noteArray and assigns the correct notes (according to mode's pattern) to a new array
        int[] patternIndexArray = new int[7];
        if (mode.equals("major")) { 
            int[] patternIndex = {0, 2, 4, 5, 7, 9, 11}; // major scale pattern
            for (int i = 0; i < patternIndexArray.length; i++){
                patternIndexArray[i] = patternIndex[i];
            }
        }
        else if (mode.equals("minor")) {
            int[] patternIndex = {0, 2, 3, 5, 7, 8, 10}; // minor scale pattern
            for (int i = 0; i < patternIndexArray.length; i++){
                patternIndexArray[i] = patternIndex[i];
            }
        }
        for (int i = 0; i < keyArray.length; i++) {
            keyArray[i] = noteArray[(patternIndexArray[i] + offset) % noteArray.length];
        }
        return keyArray;
    }

    public static int[] chordType(String[] noteArray, String[] chordArray){
        // returns the interval in semitones between the notes in the chord
        int note1Index = 0;
        int note2Index = 0;
        int note3Index = 0;
        for (int i = 0; i < noteArray.length; i++) {
            if (noteArray[i].equals(chordArray[1])){
                note1Index = i;
            }
            else if (noteArray[i].equals(chordArray[2])) {
                note2Index = i;
            }
            else if (noteArray[i].equals(chordArray[3])) {
                note3Index = i;
            }
        } 
        int[] intervalArr = {intervalFinder(note1Index, note2Index), intervalFinder(note2Index, note3Index)};
        return intervalArr;
    }

    // finds the interval between two given notes (adjusts higher note if it is not higher in noteArray)
    public static int intervalFinder(int note1Index, int note2Index){
        if (note2Index-note1Index <=0) {
            note2Index += 12;
        }
        int interval = note2Index-note1Index;
        return interval;
    }

    // displays the menu
    public static void displayMenu(String key, String mode, String[] noteArray) {
        System.out.println("Press enter to return to the menu, or type "+"QUIT"+" to quit.");
        String returnToMenu = sc.nextLine();
        if (returnToMenu.equalsIgnoreCase("QUIT")) {
            System.exit(0);
        }
        else {
            menu(key, mode, noteArray);
        }
    }

    // finds a chord progression that you input, sorts into a two d array
    // could be altered so you type the number of chords
    // could also have an option for a random chord w some changes to chordArray
    // asks for octave input and gives you the voicings with the octaves labeled
    // finds the shortest path that exists between any two notes in the chord (semitone basis) 
    //    and then adjust the others in close paths but not all in the same direction, consider common tones
    // like for CEG to FAC the E should go to F bc its shortest, then C can stay the same and G can go the other way if within 3 paths but its not so
    public static String[][] chordProgression(String[] keyArray, String[] noteArray, String mode) {
        String[][] chordProg = new String[4][];
        String[] currentChord;
        for (int i = 0; i<4; i ++) {
            System.out.println("chord " + (i + 1) + ":");
            currentChord = chordArray(keyArray, noteArray, mode);
            chordProg[i] = new String[currentChord.length];
            for (int j = 0; j < currentChord.length; j++) {
                chordProg[i][j] = currentChord[j];
            }
        }
        for (int i = 0; i<chordProg.length; i++){
            for (int j = 0; j < chordProg[i].length; j++) {
                System.out.println(chordProg[i][j]);
            }
        }

        System.out.println("create voicings? Press 1 for yes, 2 for no");
        int voicingChoice = Integer.parseInt(sc.nextLine());
        String temp;
        if (voicingChoice == 1) {
            //find common tones 
            for (int i = 0; i < chordProg.length; i++) {
                for (int j = 0; j < chordProg[i].length; j++) {
                    for (int k = 0; i+1 < chordProg.length && k < chordProg[i+1].length; k++) {
                        if (chordProg[i][j] == chordProg[i+1][k]) {
                            System.out.println("common tone");
                            // swaps the note in the next chord so it is in line
                            temp = chordProg[i+1][k];
                            chordProg[i+1][k] = chordProg[i+1][j];
                            chordProg[i+1][j] = temp;                        
                        }
                        //else if 
                    }
                }
            }
            for (int i = 0; i<chordProg.length; i++){
                for (int j = 0; j < chordProg[i].length; j++) {
                    System.out.println(chordProg[i][j]);
                }   
            }
        }
        return chordProg;
    }
}


