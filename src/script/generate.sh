#script for compressed all .class to a jar
cd /Users/rakotoharisoa/Documents/mirindra/frameworks/out/artifacts/test/WEB-INF/classes 
jar -cf m.jar .
mv ./m.jar ../lib
cd ../../
jar -cf run.war ./WEB-INF
