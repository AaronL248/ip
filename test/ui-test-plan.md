# Console UI Test Plan

## Test Case: Exit greeting

**Aim:** Verify that the chatbot displays its initial prompt and exits politely when the user enters `bye`.

### Input
```text
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Add, mark, unmark, and list typed tasks

**Aim:** Verify that to-dos, deadlines, and events are added with the correct display format, and that their completion status can be changed.

### Input
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 1
list
unmark 1
list
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Sunday)
     Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     OK, I've marked this task as not done yet:
       [T][ ] read book
____________________________________________________________
____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Sunday)
     3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: Reject every invalid command form

**Aim:** Verify every current error-response path: incomplete task commands, unknown commands, missing status-command arguments, non-numeric task numbers, and task numbers outside the list.

### Input
```text
todo
deadline whats up
event run
banana
mark
mark two
mark 1
unmark
unmark two
unmark 1
bye
```

### Expected Output
```text
 __  __    _    ____   ____ _   _ ____ 
|  \/  |  / \  |  _ \ / ___| | | / ___|
| |\/| | / _ \ | |_) | |   | | | \___ \
| |  | |/ ___ \|  _ <| |___| |_| |___) |
|_|  |_/_/   \_\_| \_\\____|\___/|____/

Hello, I am Marcus the Chatbot!
What can I do for you?
____________________________________________________________
____________________________________________________________
     Please enter task with todo, eg. todo go for a run
____________________________________________________________
____________________________________________________________
     Please enter task with deadline, eg. deadline return book /by Sunday
____________________________________________________________
____________________________________________________________
     Please enter task with event, eg. event project meeting /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
     What do you mean by "banana", please enter a valid command
____________________________________________________________
____________________________________________________________
     What do you mean by "mark", please enter a valid command
____________________________________________________________
____________________________________________________________
     Please provide a task number to mark.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     What do you mean by "unmark", please enter a valid command
____________________________________________________________
____________________________________________________________
     Please provide a task number to unmark.
____________________________________________________________
____________________________________________________________
     That task number does not exist.
____________________________________________________________
____________________________________________________________
     Bye. Hope to see you again soon!
____________________________________________________________
```
