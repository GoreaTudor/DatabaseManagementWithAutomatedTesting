## Database Management with Automated Testing

Background: Databases are a major part of many existent applications nowadays. They are the main way to store, access and modify the data used by the application, so their management is very important for everything to work.


Objective: The main objective is to find a method to verify the functionality of our database components and to find any alteration that is made that is not foreseen in the design of the tests. The secondary objective is to modify the tests so as to evaluate the changes made.


Material and Method: The solution is implemented using the Java, along with some additional tools: Maven -a build automation tool, and JUnit -a unit testing framework. For this particular case, the database is built using MySQL. The approach is to test components starting from analyzing table operations (table/column names, inserts and deletions), then test views and joins between tables, and lastly to test the stored procedures and stored functions. The tests have to check for all scenarios, including the cases that are bound to fail.


Results: We can test the functionalities of our given database at any time, without having to test manually each component.
If an user alters the database (for example: replaces some table names, or column names, added/deleted columns, modifies some procedure or function), changes that are not foreseen in the design of the tests, we will be able to see what was changed and where, only by running the prepared set of tests. When it was concluded that the changes will are permanent, then we can adapt the tests so as to consider the changes made.


Conclusions: This method can assure us that the given database is working properly and any modification will be seen by looking at which of the test fails. When the changes are permanent, then the set of tests must change.
