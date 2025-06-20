javac Compress.java
jar -cf Compress.jar Compress.class
java -cp Compress.jar Compress "..\..\..\db\graph\family.grd"
java -cp Compress.jar Compress "..\..\..\db\graph\name.grd"
java -cp Compress.jar Compress "..\..\..\db\initial\family.dat"
java -cp Compress.jar Compress "..\..\..\db\initial\name.dat"