# Data Files

Keep data files for each project here, under separate subdirectories named by project. Try to avoid storing large
files (~1GB or more) in Git by adding them to your `.gitignore` file.

Design Q:

Suppose that in addition to the commands specified in Command Line/REPL Specification, you wanted to support 10+ more
commands. How would you change your code - particularly your REPL parsing - to do this? Don't worry about specific
algorithmic details; we're interested in the higher-level design.