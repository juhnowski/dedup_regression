javac CompressRows.java
jar -cf CompressRows.jar CompressRows.class
java -cp CompressRows.jar CompressRows "..\..\..\db\graph\family.grd"
java -cp CompressRows.jar CompressRows "..\..\..\db\graph\name.grd"
java -cp CompressRows.jar CompressRows "..\..\..\db\initial\family.dat"
java -cp CompressRows.jar CompressRows "..\..\..\db\initial\name.dat"