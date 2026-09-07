# Marcus User Guide

Marcus is a task manager that supports to-dos, deadlines, events, and tags.

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

Use `deadline <description> /by <yyyy-mm-dd>` to add a deadline.

```
expected output
```

## Tagging tasks

Use `tag <task number> <tag>...` to add one or more tags to a task.

```text
tag 1 #fun #weekend
```

Tags must begin with `#`, cannot contain spaces, and are stored in lowercase.
Duplicate tags are not added. A successful command displays the updated task.

Use `untag <task number> <tag>` to remove one tag, or `untag <task number> all`
to remove every tag.

```text
untag 1 #fun
untag 1 all
```

Tags are displayed after the task description and are saved separately from
the description. Existing saved tasks without tags continue to load normally.

Use `find #fun` to search by tag, or `list #fun` to list tasks with that tag.
Normal keyword searches do not search tags.
