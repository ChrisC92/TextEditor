# TextEditor

Plain text editor that where classes have been made for features of 
- Supporting clicking
- Saving and loading files
- Word Wrapping depending on size of the window 

Rending of text uses two data structures - A double linked list that handles insertion and deletion efficiently however
the use of only a linked list can cause this to be inefficient with larger files so there is also a class for tracking the lines 
which is a resizable array deque
