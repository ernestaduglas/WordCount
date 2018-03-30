WordCount app

The user can upload multiple files. The app counts how many times same word is repeated in all files. App accepts txt files.

The app works as follows:

-on start wordcount directory with two sub-directories(uploaded and completed) is created in user's Home directory;

-user interface is accessible via http://localhost:8080/wordcount

-when user chooses files and hits "count": 

-uploaded files are stored in "uploaded" sub- directory;

-for every file thread that counts words frequency is called. Result is stored in a Map. Threads are managed by ThreadPoolTaskExecutor. 

-all results are merged into one global Map;

-four threads, that sort Map based on word's starting and ending character, are called in order to split the global Map into predefined groups of A-G, H-N, O-U and V-Z.  Same threads also save each group of the splited Map into a separate file. Files are stored in "completed" sub-directory;

-completed files are zipped and provided back to the user as a direct attachment download.

