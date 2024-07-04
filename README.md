# memory-priority
Simple JavaFX program for memorizing memory sets. Memory sets are a sequence of key value pairs. The memory sets can be ordered by Priority, and each can be rehearsed. Upon rehearsal, the reheared timestamp will update and the other memory sets within the priority level will take priority.

The "Review All Memory Sets" button will choose the most imminent memory set for rehearsal, based on priority level and rehearsal date. You may keep clicking this button to go through all your memory sets in the right order, based on the priority levels you have defined. 

## Data
This application can run fully offline. The memorys sets are simply saved in txt files, where a certain expected pattern is expected for the parsing. As long as the format is being respected, the content of these txt files may be adjusted to your liking. 

Each txt file represents a profile (which you can add or delete in the app), and in each profile txt file, you can find all the memory sets, with all their relevant data. 

This provides great flexibility and control over your memorization data.

## How to run?
```
./gradlew run
```

## Known Bugs
- If you add duplicate key-value pairs, identical keys, or identical values to a memory set, the program will crash when you attempt to rehearse that set.
