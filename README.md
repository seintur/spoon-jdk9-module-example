# spoon-jdk9-module-example

Fixed by https://github.com/INRIA/spoon/pull/1996

Illustrate an ClassNotFoundException when using Spoon 6.3.0-SNAPSHOT from a Java 9 project with modules.

In a nutshell, the project defines:
* a module named mod that exports a package named pkg that contains a class named C,
* C defines a main method and calls Launcher.parseClass to build a model with Spoon.

The project can be run with: mvn test

The test phase uses exec-maven-plugin to launch mod/pkg.C.main

The run fails with the following stack trace:

[INFO] --- exec-maven-plugin:1.6.0:exec (default) @ bar ---
Exception in thread "main" java.lang.ExceptionInInitializerError
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.factory.QueryFactory.createQuery(QueryFactory.java:66)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.CtModelImpl.filterChildren(CtModelImpl.java:46)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.CtModelImpl.getElements(CtModelImpl.java:138)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.CtModelImpl.getAllPackages(CtModelImpl.java:115)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.CtModelImpl.getAllTypes(CtModelImpl.java:106)
	at spoon.core@6.3.0-SNAPSHOT/spoon.Launcher.parseClass(Launcher.java:807)
	at mod/pkg.C.main(C.java:11)
Caused by: spoon.SpoonException: The class detected from ClassCastException not found.
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.visitor.chain.CtQueryImpl.detectTargetClassFromCCE(CtQueryImpl.java:616)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.visitor.chain.CtQueryImpl.getIndexOfCallerInStackOfLambda(CtQueryImpl.java:587)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.visitor.chain.CtQueryImpl.<clinit>(CtQueryImpl.java:566)
	... 7 more
Caused by: java.lang.ClassNotFoundException: spoon.core@6.3.0-SNAPSHOT/spoon.reflect.declaration.CtType
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Class.java:291)
	at spoon.core@6.3.0-SNAPSHOT/spoon.reflect.visitor.chain.CtQueryImpl.detectTargetClassFromCCE(CtQueryImpl.java:614)
	... 9 more
[ERROR] Command execution failed.

The failure originates from Class.forName that is used in spoon.reflect.visitor.chain.CtQueryImpl.detectTargetClassFromCCE(CtQueryImpl.java:614). Since this is a modularized project the class cannot be load from the usual classpath (hence the exception), but from the module path.

(note: Class.forName is used in several other locations in Spoon)

I do not have yet a solution to pull but my hint is that the class loading should be managed in a more principled way than with Class.forName.

For example, spoon.Environment already defines a pair of setter/getter for the input class loader. It remains to be seen whether Environment can be used to load classes (with Class.forName maybe as a default way). By this way, Environment can be extended to provide other class loading schemes such as the ones based on module paths.
